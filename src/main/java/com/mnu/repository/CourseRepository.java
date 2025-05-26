package com.mnu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mnu.entity.Course;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, String> {

    @Query("SELECT c FROM Course c WHERE c.required = 'Y' AND c.category = '전공필수' " +
           "AND c.courseCode NOT IN (SELECT tc.courseCode FROM TakenCourse tc WHERE tc.studentId = :studentId AND tc.completed = 'Y')")
    List<Course> findUncompletedRequired(@Param("studentId") String studentId);
    @Query("""
    	    SELECT c FROM Course c
    	    WHERE c.category = '전문교양' 
    	    AND c.courseCode NOT IN (
    	        SELECT tc.courseCode 
    	        FROM TakenCourse tc 
    	        WHERE tc.studentId = :studentId AND tc.completed = 'Y'
    	    )
    	""")
    	List<Course> findUncompletedGeneralEducation(@Param("studentId") String studentId);

}
