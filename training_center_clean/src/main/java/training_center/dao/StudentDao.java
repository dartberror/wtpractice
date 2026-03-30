package training_center.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import training_center.entity.Course;
import training_center.entity.Student;

@Repository
@Transactional
public class StudentDao {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(Student student) {
        entityManager.persist(student);
    }

    @Transactional(readOnly = true)
    public Student findById(Long id) {
        return entityManager.find(Student.class, id);
    }

    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return entityManager.createQuery(
                "select s from Student s order by s.fullName",
                Student.class
        ).getResultList();
    }

    @Transactional(readOnly = true)
    public List<Student> findByFullName(String fullNamePart) {
        return entityManager.createQuery(
                "select s from Student s " +
                "where lower(s.fullName) like lower(:fullNamePart) " +
                "order by s.fullName",
                Student.class
        )
        .setParameter("fullNamePart", "%" + fullNamePart + "%")
        .getResultList();
    }

    public void update(Student student) {
        entityManager.merge(student);
    }

    public void delete(Student student) {
        entityManager.remove(entityManager.merge(student));
    }

    public void deleteById(Long id) {
        Student student = findById(id);
        if (student != null) {
            entityManager.remove(student);
        }
    }

    @Transactional(readOnly = true)
    public List<Student> findByCourseId(Long courseId) {
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
    public List<Course> getCourseHistory(Long studentId) {
        return entityManager.createQuery(
                "select cs.course " +
                "from CourseStudent cs " +
                "where cs.student.id = :studentId " +
                "order by cs.enrolledAt desc, cs.course.title",
                Course.class
        )
        .setParameter("studentId", studentId)
        .getResultList();
    }
}