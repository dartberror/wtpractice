package training_center.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import training_center.entity.Company;
import training_center.service.CompanyService;

@Controller
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/companies")
    public String companies(Model model) {
        model.addAttribute("companies", companyService.getAllCompanies());
        return "companies";
    }

    @GetMapping("/companies/new")
    public String newCompanyForm(Model model) {
        model.addAttribute("company", new Company());
        model.addAttribute("errorMessage", "");
        return "company-form";
    }

    @PostMapping("/companies")
    public String createCompany(@RequestParam("name") String name,
                                @RequestParam("address") String address,
                                Model model) {
        try {
            String validName = requireText(name, "Введите название организации");
            String validAddress = requireText(address, "Введите адрес организации");
            companyService.createCompany(validName, validAddress);
            return "redirect:/companies";
        } catch (RuntimeException ex) {
            Company company = new Company();
            company.setName(name);
            company.setAddress(address);
            model.addAttribute("company", company);
            model.addAttribute("errorMessage", ex.getMessage());
            return "company-form";
        }
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new RuntimeException(message);
        }
        return value.trim();
    }
}
