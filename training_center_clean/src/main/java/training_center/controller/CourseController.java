package training_center.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
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
import training_center.entity.Company;
import training_center.entity.Course;
import training_center.entity.Schedule;
import training_center.entity.Student;
import training_center.entity.Teacher;
import training_center.service.CompanyService;

@Controller
public class CourseController {

    private final CourseDao courseDao;
    private final StudentDao studentDao;
    private final TeacherDao teacherDao;
    private final ScheduleDao scheduleDao;
    private final CompanyService companyService;

    public CourseController(CourseDao courseDao,
                            StudentDao studentDao,
                            TeacherDao teacherDao,
                            ScheduleDao scheduleDao,
                            CompanyService companyService) {
        this.courseDao = courseDao;
        this.studentDao = studentDao;
        this.teacherDao = teacherDao;
        this.scheduleDao = scheduleDao;
        this.companyService = companyService;
    }

    @GetMapping("/courses")
    public String courses(@RequestParam(value = "title", required = false) String title, Model model) {
        List<Course> courses;

        if (title == null || title.isBlank()) {
            courses = courseDao.findAll();
        } else {
            courses = courseDao.findByTitle(title.trim());
        }

        model.addAttribute("courses", courses);
        model.addAttribute("titleFilter", title == null ? "" : title);
        model.addAttribute("companyNames", resolveCompanyNamesForCourses(courses));
        return "courses";
    }

    @GetMapping("/courses/new")
    public String newCourseForm(Model model) {
        prepareCourseForm(model, new Course(), null, "Добавить курс", "/courses", "Сохранить", "/courses");
        return "course-form";
    }

    @PostMapping("/courses")
    public String createCourse(@RequestParam("title") String title,
                               @RequestParam("durationValue") Integer durationValue,
                               @RequestParam("intensity") Integer intensity,
                               @RequestParam("companyId") Long companyId,
                               Model model) {
        try {
            Company company = requireCompany(companyId);
            Course course = new Course(
                    requireTitle(title),
                    "days",
                    requirePositiveNumber(durationValue, "Введите корректную длительность в днях"),
                    requirePositiveNumber(intensity, "Введите корректную интенсивность"),
                    company
            );

            courseDao.save(course);
            return "redirect:/courses";
        } catch (RuntimeException ex) {
            Course course = buildCourseFormObject(null, title, "days", durationValue, intensity);
            prepareCourseForm(model, course, companyId, "Добавить курс", "/courses", "Сохранить", "/courses");
            model.addAttribute("errorMessage", ex.getMessage());
            return "course-form";
        }
    }

    @GetMapping("/courses/{id}")
    public String courseDetails(@PathVariable("id") Long id, Model model) {
        Course course = courseDao.findById(id);
        if (course == null) {
            return "redirect:/courses";
        }

        fillCourseDetails(model, course);
        return "course-details";
    }

    @GetMapping("/courses/{id}/edit")
    public String editCourseForm(@PathVariable("id") Long id, Model model) {
        Course course = courseDao.findById(id);
        if (course == null) {
            return "redirect:/courses";
        }

        Long selectedCompanyId = course.getCompany() != null ? course.getCompany().getId() : null;
        prepareCourseForm(
                model,
                course,
                selectedCompanyId,
                "Редактировать курс",
                "/courses/" + id + "/update",
                "Сохранить",
                "/courses/" + id
        );
        return "course-form";
    }

