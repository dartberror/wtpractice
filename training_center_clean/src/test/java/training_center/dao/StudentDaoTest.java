package training_center.dao;

import static org.testng.Assert.*;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import training_center.entity.Company;
import training_center.entity.Course;
import training_center.entity.Student;

public class StudentDaoTest extends AbstractDaoIntegrationTest {
    private StudentDao studentDao;
    private CompanyDao companyDao;
    private CourseDao courseDao;

    @BeforeMethod
    public void setUpDao() {
        studentDao = context.getBean(StudentDao.class);
        companyDao = context.getBean(CompanyDao.class);
        courseDao = context.getBean(CourseDao.class);
    }

    @Test
    public void testSaveAndFindById() {
        Student student = new Student("John Doe");

        studentDao.save(student);

        assertNotNull(student.getId());

        Student found = studentDao.findById(student.getId());

        assertNotNull(found);
        assertEquals(found.getFullName(), "John Doe");
    }

    @Test
    public void testFindAll() {
        Student student = new Student("Student A");
        studentDao.save(student);

        List<Student> students = studentDao.findAll();

        assertTrue(
                students.stream().anyMatch(s -> s.getId().equals(student.getId()))
        );
    }

    @Test
    public void testFindByFullName() {
        Student student = new Student("Unique Name");
        studentDao.save(student);

        List<Student> found = studentDao.findByFullName("Unique");

        assertTrue(
                found.stream().anyMatch(s -> s.getId().equals(student.getId()))
        );
    }

    @Test
    public void testFindByFullNameEmpty() {
        List<Student> found = studentDao.findByFullName("no_such_name");

        assertNotNull(found);
        assertTrue(found.isEmpty());
    }

    @Test
    public void testUpdate() {
        Student student = new Student("Old Name");
        studentDao.save(student);

        student.setFullName("New Name");
        studentDao.update(student);

        Student updated = studentDao.findById(student.getId());

        assertEquals(updated.getFullName(), "New Name");
    }

    @Test
    public void testUpdateDetached() {
        Student student = new Student("Detached Old");
        studentDao.save(student);

        Student detached = studentDao.findById(student.getId());
        detached.setFullName("Detached New");

        studentDao.update(detached);

        Student updated = studentDao.findById(student.getId());

        assertEquals(updated.getFullName(), "Detached New");
    }

    @Test
    public void testDelete() {
        Student student = new Student("Delete Me");
        studentDao.save(student);

        Long id = student.getId();

        studentDao.delete(student);

        assertNull(studentDao.findById(id));
    }

    @Test
    public void testDeleteDetached() {
        Student student = new Student("Delete Detached");
        studentDao.save(student);

        Student detached = studentDao.findById(student.getId());

        studentDao.delete(detached);

        assertNull(studentDao.findById(student.getId()));
    }

    @Test
    public void testDeleteById() {
        Student student = new Student("Delete By Id");
        studentDao.save(student);

        Long id = student.getId();

        studentDao.deleteById(id);

        assertNull(studentDao.findById(id));
    }

    @Test
    public void testDeleteByIdWhenStudentDoesNotExist() {
        studentDao.deleteById(-999L);

        assertNull(studentDao.findById(-999L));
    }

    @Test
    public void testFindByCourseId() {
        Company company = new Company("Company", "Address");
        companyDao.save(company);

        Course course = new Course("Course", "day", 5, 2, company);
        courseDao.save(course);

        Student student = new Student("Student Course");
        studentDao.save(student);

        courseDao.addStudentToCourse(course.getId(), student.getId());

        List<Student> students = studentDao.findByCourseId(course.getId());

        assertTrue(
                students.stream().anyMatch(s -> s.getId().equals(student.getId()))
        );
    }

    @Test
    public void testFindByCourseIdEmpty() {
        List<Student> result = studentDao.findByCourseId(-999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetCourseHistory() {
        Company company = new Company("Company H", "Address H");
        companyDao.save(company);

        Course course = new Course("Course H", "day", 3, 1, company);
        courseDao.save(course);

        Student student = new Student("Student H");
        studentDao.save(student);

        courseDao.addStudentToCourse(course.getId(), student.getId());

        List<Course> history = studentDao.getCourseHistory(student.getId());

        assertTrue(
                history.stream().anyMatch(c -> c.getId().equals(course.getId()))
        );
    }

    @Test
    public void testGetCourseHistoryEmpty() {
        List<Course> history = studentDao.getCourseHistory(-999L);

        assertNotNull(history);
        assertTrue(history.isEmpty());
    }
}