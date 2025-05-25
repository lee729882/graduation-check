package com.mnu.dto;

import java.util.List;
import java.util.Map;

public class GraduationStatusDTO {

    // 이수 학점
    private int generalEducationCredits;
    private int engineeringBasicsCredits;
    private int majorRequiredCredits;
    private int majorElectiveCredits;

    // 요구 학점 (입학년도/전공별)
    private int requiredGeneralEducationCredits;
    private int requiredEngineeringBasicsCredits;
    private int requiredMajorRequiredCredits;
    private int requiredMajorElectiveCredits;

    // 전공필수 미이수 과목
    private List<CourseDTO> missingRequiredCourses;

    // 비교과 프로그램 (이름, 이수 여부)
    private List<NonCurricularStatus> nonCurricularPrograms;

    // 비교과 전체 이수 여부
    private boolean nonCurricularOk;

    // 학기별 이수 과목 목록
    private Map<String, List<TakenCourseDTO>> takenBySemester;

    // ======== 내부 클래스 ========

    public static class CourseDTO {
        private String courseCode;
        private String courseName;

        // Getters & Setters
        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
    }

    public static class NonCurricularStatus {
        private String programName;
        private boolean completed;

        // Getters & Setters
        public String getProgramName() { return programName; }
        public void setProgramName(String programName) { this.programName = programName; }

        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }

    // ======== 전체 DTO Getter/Setter ========

    public int getGeneralEducationCredits() {
        return generalEducationCredits;
    }

    public void setGeneralEducationCredits(int generalEducationCredits) {
        this.generalEducationCredits = generalEducationCredits;
    }

    public int getEngineeringBasicsCredits() {
        return engineeringBasicsCredits;
    }

    public void setEngineeringBasicsCredits(int engineeringBasicsCredits) {
        this.engineeringBasicsCredits = engineeringBasicsCredits;
    }

    public int getMajorRequiredCredits() {
        return majorRequiredCredits;
    }

    public void setMajorRequiredCredits(int majorRequiredCredits) {
        this.majorRequiredCredits = majorRequiredCredits;
    }

    public int getMajorElectiveCredits() {
        return majorElectiveCredits;
    }

    public void setMajorElectiveCredits(int majorElectiveCredits) {
        this.majorElectiveCredits = majorElectiveCredits;
    }

    public int getRequiredGeneralEducationCredits() {
        return requiredGeneralEducationCredits;
    }

    public void setRequiredGeneralEducationCredits(int requiredGeneralEducationCredits) {
        this.requiredGeneralEducationCredits = requiredGeneralEducationCredits;
    }

    public int getRequiredEngineeringBasicsCredits() {
        return requiredEngineeringBasicsCredits;
    }

    public void setRequiredEngineeringBasicsCredits(int requiredEngineeringBasicsCredits) {
        this.requiredEngineeringBasicsCredits = requiredEngineeringBasicsCredits;
    }

    public int getRequiredMajorRequiredCredits() {
        return requiredMajorRequiredCredits;
    }

    public void setRequiredMajorRequiredCredits(int requiredMajorRequiredCredits) {
        this.requiredMajorRequiredCredits = requiredMajorRequiredCredits;
    }

    public int getRequiredMajorElectiveCredits() {
        return requiredMajorElectiveCredits;
    }

    public void setRequiredMajorElectiveCredits(int requiredMajorElectiveCredits) {
        this.requiredMajorElectiveCredits = requiredMajorElectiveCredits;
    }

    public List<CourseDTO> getMissingRequiredCourses() {
        return missingRequiredCourses;
    }

    public void setMissingRequiredCourses(List<CourseDTO> missingRequiredCourses) {
        this.missingRequiredCourses = missingRequiredCourses;
    }

    public List<NonCurricularStatus> getNonCurricularPrograms() {
        return nonCurricularPrograms;
    }

    public void setNonCurricularPrograms(List<NonCurricularStatus> nonCurricularPrograms) {
        this.nonCurricularPrograms = nonCurricularPrograms;
    }

    public boolean isNonCurricularOk() {
        return nonCurricularOk;
    }

    public void setNonCurricularOk(boolean nonCurricularOk) {
        this.nonCurricularOk = nonCurricularOk;
    }

    public Map<String, List<TakenCourseDTO>> getTakenBySemester() {
        return takenBySemester;
    }

    public void setTakenBySemester(Map<String, List<TakenCourseDTO>> takenBySemester) {
        this.takenBySemester = takenBySemester;
    }
}
