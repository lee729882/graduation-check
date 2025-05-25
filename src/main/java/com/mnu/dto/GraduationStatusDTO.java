package com.mnu.dto;

import java.util.List;
import java.util.Map;

public class GraduationStatusDTO {
	
	// ===== 상단 정보 필드 추가 =====
	private String studentId;
	private String major;
	private int admissionYear;
	private double averageGrade;  // 평점 (4.5 기준)
	
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

    // 졸업 가능 여부 (모든 조건 충족 시 true)
    private boolean graduationAvailable;
    
    // 불합격 사유 목록
    private List<String> graduationFailReasons;

    // 졸업 학점과 취득 학점
    private int totalRequiredCredits;
    private int totalEarnedCredits;
    private boolean totalCreditsOk;
    
    // 기타 학점
    private int etcCredits;
    private int requiredEtcCredits;

    // ======== 내부 클래스 ========
    public List<String> getGraduationFailReasons() {
        return graduationFailReasons;
    }

    public void setGraduationFailReasons(List<String> graduationFailReasons) {
        this.graduationFailReasons = graduationFailReasons;
    }



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
    // Getter & Setter
    public boolean isGraduationAvailable() {
        return graduationAvailable;
    }

    public void setGraduationAvailable(boolean graduationAvailable) {
        this.graduationAvailable = graduationAvailable;
    }
 // ===== Getter/Setter 추가 =====
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public int getAdmissionYear() { return admissionYear; }
    public void setAdmissionYear(int admissionYear) { this.admissionYear = admissionYear; }

    public double getAverageGrade() { return averageGrade; }
    public void setAverageGrade(double averageGrade) { this.averageGrade = averageGrade; }

    public int getTotalRequiredCredits() {
        return totalRequiredCredits;
    }

    public void setTotalRequiredCredits(int totalRequiredCredits) {
        this.totalRequiredCredits = totalRequiredCredits;
    }

    public int getTotalEarnedCredits() {
        return totalEarnedCredits;
    }

    public void setTotalEarnedCredits(int totalEarnedCredits) {
        this.totalEarnedCredits = totalEarnedCredits;
    }

    public boolean isTotalCreditsOk() {
        return totalCreditsOk;
    }

    public void setTotalCreditsOk(boolean totalCreditsOk) {
        this.totalCreditsOk = totalCreditsOk;
    }

    public int getEtcCredits() {
        return etcCredits;
    }

    public void setEtcCredits(int etcCredits) {
        this.etcCredits = etcCredits;
    }

    public int getRequiredEtcCredits() {
        return requiredEtcCredits;
    }

    public void setRequiredEtcCredits(int requiredEtcCredits) {
        this.requiredEtcCredits = requiredEtcCredits;
    }

}
