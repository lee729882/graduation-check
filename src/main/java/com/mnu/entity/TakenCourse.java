package com.mnu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "taken_course")
public class TakenCourse {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "taken_course_seq_gen")
	@SequenceGenerator(name = "taken_course_seq_gen", sequenceName = "taken_course_seq", allocationSize = 1)
	private Long id;

    private String studentId;
    private String courseCode;
    private String grade;
    
    @Column(nullable = false, columnDefinition = "CHAR(1) DEFAULT 'Y'")
    private String completed;   
    
    private String semester;

}
