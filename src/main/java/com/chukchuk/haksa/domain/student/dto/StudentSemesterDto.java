package com.chukchuk.haksa.domain.student.dto;

import com.chukchuk.haksa.domain.academic.record.model.SemesterAcademicRecord;

//year, semester를 받아오기 위한 DTO
public class StudentSemesterDto {
    public record StudentSemesterInfoDto(
            int year,
            int semester
    ){
        public static StudentSemesterInfoDto from(SemesterAcademicRecord record){
            return new StudentSemesterInfoDto(
                    record.getYear(),
                    record.getSemester()
            );
        }
    }

}
