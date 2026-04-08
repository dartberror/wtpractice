package training_center.service;

import java.util.List;

import org.springframework.stereotype.Service;

import training_center.dao.CourseDao;
import training_center.entity.Company;
import training_center.entity.Course;
import training_center.entity.Schedule;
import training_center.entity.Student;
import training_center.entity.Teacher;

@Service
public class CourseService {

    private final CourseDao courseDao;
    private final CompanyService companyService;

    public CourseService(CourseDao courseDao, CompanyService companyService) {
        this.courseDao = courseDao;
        this.companyService = companyService;
    }

    public List<Course> getAllCourses() {
        return courseDao.findAll();
    }

    public Course getCourseById(Long id) {
        return courseDao.findById(id);
    }

    public List<Student> getStudents(Long courseId) {
        return courseDao.getStudentsByCourseId(courseId);
    }

    public List<Teacher> getTeachers(Long courseId) {
        return courseDao.getTeachersByCourseId(courseId);
    }

    public List<Schedule> getSchedule(Long courseId) {
        return courseDao.getScheduleByCourseId(courseId);
    }

    public void createCourse(String title,
                             String durationUnit,
                             Integer durationValue,
                             Integer intensity,
                             Long companyId) {

        Company company = companyService.getCompanyById(companyId);
        if (company == null) {
            throw new RuntimeException("Компания не найдена");
        }

        Course course = new Course(title, durationUnit, durationValue, intensity, company);
        courseDao.save(course);
    }
}