package training_center.dao;

import static org.testng.Assert.*;

import java.time.OffsetDateTime;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import training_center.entity.Company;
import training_center.entity.Course;
import training_center.entity.Schedule;
import training_center.entity.Student;
import training_center.entity.Teacher;

public class CourseDaoTest extends AbstractDaoIntegrationTest {
    private CourseDao courseDao;
    private CompanyDao companyDao;
    private StudentDao studentDao;
    private TeacherDao teacherDao;
    private ScheduleDao scheduleDao;

    @BeforeMethod
    public void setUpDao() {
        courseDao = context.getBean(CourseDao.class);
        companyDao = context.getBean(CompanyDao.class);
        studentDao = context.getBean(StudentDao.class);
        teacherDao = context.getBean(TeacherDao.class);
        scheduleDao = context.getBean(ScheduleDao.class);
    }

    @Test
    public void testSaveAndFindById() {
        Company company = new Company("Company", "Address");
        companyDao.save(company);

        Course course = new Course("Course", "day", 5, 2, company);
        courseDao.save(course);

        Course found = courseDao.findById(course.getId());

        assertNotNull(found);
        assertEquals(found.getTitle(), "Course");
    }

    @Test
    public void testFindAll() {
        Company company = new Company("Company All", "Address");
        companyDao.save(company);

        Course course = new Course("Course All", "day", 4, 2, company);
        courseDao.save(course);

        List<Course> courses = courseDao.findAll();

        assertTrue(courses.stream().anyMatch(c -> c.getId().equals(course.getId())));
    }

    @Test
    public void testFindByTitle() {
        Company company = new Company("Company T", "Address");
        companyDao.save(company);

        Course course = new Course("Java Advanced", "day", 3, 1, company);
        courseDao.save(course);

        List<Course> found = courseDao.findByTitle("Advanced");

        assertTrue(found.stream().anyMatch(c -> c.getId().equals(course.getId())));
    }

    @Test
    public void testUpdate() {
        Company company = new Company("Company U", "Address");
        companyDao.save(company);

        Course course = new Course("Old", "day", 2, 1, company);
        courseDao.save(course);

        course.setTitle("New");
        course.setDurationValue(10);

        courseDao.update(course);

        Course updated = courseDao.findById(course.getId());

        assertEquals(updated.getTitle(), "New");
        assertEquals(updated.getDurationValue(), Integer.valueOf(10));
    }

    @Test
    public void testDelete() {
        Company company = new Company("Company D", "Address");
        companyDao.save(company);

        Course course = new Course("Delete", "day", 2, 1, company);
        courseDao.save(course);

        Long id = course.getId();

        courseDao.delete(course);

        assertNull(courseDao.findById(id));
    }

    @Test
    public void delete_shouldRemoveDetachedCourse() {
        Company company = new Company("Company Detached", "Address");
        companyDao.save(company);

        Course course = new Course("Course", "days", 5, 1, company);
        courseDao.save(course);

        Long id = course.getId();

        Course detached = new Course();
        detached.setId(id);
        detached.setTitle("Course");
        detached.setDurationUnit("days");
        detached.setDurationValue(5);
        detached.setIntensity(1);
        detached.setCompany(company);

        courseDao.delete(detached);

        Course deleted = courseDao.findById(id);
        assertNull(deleted);
    }

    @Test
    public void deleteById_shouldDeleteCourse() {
        Company company = new Company("Company DeleteById", "Address");
        companyDao.save(company);

        Course course = new Course("Course", "days", 5, 1, company);
        courseDao.save(course);

        Long id = course.getId();

        courseDao.deleteById(id);

        Course deleted = courseDao.findById(id);
        assertNull(deleted);
    }

    @Test
    public void testAddAndRemoveStudent() {
        Company company = new Company("Company S", "Address");
        companyDao.save(company);

        Course course = new Course("Course S", "day", 3, 1, company);
        courseDao.save(course);

        Student student = new Student("Student");
        studentDao.save(student);

        courseDao.addStudentToCourse(course.getId(), student.getId());

        assertTrue(
                courseDao.getStudentsByCourseId(course.getId())
                        .stream()
                        .anyMatch(s -> s.getId().equals(student.getId()))
        );

        courseDao.removeStudentFromCourse(course.getId(), student.getId());

        assertTrue(courseDao.getStudentsByCourseId(course.getId()).isEmpty());
    }

