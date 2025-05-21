package com.mnu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "student_program")
public class StudentProgram {
    @Id
    private Long id;

    private String studentId;
    private Long programId;
    private String completed;
}
