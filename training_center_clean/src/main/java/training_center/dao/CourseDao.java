package training_center.dao;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import training_center.entity.*;

@Repository
@Transactional
public class CourseDao {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(Course course) {
        entityManager.persist(course);
    }

    @Transactional(readOnly = true)
    public Course findById(Long id) {
        return entityManager.find(Course.class, id);
    }

    @Transactional(readOnly = true)
    public List<Course> findAll() {
        return entityManager.createQuery(
                "select c from Course c order by c.title",
                Course.class
        ).getResultList();
    }

    @Transactional(readOnly = true)
    public List<Course> findByTitle(String titlePart) {
        return entityManager.createQuery(
                "select c from Course c " +
                "where lower(c.title) like lower(:titlePart) " +
                "order by c.title",
                Course.class
        )
        .setParameter("titlePart", "%" + titlePart + "%")
        .getResultList();
    }

    public void update(Course course) {
        entityManager.merge(course);
    }

    public void delete(Course course) {
        entityManager.remove(entityManager.merge(course));
    }

    public void deleteById(Long id) {
        Course course = findById(id);
        if (course != null) {
            entityManager.remove(course);
        }
    }

    @Transactional(readOnly = true)
    public List<Student> getStudentsByCourseId(Long courseId) {
        return entityManager.createQuery(
                "select cs.student " +
                "from CourseStudent cs " +
                "where cs.course.id = :courseId " +
                "order by cs.student.fullName",
                Student.class
        )
        .setParameter("courseId", courseId)
        .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Teacher> getTeachersByCourseId(Long courseId) {
        return entityManager.createQuery(
                "select ct.teacher " +
                "from CourseTeacher ct " +
                "where ct.course.id = :courseId " +
                "order by ct.teacher.fullName",
                Teacher.class
        )
        .setParameter("courseId", courseId)
        .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Schedule> getScheduleByCourseId(Long courseId) {
        return entityManager.createQuery(
                "select s from Schedule s " +
                "where s.course.id = :courseId " +
                "order by s.startAt",
                Schedule.class
        )
        .setParameter("courseId", courseId)
        .getResultList();
    }

    public void addStudentToCourse(Long courseId, Long studentId) {

        Course course = entityManager.find(Course.class, courseId);
        Student student = entityManager.find(Student.class, studentId);

        if (course == null) {
            throw new RuntimeException("Курс с id=" + courseId + " не найден");
        }
        if (student == null) {
            throw new RuntimeException("Обучающийся с id=" + studentId + " не найден");
        }

        CourseStudentId id = new CourseStudentId(studentId, courseId);

        CourseStudent existing = entityManager.find(CourseStudent.class, id);
        if (existing != null) {
            return;
        }

        CourseStudent cs = new CourseStudent();
        cs.setId(id);
        cs.setStudent(student);
        cs.setCourse(course);
        cs.setEnrolledAt(OffsetDateTime.now());

        entityManager.persist(cs);
    }

    public void removeStudentFromCourse(Long courseId, Long studentId) {

        CourseStudentId id = new CourseStudentId(studentId, courseId);
        CourseStudent cs = entityManager.find(CourseStudent.class, id);

        if (cs != null) {
            entityManager.remove(cs);
        }
    }

    public void assignTeacherToCourse(Long courseId, Long teacherId) {

        Course course = entityManager.find(Course.class, courseId);
        Teacher teacher = entityManager.find(Teacher.class, teacherId);

        if (course == null) {
            throw new RuntimeException("Курс с id=" + courseId + " не найден");
        }
        if (teacher == null) {
            throw new RuntimeException("Преподаватель с id=" + teacherId + " не найден");
        }

        CourseTeacherId id = new CourseTeacherId(courseId, teacherId);

        CourseTeacher existing = entityManager.find(CourseTeacher.class, id);
        if (existing != null) {
            return;
        }

        CourseTeacher ct = new CourseTeacher();
        ct.setId(id);
        ct.setCourse(course);
        ct.setTeacher(teacher);

        entityManager.persist(ct);
    }

    public void removeTeacherFromCourse(Long courseId, Long teacherId) {

        CourseTeacherId id = new CourseTeacherId(courseId, teacherId);
        CourseTeacher ct = entityManager.find(CourseTeacher.class, id);

        if (ct != null) {
            entityManager.remove(ct);
        }
    }
}