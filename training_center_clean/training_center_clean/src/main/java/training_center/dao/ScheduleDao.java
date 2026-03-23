package training_center.dao;

import java.time.OffsetDateTime;
import java.util.List;
import org.hibernate.Session;
import training_center.entity.Schedule;
import training_center.util.HibernateUtil;

public class ScheduleDao {
    public List<Schedule> findByTeacherAndInterval(Long teacherId, OffsetDateTime from, OffsetDateTime to) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select s " +
                    "from Schedule s " +
                    "join fetch s.course " +
                    "where s.teacher.id = :teacherId " +
                    "and s.startAt >= :from " +
                    "and s.startAt <= :to " +
                    "order by s.startAt", Schedule.class)
                    .setParameter("teacherId", teacherId)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .getResultList();
        }
    }
}