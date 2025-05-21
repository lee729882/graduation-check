package com.mnu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mnu.entity.Student;

public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findByAdmissionYear(int year);
}