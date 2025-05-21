package com.mnu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mnu.entity.NonCurricularProgram;

import java.util.List;

public interface NonCurricularProgramRepository extends JpaRepository<NonCurricularProgram, Long> {

    @Query("SELECT n.programName, s.completed " +
           "FROM NonCurricularProgram n " +
           "LEFT JOIN StudentProgram s ON n.programId = s.programId AND s.studentId = :studentId")
    List<Object[]> checkProgramStatus(@Param("studentId") String studentId);
}
