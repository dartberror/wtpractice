package training_center.dao;

import java.util.List;
import org.hibernate.Session;
import training_center.entity.Student;
import training_center.util.HibernateUtil;

public class StudentDao {
    public List<Student> findByCourseId(Long courseId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select cs.student " +
                    "from CourseStudent cs " +
                    "where cs.course.id = :courseId", Student.class)
                    .setParameter("courseId", courseId)
                    .getResultList();
        }
    }
}
