package com.mnu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "taken_course")
public class TakenCourse {
    @Id
    private Long id;

    private String studentId;
    private String courseCode;
    private String grade;
    private String completed;
}
