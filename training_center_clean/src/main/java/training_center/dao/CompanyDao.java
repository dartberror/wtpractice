package training_center.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import training_center.entity.Company;

@Repository
@Transactional
public class CompanyDao {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(Company company) {
        entityManager.persist(company);
    }

    @Transactional(readOnly = true)
    public Company findById(Long id) {
        return entityManager.find(Company.class, id);
    }

    @Transactional(readOnly = true)
    public List<Company> findAll() {
        return entityManager.createQuery(
                "select c from Company c order by c.name",
                Company.class
        ).getResultList();
    }

    public void update(Company company) {
        entityManager.merge(company);
    }

    public void delete(Company company) {
        entityManager.remove(entityManager.merge(company));
    }

    public void deleteById(Long id) {
        Company company = findById(id);
        if (company != null) {
            entityManager.remove(company);
        }
    }
}