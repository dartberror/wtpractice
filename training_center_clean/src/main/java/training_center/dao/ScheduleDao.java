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
public class ScheduleDao {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(Schedule schedule) {
        entityManager.persist(schedule);
    }

    @Transactional(readOnly = true)
    public Schedule findById(Long id) {
        return entityManager.find(Schedule.class, id);
    }

    @Transactional(readOnly = true)
    public List<Schedule> findAll() {
        return entityManager.createQuery(
                "select s from Schedule s order by s.startAt",
                Schedule.class
        ).getResultList();
    }

    public void update(Schedule schedule) {
        entityManager.merge(schedule);
    }

    public void delete(Schedule schedule) {
        entityManager.remove(entityManager.merge(schedule));
    }

    public void deleteById(Long id) {
        Schedule schedule = findById(id);
        if (schedule != null) {
            entityManager.remove(schedule);
        }
    }

    @Transactional(readOnly = true)
    public List<Schedule> findByTeacherAndInterval(Long teacherId, OffsetDateTime from, OffsetDateTime to) {
        return entityManager.createQuery(
                "select s from Schedule s " +
                "join fetch s.course " +
                "where s.teacher.id = :teacherId " +
                "and s.startAt >= :from " +
                "and s.startAt <= :to " +
                "order by s.startAt",
                Schedule.class
        )
        .setParameter("teacherId", teacherId)
        .setParameter("from", from)
        .setParameter("to", to)
        .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Schedule> findByStudentAndInterval(Long studentId, OffsetDateTime from, OffsetDateTime to) {
        return entityManager.createQuery(
                "select s from Schedule s, CourseStudent cs " +
                "where cs.course.id = s.course.id " +
                "and cs.student.id = :studentId " +
                "and s.startAt >= :from " +
                "and s.startAt <= :to " +
                "order by s.startAt",
                Schedule.class
        )
        .setParameter("studentId", studentId)
        .setParameter("from", from)
        .setParameter("to", to)
        .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Schedule> findByCourseId(Long courseId) {
        return entityManager.createQuery(
                "select s from Schedule s " +
                "where s.course.id = :courseId " +
                "order by s.startAt",
                Schedule.class
        )
        .setParameter("courseId", courseId)
        .getResultList();
    }

    public void saveLesson(Long courseId, Long teacherId, OffsetDateTime startAt, OffsetDateTime endAt) {
        if (startAt == null || endAt == null) {
            throw new RuntimeException("Дата и время занятия не должны быть null");
        }

        if (!endAt.isAfter(startAt)) {
            throw new RuntimeException("Некорректный временной интервал: endAt должен быть больше startAt");
        }

        Course course = entityManager.find(Course.class, courseId);
        if (course == null) {
            throw new RuntimeException("Курс с id=" + courseId + " не найден");
        }

        Teacher teacher = entityManager.find(Teacher.class, teacherId);
        if (teacher == null) {
            throw new RuntimeException("Преподаватель с id=" + teacherId + " не найден");
        }

        CourseTeacherId id = new CourseTeacherId(courseId, teacherId);
        CourseTeacher ct = entityManager.find(CourseTeacher.class, id);

        if (ct == null) {
            throw new RuntimeException("Преподаватель не назначен на данный курс");
        }

        Schedule schedule = new Schedule(course, teacher, startAt, endAt);
        entityManager.persist(schedule);
    }
}