package com.chukchuk.haksa.domain.student.controller;

import com.chukchuk.haksa.domain.student.dto.StudentSemesterDto;
import com.chukchuk.haksa.domain.student.service.StudentSemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/semester")
@RequiredArgsConstructor
public class StudentSemesterController {
    private final StudentSemesterService studentSemesterService;

    @GetMapping("/get-semesters")
    public ResponseEntity<List<StudentSemesterDto.StudentSemesterInfoDto>> getSemesterRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("year") int year,
            @RequestParam("semester") int semester) { //parm 값이 필요한가 생각중 그냥 student ID로 긁어 오는 거니까, SemesterAcademicRecordRepository에 코드를 추가하는 것을 고려중

        String email = userDetails.getUsername();
        UUID studentId = studentSemesterService.getStudentId(email);//service에서 StudentID 얻어오게 로직 변경

        List<StudentSemesterDto.StudentSemesterInfoDto> response = studentSemesterService.getStudentSemester(studentId, year, semester);

        return ResponseEntity.ok(response);
    }
}