package training_center.util;

import java.util.List;

import training_center.dao.CompanyDao;
import training_center.entity.Company;

public class testik {

    public static void main(String[] args) {
        CompanyDao companyDao = new CompanyDao();

        try {
            Company company = new Company("ООО Кашкин Роман", "г. Москва, ул. Старая Басманная, д. 31");
            companyDao.save(company);
            System.out.println("Компания сохранена.");
            List<Company> companies = companyDao.findAll();

            System.out.println("Список компаний:");
            for (Company c : companies) {
                System.out.println(
                        "ID: " + c.getId()
                        + ", Название: " + c.getName()
                        + ", Адрес: " + c.getAddress()
                );
            }

        } catch (Exception e) {
            System.out.println("Ошибка");
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}