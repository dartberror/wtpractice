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

public class ScheduleDaoTest extends AbstractDaoIntegrationTest {

    private ScheduleDao scheduleDao;
    private CompanyDao companyDao;
    private CourseDao courseDao;
    private TeacherDao teacherDao;
    private StudentDao studentDao;

    @BeforeMethod
    public void setUpDao() {
        scheduleDao = context.getBean(ScheduleDao.class);
        companyDao = context.getBean(CompanyDao.class);
        courseDao = context.getBean(CourseDao.class);
        teacherDao = context.getBean(TeacherDao.class);
        studentDao = context.getBean(StudentDao.class);
    }

    @Test
    public void testSaveAndFindById() {
        Company company = new Company("C", "A");
        companyDao.save(company);

        Course course = new Course("Course", "day", 5, 2, company);
        courseDao.save(course);

        Teacher teacher = new Teacher("Teacher", company);
        teacherDao.save(teacher);

        courseDao.assignTeacherToCourse(course.getId(), teacher.getId());

        OffsetDateTime start = OffsetDateTime.now().plusDays(1);
        OffsetDateTime end = start.plusHours(2);

        Schedule schedule = new Schedule(course, teacher, start, end);
        scheduleDao.save(schedule);

        Schedule found = scheduleDao.findById(schedule.getId());

        assertNotNull(found);
        assertEquals(found.getCourse().getId(), course.getId());
        assertEquals(found.getTeacher().getId(), teacher.getId());
    }

    @Test
    public void testFindAll() {
        Company company = new Company("C2", "A2");
        companyDao.save(company);

        Course course = new Course("Course2", "day", 4, 2, company);
        courseDao.save(course);

        Teacher teacher = new Teacher("Teacher2", company);
        teacherDao.save(teacher);

        courseDao.assignTeacherToCourse(course.getId(), teacher.getId());

        Schedule schedule = new Schedule(
                course,
                teacher,
                OffsetDateTime.now().plusDays(1),
                OffsetDateTime.now().plusDays(1).plusHours(2)
        );

        scheduleDao.save(schedule);

        assertTrue(
                scheduleDao.findAll().stream()
                        .anyMatch(s -> s.getId().equals(schedule.getId()))
        );
    }

    @Test
    public void testUpdate() {
        Company company = new Company("CU", "AU");
        companyDao.save(company);

        Course course = new Course("CourseU", "day", 3, 1, company);
        courseDao.save(course);

        Teacher teacher = new Teacher("TeacherU", company);
        teacherDao.save(teacher);

        courseDao.assignTeacherToCourse(course.getId(), teacher.getId());

        OffsetDateTime start = OffsetDateTime.now().plusDays(1);
        OffsetDateTime end = start.plusHours(2);

        Schedule schedule = new Schedule(course, teacher, start, end);
        scheduleDao.save(schedule);

        OffsetDateTime newStart = start.plusDays(1);
        OffsetDateTime newEnd = newStart.plusHours(3);

        schedule.setStartAt(newStart);
        schedule.setEndAt(newEnd);
        scheduleDao.update(schedule);

        Schedule updated = scheduleDao.findById(schedule.getId());

        assertEquals(
                updated.getStartAt().toInstant().toEpochMilli(),
                newStart.toInstant().toEpochMilli()
        );
        assertEquals(
                updated.getEndAt().toInstant().toEpochMilli(),
                newEnd.toInstant().toEpochMilli()
        );
    }

    @Test
    public void testDelete() {
        Schedule schedule = createSchedule();

        Long id = schedule.getId();

        scheduleDao.delete(schedule);

        assertNull(scheduleDao.findById(id));
    }

    @Test
    public void testDeleteDetached() {
        Schedule schedule = createSchedule();
        Long id = schedule.getId();

        Schedule detached = new Schedule();
        detached.setId(id);
        detached.setCourse(schedule.getCourse());
        detached.setTeacher(schedule.getTeacher());
        detached.setStartAt(schedule.getStartAt());
        detached.setEndAt(schedule.getEndAt());

        scheduleDao.delete(detached);

        assertNull(scheduleDao.findById(id));
    }

    @Test
    public void testDeleteByIdWhenExists() {
        Schedule schedule = createSchedule();
        Long id = schedule.getId();

        scheduleDao.deleteById(id);

        assertNull(scheduleDao.findById(id));
    }

    @Test
    public void testDeleteByIdWhenNotExists() {
        scheduleDao.deleteById(-999L);

        assertNull(scheduleDao.findById(-999L));
    }

    @Test
    public void testFindByTeacherAndInterval() {
        Company company = new Company("CT", "AT");
        companyDao.save(company);

        Course course = new Course("CourseT", "day", 5, 2, company);
        courseDao.save(course);

        Teacher teacher = new Teacher("TeacherT", company);
        teacherDao.save(teacher);

        courseDao.assignTeacherToCourse(course.getId(), teacher.getId());

        OffsetDateTime start = OffsetDateTime.now().plusDays(1);

        Schedule schedule = new Schedule(course, teacher, start, start.plusHours(2));
        scheduleDao.save(schedule);

        List<Schedule> result = scheduleDao.findByTeacherAndInterval(
                teacher.getId(),
                start.minusHours(1),
                start.plusHours(1)
        );

        assertTrue(result.stream().anyMatch(s -> s.getId().equals(schedule.getId())));
    }

