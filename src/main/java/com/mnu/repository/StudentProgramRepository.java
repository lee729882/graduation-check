package com.mnu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mnu.entity.StudentProgram;

import java.util.List;

public interface StudentProgramRepository extends JpaRepository<StudentProgram, Long> {
    List<StudentProgram> findByStudentId(String studentId);
}
