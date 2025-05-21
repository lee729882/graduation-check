package com.mnu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "graduation_requirement")
public class GraduationRequirement {
    @Id
    private Long id;

    private String major;
    private String category;
    private int requiredCredits;
    private int admissionYear;
}

