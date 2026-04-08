package training_center.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
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
import training_center.dao.TeacherDao;
import training_center.entity.Company;
import training_center.entity.Course;
import training_center.entity.Teacher;
import training_center.service.CompanyService;

@Controller
public class TeacherController {

    private final TeacherDao teacherDao;
    private final CourseDao courseDao;
    private final ScheduleDao scheduleDao;
    private final CompanyService companyService;

    public TeacherController(TeacherDao teacherDao,
                             CourseDao courseDao,
                             ScheduleDao scheduleDao,
                             CompanyService companyService) {
        this.teacherDao = teacherDao;
        this.courseDao = courseDao;
        this.scheduleDao = scheduleDao;
        this.companyService = companyService;
    }

    @GetMapping("/teachers")
    public String teachers(@RequestParam(value = "courseId", required = false) Long courseId, Model model) {
        List<Teacher> teachers = courseId == null ? teacherDao.findAll() : teacherDao.findByCourseId(courseId);
        Map<Long, String> teacherCourses = new LinkedHashMap<>();

        for (Teacher teacher : teachers) {
            teacherCourses.put(teacher.getId(), joinCourseTitles(teacherDao.getCoursesByTeacherId(teacher.getId())));
        }

        model.addAttribute("teachers", teachers);
        model.addAttribute("teacherCourses", teacherCourses);
        model.addAttribute("teacherCompanyNames", resolveCompanyNamesForTeachers(teachers));
        model.addAttribute("courses", courseDao.findAll());
        model.addAttribute("selectedCourseId", courseId);
        return "teachers";
    }

    @GetMapping("/teachers/new")
    public String newTeacherForm(Model model) {
        prepareTeacherForm(model, new Teacher(), null, "Добавить преподавателя", "/teachers", "Сохранить", "/teachers");
        return "teacher-form";
    }

    @PostMapping("/teachers")
    public String createTeacher(@RequestParam("fullName") String fullName,
                                @RequestParam("companyId") Long companyId,
                                Model model) {
        try {
            teacherDao.save(new Teacher(requireFullName(fullName), requireCompany(companyId)));
            return "redirect:/teachers";
        } catch (RuntimeException ex) {
            Teacher teacher = new Teacher();
            teacher.setFullName(fullName);
            prepareTeacherForm(model, teacher, companyId, "Добавить преподавателя", "/teachers", "Сохранить", "/teachers");
            model.addAttribute("errorMessage", ex.getMessage());
            return "teacher-form";
        }
    }

    @GetMapping("/teachers/{id}")
    public String teacherDetails(@PathVariable("id") Long id, Model model) {
        Teacher teacher = teacherDao.findById(id);
        if (teacher == null) {
            return "redirect:/teachers";
        }

        fillTeacherDetails(model, teacher);
        return "teacher-details";
    }

    @GetMapping("/teachers/{id}/edit")
    public String editTeacherForm(@PathVariable("id") Long id, Model model) {
        Teacher teacher = teacherDao.findById(id);
        if (teacher == null) {
            return "redirect:/teachers";
        }

        Long selectedCompanyId = teacher.getCompany() != null ? teacher.getCompany().getId() : null;
        prepareTeacherForm(
                model,
                teacher,
                selectedCompanyId,
                "Редактировать преподавателя",
                "/teachers/" + id + "/update",
                "Сохранить",
                "/teachers/" + id
        );
        return "teacher-form";
    }

    @PostMapping("/teachers/{id}/update")
    public String updateTeacher(@PathVariable("id") Long id,
                                @RequestParam("fullName") String fullName,
                                @RequestParam("companyId") Long companyId,
                                Model model) {
        Teacher teacher = teacherDao.findById(id);
        if (teacher == null) {
            return "redirect:/teachers";
        }

        try {
            teacher.setFullName(requireFullName(fullName));
            teacher.setCompany(requireCompany(companyId));
            teacherDao.update(teacher);
            return "redirect:/teachers/" + id;
        } catch (RuntimeException ex) {
            teacher.setFullName(fullName);
            prepareTeacherForm(
                    model,
                    teacher,
                    companyId,
                    "Редактировать преподавателя",
                    "/teachers/" + id + "/update",
                    "Сохранить",
                    "/teachers/" + id
            );
            model.addAttribute("errorMessage", ex.getMessage());
            return "teacher-form";
        }
    }

