package com.mnu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mnu.entity.GraduationRequirement;

import java.util.List;

public interface GraduationRequirementRepository extends JpaRepository<GraduationRequirement, Long> {
    List<GraduationRequirement> findByMajorAndAdmissionYear(String major, int admissionYear);
}
