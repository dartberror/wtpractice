package training_center.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import training_center.entity.Course;
import training_center.entity.Teacher;

@Repository
@Transactional
public class TeacherDao {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(Teacher teacher) {
        entityManager.persist(teacher);
    }

    @Transactional(readOnly = true)
    public Teacher findById(Long id) {
        return entityManager.find(Teacher.class, id);
    }

    @Transactional(readOnly = true)
    public List<Teacher> findAll() {
        return entityManager.createQuery(
                "select t from Teacher t order by t.fullName",
                Teacher.class
        ).getResultList();
    }

    @Transactional(readOnly = true)
    public List<Teacher> findByFullName(String fullNamePart) {
        return entityManager.createQuery(
                "select t from Teacher t " +
                "where lower(t.fullName) like lower(:fullNamePart) " +
                "order by t.fullName",
                Teacher.class
        )
        .setParameter("fullNamePart", "%" + fullNamePart + "%")
        .getResultList();
    }

    public void update(Teacher teacher) {
        entityManager.merge(teacher);
    }

    public void delete(Teacher teacher) {
        entityManager.remove(entityManager.merge(teacher));
    }

    public void deleteById(Long id) {
        Teacher teacher = findById(id);
        if (teacher != null) {
            entityManager.remove(teacher);
        }
    }

    @Transactional(readOnly = true)
    public List<Teacher> findByCourseId(Long courseId) {
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
    public List<Course> getCoursesByTeacherId(Long teacherId) {
        return entityManager.createQuery(
                "select ct.course " +
                "from CourseTeacher ct " +
                "where ct.teacher.id = :teacherId " +
                "order by ct.course.title",
                Course.class
        )
        .setParameter("teacherId", teacherId)
        .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Teacher> findByCompanyId(Long companyId) {
        return entityManager.createQuery(
                "select t from Teacher t " +
                "where t.company.id = :companyId " +
                "order by t.fullName",
                Teacher.class
        )
        .setParameter("companyId", companyId)
        .getResultList();
    }
}