    @Test
    public void testFindByStudentAndInterval() {
        Company company = new Company("CS", "AS");
        companyDao.save(company);

        Course course = new Course("CourseS", "day", 5, 2, company);
        courseDao.save(course);

        Teacher teacher = new Teacher("TeacherS", company);
        teacherDao.save(teacher);

        Student student = new Student("StudentS");
        studentDao.save(student);

        courseDao.assignTeacherToCourse(course.getId(), teacher.getId());
        courseDao.addStudentToCourse(course.getId(), student.getId());

        OffsetDateTime start = OffsetDateTime.now().plusDays(1);

        Schedule schedule = new Schedule(course, teacher, start, start.plusHours(2));
        scheduleDao.save(schedule);

        List<Schedule> result = scheduleDao.findByStudentAndInterval(
                student.getId(),
                start.minusHours(1),
                start.plusHours(1)
        );

        assertTrue(result.stream().anyMatch(s -> s.getId().equals(schedule.getId())));
    }

    @Test
    public void testFindByCourseId() {
        Company company = new Company("CC", "AC");
        companyDao.save(company);

        Course course = new Course("CourseC", "day", 4, 2, company);
        courseDao.save(course);

        Teacher teacher = new Teacher("TeacherC", company);
        teacherDao.save(teacher);

        courseDao.assignTeacherToCourse(course.getId(), teacher.getId());

        Schedule schedule = new Schedule(
                course,
                teacher,
                OffsetDateTime.now().plusDays(1),
                OffsetDateTime.now().plusDays(1).plusHours(2)
        );

        scheduleDao.save(schedule);

        assertTrue(
                scheduleDao.findByCourseId(course.getId())
                        .stream()
                        .anyMatch(s -> s.getId().equals(schedule.getId()))
        );
    }

    @Test
    public void testSaveLessonSuccess() {
        Company company = new Company("CL", "AL");
        companyDao.save(company);

        Course course = new Course("CourseL", "day", 5, 2, company);
        courseDao.save(course);

        Teacher teacher = new Teacher("TeacherL", company);
        teacherDao.save(teacher);

        courseDao.assignTeacherToCourse(course.getId(), teacher.getId());

        OffsetDateTime start = OffsetDateTime.now().plusDays(2);
        OffsetDateTime end = start.plusHours(2);

        scheduleDao.saveLesson(course.getId(), teacher.getId(), start, end);

        assertFalse(scheduleDao.findByCourseId(course.getId()).isEmpty());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testSaveLessonWhenBothDatesNull() {
        scheduleDao.saveLesson(1L, 1L, null, null);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testSaveLessonWhenStartNull() {
        scheduleDao.saveLesson(1L, 1L, null, OffsetDateTime.now().plusDays(1));
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testSaveLessonWhenEndNull() {
        scheduleDao.saveLesson(1L, 1L, OffsetDateTime.now().plusDays(1), null);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testSaveLessonWhenInvalidInterval() {
        OffsetDateTime start = OffsetDateTime.now().plusDays(1);
        scheduleDao.saveLesson(1L, 1L, start, start.minusHours(1));
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testSaveLessonWhenCourseNotFound() {
        Company company = new Company("MC", "ADDR");
        companyDao.save(company);

        Teacher teacher = new Teacher("Teacher MC", company);
        teacherDao.save(teacher);

        OffsetDateTime start = OffsetDateTime.now().plusDays(1);
        OffsetDateTime end = start.plusHours(2);

        scheduleDao.saveLesson(-999L, teacher.getId(), start, end);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testSaveLessonWhenTeacherNotFound() {
        Company company = new Company("MT", "ADDR");
        companyDao.save(company);

        Course course = new Course("Course MT", "day", 5, 2, company);
        courseDao.save(course);

        OffsetDateTime start = OffsetDateTime.now().plusDays(1);
        OffsetDateTime end = start.plusHours(2);

        scheduleDao.saveLesson(course.getId(), -999L, start, end);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testSaveLessonWhenTeacherNotAssignedToCourse() {
        Company company = new Company("NA", "ADDR");
        companyDao.save(company);

        Course course = new Course("Course NA", "day", 5, 2, company);
        courseDao.save(course);

        Teacher teacher = new Teacher("Teacher NA", company);
        teacherDao.save(teacher);

        OffsetDateTime start = OffsetDateTime.now().plusDays(1);
        OffsetDateTime end = start.plusHours(2);

        scheduleDao.saveLesson(course.getId(), teacher.getId(), start, end);
    }

    @Test
    public void testFindEmptyCases() {
        assertTrue(scheduleDao.findAll().isEmpty());
        assertTrue(scheduleDao.findByCourseId(-1L).isEmpty());
        assertTrue(scheduleDao.findByTeacherAndInterval(
                -1L,
                OffsetDateTime.now().minusDays(1),
                OffsetDateTime.now().plusDays(1)
        ).isEmpty());
        assertTrue(scheduleDao.findByStudentAndInterval(
                -1L,
                OffsetDateTime.now().minusDays(1),
                OffsetDateTime.now().plusDays(1)
        ).isEmpty());
    }

    private Schedule createSchedule() {
        Company company = new Company("Comp_" + System.nanoTime(), "Addr");
        companyDao.save(company);

        Course course = new Course("Course_" + System.nanoTime(), "day", 5, 1, company);
        courseDao.save(course);

        Teacher teacher = new Teacher("Teacher_" + System.nanoTime(), company);
        teacherDao.save(teacher);

        courseDao.assignTeacherToCourse(course.getId(), teacher.getId());

        OffsetDateTime start = OffsetDateTime.now().plusDays(1);
        OffsetDateTime end = start.plusHours(2);

        Schedule schedule = new Schedule(course, teacher, start, end);
        scheduleDao.save(schedule);

        return schedule;
    }
}