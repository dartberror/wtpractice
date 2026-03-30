package training_center.dao;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import training_center.config.PersistenceConfig;

public abstract class AbstractDaoIntegrationTest {

    protected AnnotationConfigApplicationContext context;
    protected EntityManagerFactory entityManagerFactory;
    protected EntityManager entityManager;

    @BeforeClass
    public void setUpClass() {
        context = new AnnotationConfigApplicationContext(PersistenceConfig.class);
        entityManagerFactory = context.getBean(EntityManagerFactory.class);
        entityManager = entityManagerFactory.createEntityManager();
    }

    @BeforeMethod
    public void cleanDatabase() {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        entityManager.createQuery("delete from Schedule").executeUpdate();
        entityManager.createQuery("delete from CourseStudent").executeUpdate();
        entityManager.createQuery("delete from CourseTeacher").executeUpdate();
        entityManager.createQuery("delete from Student").executeUpdate();
        entityManager.createQuery("delete from Teacher").executeUpdate();
        entityManager.createQuery("delete from Course").executeUpdate();
        entityManager.createQuery("delete from Company").executeUpdate();

        transaction.commit();
    }

    @AfterClass
    public void tearDownClass() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
        if (context != null) {
            context.close();
        }
    }
}