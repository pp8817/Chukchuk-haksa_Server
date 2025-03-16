package com.chukchuk.haksa.domain.academic.record.controller;

import com.chukchuk.haksa.domain.academic.record.dto.AcademicRecordResponse;
import com.chukchuk.haksa.domain.academic.record.service.AcademicRecordService;
import com.chukchuk.haksa.domain.academic.record.service.SemesterAcademicRecordService;
import com.chukchuk.haksa.domain.student.dto.StudentSemesterDto;
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
@RequestMapping("/api")
@RequiredArgsConstructor
public class AcademicRecordController {

    private final AcademicRecordService academicRecordService;
    private final SemesterAcademicRecordService semesterAcademicRecordService;

    /* 학기별 성적 및 수강 과목 정보 조회 API
    * API 경로 변경 제안
    * param으로 들어오는 year, semester의 Type을 String -> int로 변경 제안
    *  */
    @GetMapping("/get-academic") // Restful 방식으로 변경 제안: /api/academic-record
    public ResponseEntity<AcademicRecordResponse> getAcademicRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Integer year,
            @RequestParam Integer semester) {

        String email = userDetails.getUsername();

        AcademicRecordResponse response = academicRecordService.getAcademicRecord(email, year, semester);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-semesters") //semester 불러오는 controller, 병합
    public ResponseEntity<List<StudentSemesterDto.StudentSemesterInfoDto>> getSemesterRecord(
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        List<StudentSemesterDto.StudentSemesterInfoDto> response = semesterAcademicRecordService.getStudentSemester(email);

        return ResponseEntity.ok(response);
    }

}
