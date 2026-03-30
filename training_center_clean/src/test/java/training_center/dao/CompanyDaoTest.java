package training_center.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import training_center.entity.Company;

public class CompanyDaoTest extends AbstractDaoIntegrationTest {
    private CompanyDao companyDao;

    @BeforeMethod
    public void setUpDao() {
        companyDao = context.getBean(CompanyDao.class);
    }

    @Test
    public void save_and_findById() {
        Company company = new Company();
        company.setName("Test Company");
        company.setAddress("Test Address");

        companyDao.save(company);

        Company found = companyDao.findById(company.getId());

        assertNotNull(found);
        assertEquals(found.getName(), "Test Company");
        assertEquals(found.getAddress(), "Test Address");
    }

    @Test
    public void findById_shouldReturnNull_whenCompanyNotExists() {
        Company found = companyDao.findById(999999L);
        assertNull(found);
    }

    @Test
    public void findAll_shouldReturnSorted() {
        Company c1 = new Company();
        c1.setName("B Company");
        c1.setAddress("Addr1");

        Company c2 = new Company();
        c2.setName("A Company");
        c2.setAddress("Addr2");

        companyDao.save(c1);
        companyDao.save(c2);

        List<Company> list = companyDao.findAll();

        assertEquals(list.size(), 2);
        assertEquals(list.get(0).getName(), "A Company");
        assertEquals(list.get(1).getName(), "B Company");
    }

    @Test
    public void update_shouldUpdateCompany() {
        Company company = new Company();
        company.setName("Old Name");
        company.setAddress("Old Address");
        companyDao.save(company);

        company.setName("New Name");
        company.setAddress("New Address");

        companyDao.update(company);

        Company updated = companyDao.findById(company.getId());

        assertNotNull(updated);
        assertEquals(updated.getName(), "New Name");
        assertEquals(updated.getAddress(), "New Address");
    }

    @Test
    public void delete_shouldRemoveManagedCompany() {
        Company company = new Company();
        company.setName("Delete Me");
        company.setAddress("Addr");
        companyDao.save(company);

        Long id = company.getId();

        companyDao.delete(company);

        Company deleted = companyDao.findById(id);
        assertNull(deleted);
    }

    @Test
    public void delete_shouldRemoveDetachedCompany() {
        Company company = new Company();
        company.setName("Detach");
        company.setAddress("Addr");
        companyDao.save(company);

        Long id = company.getId();

        Company detached = new Company();
        detached.setId(id);
        detached.setName("Detach");
        detached.setAddress("Addr");

        companyDao.delete(detached);

        Company deleted = companyDao.findById(id);
        assertNull(deleted);
    }

    @Test
    public void deleteById_shouldDeleteCompany() {
        Company company = new Company();
        company.setName("To delete");
        company.setAddress("Addr");
        companyDao.save(company);

        Long id = company.getId();

        companyDao.deleteById(id);

        Company deleted = companyDao.findById(id);
        assertNull(deleted);
    }

    @Test
    public void deleteById_shouldDoNothing_whenCompanyNotExists() {
        companyDao.deleteById(999999L);

        List<Company> list = companyDao.findAll();
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }
}