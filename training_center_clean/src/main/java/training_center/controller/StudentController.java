package training_center.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import training_center.dao.CourseDao;
import training_center.dao.ScheduleDao;
import training_center.dao.StudentDao;
import training_center.dao.TeacherDao;
import training_center.entity.Course;
import training_center.entity.Schedule;
import training_center.entity.Student;
import training_center.entity.Teacher;

@Controller
public class StudentController {

    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private final ScheduleDao scheduleDao;
    private final TeacherDao teacherDao;

    public StudentController(StudentDao studentDao,
                             CourseDao courseDao,
                             ScheduleDao scheduleDao,
                             TeacherDao teacherDao) {
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.scheduleDao = scheduleDao;
        this.teacherDao = teacherDao;
    }

    @GetMapping("/students")
    public String students(@RequestParam(value = "fullName", required = false) String fullName, Model model) {
        List<Student> students;

        if (fullName == null || fullName.isBlank()) {
            students = studentDao.findAll();
        } else {
            students = studentDao.findByFullName(fullName.trim());
        }

        model.addAttribute("students", students);
        model.addAttribute("fullNameFilter", fullName == null ? "" : fullName);
        return "students";
    }

    @GetMapping("/students/new")
    public String newStudentForm(Model model) {
        prepareStudentForm(model, new Student(), "Добавить обучающегося", "/students", "Сохранить", "/students");
        return "student-form";
    }

    @PostMapping("/students")
    public String createStudent(@RequestParam("fullName") String fullName, Model model) {
        try {
            studentDao.save(new Student(requireFullName(fullName)));
            return "redirect:/students";
        } catch (RuntimeException ex) {
            Student student = new Student();
            student.setFullName(fullName);
            prepareStudentForm(model, student, "Добавить обучающегося", "/students", "Сохранить", "/students");
            model.addAttribute("errorMessage", ex.getMessage());
            return "student-form";
        }
    }

    @GetMapping("/students/{id}")
    public String studentDetails(@PathVariable("id") Long id, Model model) {
        Student student = studentDao.findById(id);
        if (student == null) {
            return "redirect:/students";
        }

        fillStudentDetails(model, student);
        return "student-details";
    }

    @GetMapping("/students/{id}/edit")
    public String editStudentForm(@PathVariable("id") Long id, Model model) {
        Student student = studentDao.findById(id);
        if (student == null) {
            return "redirect:/students";
        }

        prepareStudentForm(
                model,
                student,
                "Редактировать обучающегося",
                "/students/" + id + "/update",
                "Сохранить",
                "/students/" + id
        );
        return "student-form";
    }

    @PostMapping("/students/{id}/update")
    public String updateStudent(@PathVariable("id") Long id,
                                @RequestParam("fullName") String fullName,
                                Model model) {
        Student student = studentDao.findById(id);
        if (student == null) {
            return "redirect:/students";
        }

        try {
            student.setFullName(requireFullName(fullName));
            studentDao.update(student);
            return "redirect:/students/" + id;
        } catch (RuntimeException ex) {
            student.setFullName(fullName);
            prepareStudentForm(
                    model,
                    student,
                    "Редактировать обучающегося",
                    "/students/" + id + "/update",
                    "Сохранить",
                    "/students/" + id
            );
            model.addAttribute("errorMessage", ex.getMessage());
            return "student-form";
        }
    }

    @PostMapping("/students/{id}/delete")
    public String deleteStudent(@PathVariable("id") Long id, Model model) {
        try {
            studentDao.deleteById(id);
            return "redirect:/students";
        } catch (RuntimeException ex) {
            Student student = studentDao.findById(id);
            if (student == null) {
                return "redirect:/students";
            }

            fillStudentDetails(model, student);
            model.addAttribute("errorMessage", "Не удалось удалить обучающегося");
            return "student-details";
        }
    }

