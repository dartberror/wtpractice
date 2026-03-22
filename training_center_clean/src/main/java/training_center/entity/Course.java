package training_center.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "duration_unit", nullable = false, length = 20)
    private String durationUnit;

    @Column(name = "duration_value", nullable = false)
    private Integer durationValue;

    @Column(name = "intensity", nullable = false)
    private Integer intensity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    public Course() {
    }

    public Course(String title, String durationUnit, Integer durationValue, Integer intensity, Company company) {
        this.title = title;
        this.durationUnit = durationUnit;
        this.durationValue = durationValue;
        this.intensity = intensity;
        this.company = company;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDurationUnit() {
        return durationUnit;
    }

    public Integer getDurationValue() {
        return durationValue;
    }

    public Integer getIntensity() {
        return intensity;
    }

    public Company getCompany() {
        return company;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
    }

    public void setDurationValue(Integer durationValue) {
        this.durationValue = durationValue;
    }

    public void setIntensity(Integer intensity) {
        this.intensity = intensity;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}