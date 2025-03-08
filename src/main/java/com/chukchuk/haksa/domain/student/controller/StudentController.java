package com.chukchuk.haksa.domain.student.controller;

import com.chukchuk.haksa.domain.academic.record.dto.SemesterAcademicRecordDto;
import com.chukchuk.haksa.domain.academic.record.service.SemesterAcademicRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/semester")
@RequiredArgsConstructor
public class StudentController {
    private final SemesterAcademicRecordService semesterAcademicRecordService;

    @GetMapping("/get-semesters")
    public ResponseEntity<List<SemesterAcademicRecordDto.SemesterGradeDto>> getSemesterRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("year") int year,
            @RequestParam("semester") int semester) {

        String email = userDetails.getUsername();
        List<SemesterAcademicRecordDto.SemesterGradeDto> response = semesterAcademicRecordService.getSemesterGrades(email, year, semester);

        return ResponseEntity.ok(response);
    }
}
