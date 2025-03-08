package com.chukchuk.haksa.domain.student.controller;

import com.chukchuk.haksa.domain.academic.record.dto.AcademicRecordResponse;
import com.chukchuk.haksa.domain.academic.record.dto.SemesterAcademicRecordDto;
import com.chukchuk.haksa.domain.academic.record.model.SemesterAcademicRecord;
import com.chukchuk.haksa.domain.academic.record.service.SemesterAcademicRecordService;
import com.chukchuk.haksa.domain.user.service.UserService;
import com.chukchuk.haksa.global.exception.DataNotFoundException;
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
public class StudentController {
    private final SemesterAcademicRecordService semesterAcademicRecordService;
    private final UserService userService;

    @GetMapping("/get-semesters")
    public ResponseEntity<List<SemesterAcademicRecordDto.SemesterGradeDto>> getSemesterRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("year") int year,//Param 값 year는 int로 받아도 될 것 같음
            @RequestParam("semester") String semester) { //Param 값 semester는 String
        String email = userDetails.getUsername(); //UserDetail에서 email얻고
        UUID studentId = userService.getUserId(email); //받아온 email로 유저 UUID 구하기

        List<SemesterAcademicRecordDto.SemesterGradeDto> response = semesterAcademicRecordService.getSemesterGrades(studentId, year, Integer.parseInt(semester));
        //getSemesterRecords보다 getSemesterGrades를 쓰는것이 안전하고 예외처리도 가능 할 것 같아서 사용
        /*if (response.isEmpty()){
            throw new DataNotFoundException("신입생은 아직 학사 기록이 존재하지 않습니다.");
        }*/
        //이미 SemesterAcademicRecordService getSemesterGrades에서 예외 처리 하는데 따로 여기에서 해야할 필요성을 모르겠음
        //response.isEmpty() -> 학기 정보가 없는 것을 신입생으로 규정하고 예외처리를 할 것인지 신입생과 오류로인한 Empty를 구별할 것인지
        return ResponseEntity.ok(response);
    }
}
