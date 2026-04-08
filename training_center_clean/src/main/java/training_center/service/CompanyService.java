package training_center.service;

import java.util.List;

import org.springframework.stereotype.Service;

import training_center.dao.CompanyDao;
import training_center.entity.Company;

@Service
public class CompanyService {

    private final CompanyDao companyDao;

    public CompanyService(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    public List<Company> getAllCompanies() {
        return companyDao.findAll();
    }

    public Company getCompanyById(Long id) {
        return companyDao.findById(id);
    }

    public void createCompany(String name, String address) {
        Company company = new Company(name, address);
        companyDao.save(company);
    }
}