    @GetMapping("/students/{id}/schedule")
    public String studentSchedule(@PathVariable("id") Long id,
                                  @RequestParam(value = "from", required = false) String from,
                                  @RequestParam(value = "to", required = false) String to,
                                  Model model) {
        Student student = studentDao.findById(id);
        if (student == null) {
            return "redirect:/students";
        }

        model.addAttribute("student", student);
        model.addAttribute("from", from == null ? "" : from);
        model.addAttribute("to", to == null ? "" : to);
        model.addAttribute("schedule", Collections.emptyList());
        model.addAttribute("scheduleCourseTitles", Collections.emptyMap());
        model.addAttribute("scheduleTeacherNames", Collections.emptyMap());

        if (from != null && !from.isBlank() && to != null && !to.isBlank()) {
            try {
                OffsetDateTime fromDate = toOffsetDateTime(parseDate(from), LocalTime.MIN);
                OffsetDateTime toDate = toOffsetDateTime(parseDate(to), LocalTime.of(23, 59, 59));
                List<Schedule> schedule = scheduleDao.findByStudentAndInterval(id, fromDate, toDate);

                model.addAttribute("schedule", schedule);
                model.addAttribute("scheduleCourseTitles", resolveCourseTitlesForSchedule(schedule));
                model.addAttribute("scheduleTeacherNames", resolveTeacherNamesForSchedule(schedule));
            } catch (RuntimeException ex) {
                model.addAttribute("errorMessage", ex.getMessage());
            }
        } else if ((from != null && !from.isBlank()) || (to != null && !to.isBlank())) {
            model.addAttribute("errorMessage", "Укажите обе даты интервала");
        }

        return "student-schedule";
    }

    private void fillStudentDetails(Model model, Student student) {
        model.addAttribute("student", student);
        model.addAttribute("courseHistory", studentDao.getCourseHistory(student.getId()));
        model.addAttribute("currentCourses", resolveCurrentCourses(student.getId()));
    }

    private List<Course> resolveCurrentCourses(Long studentId) {
        OffsetDateTime now = OffsetDateTime.now();
        List<Schedule> schedule = scheduleDao.findByStudentAndInterval(studentId, now.minusYears(10), now.plusYears(10));
        Map<Long, Course> courses = new LinkedHashMap<>();

        for (Schedule item : schedule) {
            if (item.getEndAt() != null && item.getEndAt().isAfter(now) && item.getCourse() != null) {
                Long courseId = item.getCourse().getId();
                Course course = courseDao.findById(courseId);
                if (course != null) {
                    courses.put(courseId, course);
                }
            }
        }

        return new ArrayList<>(courses.values());
    }

    private Map<Long, String> resolveCourseTitlesForSchedule(List<Schedule> schedule) {
        Map<Long, String> courseTitles = new LinkedHashMap<>();

        for (Schedule item : schedule) {
            Long courseId = item.getCourse() != null ? item.getCourse().getId() : null;
            Course course = courseId != null ? courseDao.findById(courseId) : null;
            courseTitles.put(item.getId(), course != null ? course.getTitle() : "");
        }

        return courseTitles;
    }

    private Map<Long, String> resolveTeacherNamesForSchedule(List<Schedule> schedule) {
        Map<Long, String> teacherNames = new LinkedHashMap<>();

        for (Schedule item : schedule) {
            Long teacherId = item.getTeacher() != null ? item.getTeacher().getId() : null;
            Teacher teacher = teacherId != null ? teacherDao.findById(teacherId) : null;
            teacherNames.put(item.getId(), teacher != null ? teacher.getFullName() : "");
        }

        return teacherNames;
    }

    private void prepareStudentForm(Model model,
                                    Student student,
                                    String formTitle,
                                    String formAction,
                                    String submitLabel,
                                    String cancelUrl) {
        model.addAttribute("student", student);
        model.addAttribute("formTitle", formTitle);
        model.addAttribute("formAction", formAction);
        model.addAttribute("submitLabel", submitLabel);
        model.addAttribute("cancelUrl", cancelUrl);
    }

    private String requireFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new RuntimeException("Введите ФИО обучающегося");
        }

        return fullName.trim();
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new RuntimeException("Некорректная дата");
        }
    }

    private OffsetDateTime toOffsetDateTime(LocalDate date, LocalTime time) {
        LocalDateTime dateTime = date.atTime(time);
        return dateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }
}
