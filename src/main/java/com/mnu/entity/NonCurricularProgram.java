package com.mnu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "non_curricular_program")
public class NonCurricularProgram {
    @Id
    private Long programId;

    private String programName;
    private String isRequired;
    private int creditsRecognized;
}
