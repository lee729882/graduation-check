package com.mnu.repository;

import com.mnu.entity.TakenCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TakenCourseRepository extends JpaRepository<TakenCourse, Long> {

    @Query("SELECT c.category, SUM(c.credit) " +
           "FROM TakenCourse tc JOIN Course c ON tc.courseCode = c.courseCode " +
           "WHERE tc.studentId = :studentId AND tc.completed = 'Y' " +
           "GROUP BY c.category")
    List<Object[]> sumCreditsByCategory(@Param("studentId") String studentId);
}
