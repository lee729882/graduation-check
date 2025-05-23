package com.mnu.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfParserService {

	public Map<String, Object> parsePdf(MultipartFile pdfFile) {
	    Map<String, Object> result = new HashMap<>();
	    List<Map<String, String>> courses = new ArrayList<>();

	    try (PDDocument document = PDDocument.load(pdfFile.getInputStream())) {
	        String text = new PDFTextStripper().getText(document);
	        System.out.println("===== PDF Content =====\n" + text);

	        // 학번 추출
	        Pattern studentIdPattern = Pattern.compile("\\((\\d{6})\\s");
	        Matcher idMatcher = studentIdPattern.matcher(text);
	        if (idMatcher.find()) {
	            result.put("studentId", idMatcher.group(1));
	        }

	        // 줄 단위로 나누기
	        String[] lines = text.split("\r\n|\r|\n");
	        String currentSemester = ""; // 학기 정보 저장

	        Pattern semesterPattern = Pattern.compile("^(\\d{4}/\\d학기)");
	        Pattern courseLinePattern = Pattern.compile("([A-Z]{2}\\d{3})[^\\n]*?([A-F][+0]?)\\s");

	        for (String line : lines) {
	            Matcher semesterMatcher = semesterPattern.matcher(line);
	            if (semesterMatcher.find()) {
	                currentSemester = semesterMatcher.group(1); // 예: 2021/1학기
	            }

	            Matcher courseMatcher = courseLinePattern.matcher(line);
	            while (courseMatcher.find()) {
	                String code = courseMatcher.group(1);
	                String grade = courseMatcher.group(2);

	                Map<String, String> course = new HashMap<>();
	                course.put("courseCode", code);
	                course.put("grade", grade);
	                course.put("semester", currentSemester); // 학기 저장

	                courses.add(course);
	            }
	        }

	        result.put("courses", courses);

	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return result;
	}

}