    @PostMapping("/courses/{id}/update")
    public String updateCourse(@PathVariable("id") Long id,
                               @RequestParam("title") String title,
                               @RequestParam("durationValue") Integer durationValue,
                               @RequestParam("intensity") Integer intensity,
                               @RequestParam("companyId") Long companyId,
                               Model model) {
        Course course = courseDao.findById(id);
        if (course == null) {
            return "redirect:/courses";
        }

        try {
            course.setTitle(requireTitle(title));
            course.setDurationUnit("days");
            course.setDurationValue(requirePositiveNumber(durationValue, "Введите корректную длительность в днях"));
            course.setIntensity(requirePositiveNumber(intensity, "Введите корректную интенсивность"));
            course.setCompany(requireCompany(companyId));

            courseDao.update(course);
            return "redirect:/courses/" + id;
        } catch (RuntimeException ex) {
            Course formCourse = buildCourseFormObject(id, title, "days", durationValue, intensity);
            prepareCourseForm(
                    model,
                    formCourse,
                    companyId,
                    "Редактировать курс",
                    "/courses/" + id + "/update",
                    "Сохранить",
                    "/courses/" + id
            );
            model.addAttribute("errorMessage", ex.getMessage());
            return "course-form";
        }
    }

    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable("id") Long id, Model model) {
        try {
            courseDao.deleteById(id);
            return "redirect:/courses";
        } catch (RuntimeException ex) {
            Course course = courseDao.findById(id);
            if (course == null) {
                return "redirect:/courses";
            }

            fillCourseDetails(model, course);
            model.addAttribute("errorMessage", "Не удалось удалить курс");
            return "course-details";
        }
    }

    @GetMapping("/courses/{id}/add-student")
    public String addStudentForm(@PathVariable("id") Long id, Model model) {
        Course course = courseDao.findById(id);
        if (course == null) {
            return "redirect:/courses";
        }

        prepareAddStudentForm(model, course, null);
        return "course-add-student";
    }

    @PostMapping("/courses/{id}/students")
    public String addStudentToCourse(@PathVariable("id") Long id,
                                     @RequestParam("studentId") Long studentId,
                                     Model model) {
        Course course = courseDao.findById(id);
        if (course == null) {
            return "redirect:/courses";
        }

        try {
            requireStudent(studentId);
            courseDao.addStudentToCourse(id, studentId);
            return "redirect:/courses/" + id;
        } catch (RuntimeException ex) {
            prepareAddStudentForm(model, course, studentId);
            model.addAttribute("errorMessage", "Не удалось добавить обучающегося в курс");
            return "course-add-student";
        }
    }

    @PostMapping("/courses/{id}/students/{studentId}/delete")
    public String removeStudentFromCourse(@PathVariable("id") Long id,
                                          @PathVariable("studentId") Long studentId) {
        courseDao.removeStudentFromCourse(id, studentId);
        return "redirect:/courses/" + id;
    }

    @GetMapping("/courses/{id}/add-teacher")
    public String addTeacherForm(@PathVariable("id") Long id, Model model) {
        Course course = courseDao.findById(id);
        if (course == null) {
            return "redirect:/courses";
        }

        prepareAddTeacherForm(model, course, null);
        return "course-add-teacher";
    }

    @PostMapping("/courses/{id}/teachers")
    public String addTeacherToCourse(@PathVariable("id") Long id,
                                     @RequestParam("teacherId") Long teacherId,
                                     Model model) {
        Course course = courseDao.findById(id);
        if (course == null) {
            return "redirect:/courses";
        }

        try {
            requireTeacher(teacherId);
            courseDao.assignTeacherToCourse(id, teacherId);
            return "redirect:/courses/" + id;
        } catch (RuntimeException ex) {
            prepareAddTeacherForm(model, course, teacherId);
            model.addAttribute("errorMessage", "Не удалось назначить преподавателя на курс");
            return "course-add-teacher";
        }
    }

    @PostMapping("/courses/{id}/teachers/{teacherId}/delete")
    public String removeTeacherFromCourse(@PathVariable("id") Long id,
                                          @PathVariable("teacherId") Long teacherId) {
        courseDao.removeTeacherFromCourse(id, teacherId);
        return "redirect:/courses/" + id;
    }

    @GetMapping("/courses/{id}/schedule/new")
    public String newLessonForm(@PathVariable("id") Long id, Model model) {
        Course course = courseDao.findById(id);
        if (course == null) {
            return "redirect:/courses";
        }

        prepareLessonForm(model, course, null, "", "", "");
        return "lesson-form";
    }

    @PostMapping("/courses/{id}/schedule")
    public String createLesson(@PathVariable("id") Long id,
                               @RequestParam("teacherId") Long teacherId,
                               @RequestParam("lessonDate") String lessonDate,
                               @RequestParam("startTime") String startTime,
                               @RequestParam("endTime") String endTime,
                               Model model) {
        Course course = courseDao.findById(id);
        if (course == null) {
            return "redirect:/courses";
        }

        try {
            requireTeacher(teacherId);
            ensureTeacherAssignedToCourse(id, teacherId);

            OffsetDateTime startAt = toOffsetDateTime(parseDate(lessonDate), parseTime(startTime));
            OffsetDateTime endAt = toOffsetDateTime(parseDate(lessonDate), parseTime(endTime));

            if (!endAt.isAfter(startAt)) {
                throw new RuntimeException("Время окончания должно быть позже времени начала");
            }

            scheduleDao.saveLesson(id, teacherId, startAt, endAt);
            return "redirect:/courses/" + id;
        } catch (RuntimeException ex) {
            prepareLessonForm(model, course, teacherId, lessonDate, startTime, endTime);
            model.addAttribute("errorMessage", ex.getMessage());
            return "lesson-form";
        }
    }

    private void fillCourseDetails(Model model, Course course) {
        List<Student> students = courseDao.getStudentsByCourseId(course.getId());
        List<Teacher> teachers = courseDao.getTeachersByCourseId(course.getId());
        List<Schedule> schedule = courseDao.getScheduleByCourseId(course.getId());

        model.addAttribute("course", course);
        model.addAttribute("companyName", resolveCompanyName(course));
        model.addAttribute("students", students);
        model.addAttribute("teachers", teachers);
        model.addAttribute("schedule", schedule);
        model.addAttribute("scheduleTeacherNames", resolveTeacherNamesForSchedule(schedule));
    }

    private void prepareCourseForm(Model model,
                                   Course course,
                                   Long selectedCompanyId,
                                   String formTitle,
                                   String formAction,
                                   String submitLabel,
                                   String cancelUrl) {
        model.addAttribute("course", course);
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("selectedCompanyId", selectedCompanyId);
        model.addAttribute("formTitle", formTitle);
        model.addAttribute("formAction", formAction);
        model.addAttribute("submitLabel", submitLabel);
        model.addAttribute("cancelUrl", cancelUrl);
    }

    private void prepareAddStudentForm(Model model, Course course, Long selectedStudentId) {
        model.addAttribute("course", course);
        model.addAttribute("students", studentDao.findAll());
        model.addAttribute("selectedStudentId", selectedStudentId);
    }

    private void prepareAddTeacherForm(Model model, Course course, Long selectedTeacherId) {
        List<Teacher> teachers = teacherDao.findAll();

        model.addAttribute("course", course);
        model.addAttribute("teachers", teachers);
        model.addAttribute("selectedTeacherId", selectedTeacherId);
        model.addAttribute("teacherCompanyNames", resolveCompanyNamesForTeachers(teachers));
    }

    private void prepareLessonForm(Model model,
                                   Course course,
                                   Long selectedTeacherId,
                                   String lessonDate,
                                   String startTime,
                                   String endTime) {
        model.addAttribute("course", course);
        model.addAttribute("teachers", courseDao.getTeachersByCourseId(course.getId()));
        model.addAttribute("selectedTeacherId", selectedTeacherId);
        model.addAttribute("lessonDate", lessonDate);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);
    }

    private Map<Long, String> resolveCompanyNamesForCourses(List<Course> courses) {
        Map<Long, String> companyNames = new LinkedHashMap<>();

        for (Course course : courses) {
            companyNames.put(course.getId(), resolveCompanyName(course));
        }

        return companyNames;
    }

    private Map<Long, String> resolveCompanyNamesForTeachers(List<Teacher> teachers) {
        Map<Long, String> companyNames = new LinkedHashMap<>();

        for (Teacher teacher : teachers) {
            Long companyId = teacher.getCompany() != null ? teacher.getCompany().getId() : null;
            Company company = companyId != null ? companyService.getCompanyById(companyId) : null;
            companyNames.put(teacher.getId(), company != null ? company.getName() : "");
        }

        return companyNames;
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

    private String resolveCompanyName(Course course) {
        if (course == null || course.getCompany() == null) {
            return "";
        }

        Company company = companyService.getCompanyById(course.getCompany().getId());
        return company != null ? company.getName() : "";
    }

    private Course buildCourseFormObject(Long id,
                                         String title,
                                         String durationUnit,
                                         Integer durationValue,
                                         Integer intensity) {
        Course course = new Course();
        course.setId(id);
        course.setTitle(title);
        course.setDurationUnit(durationUnit);
        course.setDurationValue(durationValue);
        course.setIntensity(intensity);
        return course;
    }

    private Company requireCompany(Long companyId) {
        Company company = companyService.getCompanyById(companyId);
        if (company == null) {
            throw new RuntimeException("Компания не найдена");
        }

        return company;
    }

    private Student requireStudent(Long studentId) {
        Student student = studentDao.findById(studentId);
        if (student == null) {
            throw new RuntimeException("Обучающийся не найден");
        }

        return student;
    }

    private Teacher requireTeacher(Long teacherId) {
        Teacher teacher = teacherDao.findById(teacherId);
        if (teacher == null) {
            throw new RuntimeException("Преподаватель не найден");
        }

        return teacher;
    }

    private void ensureTeacherAssignedToCourse(Long courseId, Long teacherId) {
        List<Teacher> teachers = courseDao.getTeachersByCourseId(courseId);

        for (Teacher teacher : teachers) {
            if (teacherId.equals(teacher.getId())) {
                return;
            }
        }

        throw new RuntimeException("Преподаватель не назначен на этот курс");
    }

    private String requireTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new RuntimeException("Введите название курса");
        }

        return title.trim();
    }

    private Integer requirePositiveNumber(Integer value, String message) {
        if (value == null || value <= 0) {
            throw new RuntimeException(message);
        }

        return value;
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            throw new RuntimeException("Выберите дату");
        }

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new RuntimeException("Некорректная дата");
        }
    }

    private LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) {
            throw new RuntimeException("Выберите время");
        }

        try {
            return LocalTime.parse(value);
        } catch (DateTimeParseException ex) {
            throw new RuntimeException("Некорректное время");
        }
    }

    private OffsetDateTime toOffsetDateTime(LocalDate date, LocalTime time) {
        LocalDateTime dateTime = date.atTime(time);
        return dateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }
}