    @PostMapping("/teachers/{id}/delete")
    public String deleteTeacher(@PathVariable("id") Long id, Model model) {
        try {
            teacherDao.deleteById(id);
            return "redirect:/teachers";
        } catch (RuntimeException ex) {
            Teacher teacher = teacherDao.findById(id);
            if (teacher == null) {
                return "redirect:/teachers";
            }

            fillTeacherDetails(model, teacher);
            model.addAttribute("errorMessage", "Не удалось удалить преподавателя");
            return "teacher-details";
        }
    }

    @GetMapping("/teachers/{id}/schedule")
    public String teacherSchedule(@PathVariable("id") Long id,
                                  @RequestParam(value = "from", required = false) String from,
                                  @RequestParam(value = "to", required = false) String to,
                                  Model model) {
        Teacher teacher = teacherDao.findById(id);
        if (teacher == null) {
            return "redirect:/teachers";
        }

        model.addAttribute("teacher", teacher);
        model.addAttribute("from", from == null ? "" : from);
        model.addAttribute("to", to == null ? "" : to);
        model.addAttribute("schedule", Collections.emptyList());

        if (from != null && !from.isBlank() && to != null && !to.isBlank()) {
            try {
                OffsetDateTime fromDate = toOffsetDateTime(parseDate(from), LocalTime.MIN);
                OffsetDateTime toDate = toOffsetDateTime(parseDate(to), LocalTime.of(23, 59, 59));
                model.addAttribute("schedule", scheduleDao.findByTeacherAndInterval(id, fromDate, toDate));
            } catch (RuntimeException ex) {
                model.addAttribute("errorMessage", ex.getMessage());
            }
        } else if ((from != null && !from.isBlank()) || (to != null && !to.isBlank())) {
            model.addAttribute("errorMessage", "Укажите обе даты интервала");
        }

        return "teacher-schedule";
    }

    private void fillTeacherDetails(Model model, Teacher teacher) {
        model.addAttribute("teacher", teacher);
        model.addAttribute("companyName", resolveCompanyName(teacher));
        model.addAttribute("courses", teacherDao.getCoursesByTeacherId(teacher.getId()));
    }

    private void prepareTeacherForm(Model model,
                                    Teacher teacher,
                                    Long selectedCompanyId,
                                    String formTitle,
                                    String formAction,
                                    String submitLabel,
                                    String cancelUrl) {
        model.addAttribute("teacher", teacher);
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("selectedCompanyId", selectedCompanyId);
        model.addAttribute("formTitle", formTitle);
        model.addAttribute("formAction", formAction);
        model.addAttribute("submitLabel", submitLabel);
        model.addAttribute("cancelUrl", cancelUrl);
    }

    private Map<Long, String> resolveCompanyNamesForTeachers(List<Teacher> teachers) {
        Map<Long, String> companyNames = new LinkedHashMap<>();

        for (Teacher teacher : teachers) {
            companyNames.put(teacher.getId(), resolveCompanyName(teacher));
        }

        return companyNames;
    }

    private String resolveCompanyName(Teacher teacher) {
        if (teacher == null || teacher.getCompany() == null) {
            return "";
        }

        Company company = companyService.getCompanyById(teacher.getCompany().getId());
        return company != null ? company.getName() : "";
    }

    private Company requireCompany(Long companyId) {
        Company company = companyService.getCompanyById(companyId);
        if (company == null) {
            throw new RuntimeException("Компания не найдена");
        }

        return company;
    }

    private String requireFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new RuntimeException("Введите ФИО преподавателя");
        }

        return fullName.trim();
    }

    private String joinCourseTitles(List<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            return "Нет курсов";
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < courses.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(courses.get(i).getTitle());
        }

        return builder.toString();
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
