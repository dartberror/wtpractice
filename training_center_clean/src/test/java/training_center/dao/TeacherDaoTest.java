package training_center.dao;

import static org.testng.Assert.*;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import training_center.entity.Company;
import training_center.entity.Course;
import training_center.entity.Teacher;

public class TeacherDaoTest extends AbstractDaoIntegrationTest {

    private TeacherDao teacherDao;
    private CompanyDao companyDao;

    @BeforeMethod
    public void setUpDao() {
        teacherDao = context.getBean(TeacherDao.class);
        companyDao = context.getBean(CompanyDao.class);
    }

    @Test
    public void testSaveAndFindById() {
        Company company = new Company("Company", "Address");
        companyDao.save(company);

        Teacher teacher = new Teacher("Teacher Name", company);
        teacherDao.save(teacher);

        Teacher found = teacherDao.findById(teacher.getId());

        assertNotNull(found);
        assertEquals(found.getFullName(), "Teacher Name");
    }

    @Test
    public void testFindAll() {
        Company company = new Company("Company All", "Address");
        companyDao.save(company);

        Teacher teacher = new Teacher("Teacher All", company);
        teacherDao.save(teacher);

        List<Teacher> list = teacherDao.findAll();

        assertTrue(list.stream().anyMatch(t -> t.getId().equals(teacher.getId())));
    }

    @Test
    public void testFindByFullName() {
        Company company = new Company("Company Name", "Address");
        companyDao.save(company);

        Teacher teacher = new Teacher("Unique Teacher", company);
        teacherDao.save(teacher);

        List<Teacher> found = teacherDao.findByFullName("Unique");

        assertTrue(found.stream().anyMatch(t -> t.getId().equals(teacher.getId())));
    }

    @Test
    public void testFindByFullNameEmpty() {
        List<Teacher> result = teacherDao.findByFullName("no_such_teacher");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testUpdate() {
        Company company = new Company("Company Update", "Address");
        companyDao.save(company);

        Teacher teacher = new Teacher("Old Name", company);
        teacherDao.save(teacher);

        teacher.setFullName("New Name");
        teacherDao.update(teacher);

        Teacher updated = teacherDao.findById(teacher.getId());

        assertEquals(updated.getFullName(), "New Name");
    }

    @Test
    public void testUpdateDetached() {
        Company company = new Company("Company Detached", "Address");
        companyDao.save(company);

        Teacher teacher = new Teacher("Detached Old", company);
        teacherDao.save(teacher);

        Teacher detached = teacherDao.findById(teacher.getId());
        detached.setFullName("Detached New");

        teacherDao.update(detached);

        Teacher updated = teacherDao.findById(teacher.getId());

        assertEquals(updated.getFullName(), "Detached New");
    }

    @Test
    public void testDelete() {
        Company company = new Company("Company Delete", "Address");
        companyDao.save(company);

        Teacher teacher = new Teacher("Delete Me", company);
        teacherDao.save(teacher);

        Long id = teacher.getId();

        teacherDao.delete(teacher);

        assertNull(teacherDao.findById(id));
    }

    @Test
    public void testDeleteDetached() {
        Company company = new Company("Company Delete Detached", "Address");
        companyDao.save(company);

        Teacher teacher = new Teacher("Delete Detached", company);
        teacherDao.save(teacher);

        Long id = teacher.getId();

        Teacher detached = new Teacher();
        detached.setId(id);
        detached.setFullName("Delete Detached");
        detached.setCompany(company);

        teacherDao.delete(detached);

        assertNull(teacherDao.findById(id));
    }

    @Test
    public void testDeleteById() {
        Company company = new Company("Company DeleteById", "Address");
        companyDao.save(company);

        Teacher teacher = new Teacher("Delete By Id", company);
        teacherDao.save(teacher);

        Long id = teacher.getId();

        teacherDao.deleteById(id);

        assertNull(teacherDao.findById(id));
    }

    @Test
    public void testDeleteByIdWhenNotExists() {
        teacherDao.deleteById(-999L);

        assertNull(teacherDao.findById(-999L));
    }

    @Test
    public void testFindByCompanyIdEmpty() {
        List<Teacher> result = teacherDao.findByCompanyId(-999L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindByCourseIdEmpty() {
        List<Teacher> result = teacherDao.findByCourseId(-999L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetCoursesByTeacherIdEmpty() {
        List<Course> result = teacherDao.getCoursesByTeacherId(-999L);

        assertTrue(result.isEmpty());
    }
}