    @Test
    public void testAddStudentAlreadyExists() {
        Company company = new Company("Company SE", "Address");
        companyDao.save(company);

        Course course = new Course("Course SE", "day", 3, 1, company);
        courseDao.save(course);

        Student student = new Student("Student SE");
        studentDao.save(student);

        courseDao.addStudentToCourse(course.getId(), student.getId());
        courseDao.addStudentToCourse(course.getId(), student.getId());

        long count = courseDao.getStudentsByCourseId(course.getId())
                .stream()
                .filter(s -> s.getId().equals(student.getId()))
                .count();

        assertEquals(count, 1L);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void addStudentToCourse_shouldThrow_whenCourseNotFound() {
        Student student = new Student("Student");
        studentDao.save(student);

        courseDao.addStudentToCourse(999L, student.getId());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void addStudentToCourse_shouldThrow_whenStudentNotFound() {
        Course course = createCourse();

        courseDao.addStudentToCourse(course.getId(), 999L);
    }

    @Test
    public void addStudentToCourse_shouldNotDuplicateRelation() {
        Course course = createCourse();
        Student student = createStudent();

        courseDao.addStudentToCourse(course.getId(), student.getId());
        courseDao.addStudentToCourse(course.getId(), student.getId());

        List<Student> students = courseDao.getStudentsByCourseId(course.getId());

        long count = students.stream()
                .filter(s -> s.getId().equals(student.getId()))
                .count();

        assertEquals(count, 1L);
    }

    @Test
    public void removeStudentFromCourse_shouldDoNothing_whenRelationNotExists() {
        Course course = createCourse();
        Student student = createStudent();

        courseDao.removeStudentFromCourse(course.getId(), student.getId());

        assertTrue(courseDao.getStudentsByCourseId(course.getId()).isEmpty());
    }

    @Test
    public void testAssignAndRemoveTeacher() {
        Company company = new Company("Company T2", "Address");
        companyDao.save(company);

        Course course = new Course("Course T2", "day", 3, 1, company);
        courseDao.save(course);

        Teacher teacher = new Teacher("Teacher", company);
        teacherDao.save(teacher);

        courseDao.assignTeacherToCourse(course.getId(), teacher.getId());

        assertTrue(
                courseDao.getTeachersByCourseId(course.getId())
                        .stream()
                        .anyMatch(t -> t.getId().equals(teacher.getId()))
        );

        courseDao.removeTeacherFromCourse(course.getId(), teacher.getId());

        assertTrue(courseDao.getTeachersByCourseId(course.getId()).isEmpty());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void assignTeacherToCourse_shouldThrow_whenCourseNotFound() {
        Teacher teacher = createTeacher();

        courseDao.assignTeacherToCourse(999L, teacher.getId());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void assignTeacherToCourse_shouldThrow_whenTeacherNotFound() {
        Course course = createCourse();

        courseDao.assignTeacherToCourse(course.getId(), 999L);
    }

    @Test
    public void assignTeacherToCourse_shouldNotDuplicateRelation() {
        Course course = createCourse();
        Teacher teacher = createTeacher();

        courseDao.assignTeacherToCourse(course.getId(), teacher.getId());
        courseDao.assignTeacherToCourse(course.getId(), teacher.getId());

        List<Teacher> teachers = courseDao.getTeachersByCourseId(course.getId());

        long count = teachers.stream()
                .filter(t -> t.getId().equals(teacher.getId()))
                .count();

        assertEquals(count, 1L);
    }

    @Test
    public void removeTeacherFromCourse_shouldDoNothing_whenRelationNotExists() {
        Course course = createCourse();
        Teacher teacher = createTeacher();

        courseDao.removeTeacherFromCourse(course.getId(), teacher.getId());

        assertTrue(courseDao.getTeachersByCourseId(course.getId()).isEmpty());
    }

    @Test
    public void testGetScheduleByCourseId() {
        Company company = new Company("Company Sch", "Address");
        companyDao.save(company);

        Course course = new Course("Course Sch", "day", 3, 1, company);
        courseDao.save(course);

        Teacher teacher = new Teacher("Teacher Sch", company);
        teacherDao.save(teacher);

        courseDao.assignTeacherToCourse(course.getId(), teacher.getId());

        OffsetDateTime start = OffsetDateTime.now().plusDays(1);
        OffsetDateTime end = start.plusHours(2);

        Schedule schedule = new Schedule(course, teacher, start, end);
        scheduleDao.save(schedule);

        List<Schedule> schedules = courseDao.getScheduleByCourseId(course.getId());

        assertTrue(schedules.stream().anyMatch(s -> s.getId().equals(schedule.getId())));
    }

    @Test
    public void testFindByTitleEmpty() {
        assertTrue(courseDao.findByTitle("nope").isEmpty());
    }

    @Test
    public void testDeleteByIdNotExists() {
        courseDao.deleteById(-999L);
        assertNull(courseDao.findById(-999L));
    }

    private Course createCourse() {
        Company company = new Company("Comp_" + System.nanoTime(), "Addr");
        companyDao.save(company);

        Course course = new Course();
        course.setTitle("Course_" + System.nanoTime());
        course.setDurationUnit("days");
        course.setDurationValue(5);
        course.setIntensity(1);
        course.setCompany(company);

        courseDao.save(course);
        return course;
    }

    private Student createStudent() {
        Student student = new Student();
        student.setFullName("Student_" + System.nanoTime());

        studentDao.save(student);
        return student;
    }

    private Teacher createTeacher() {
        Company company = new Company("Comp_" + System.nanoTime(), "Addr");
        companyDao.save(company);

        Teacher teacher = new Teacher();
        teacher.setFullName("Teacher_" + System.nanoTime());
        teacher.setCompany(company);

        teacherDao.save(teacher);
        return teacher;
    }
}