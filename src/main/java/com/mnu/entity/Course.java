package com.mnu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "course")
@Data
public class Course {
    @Id
    private String courseCode;
    private String courseName;
    private int credit;
    private String category;
    private String required;  // 'Y' or 'N'
}
