package com.mnu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "student")
public class Student {

    @Id
    private String studentId;

    private String name;
    private String major;
    private int admissionYear;
    private String password;
}
