package com.chukchuk.haksa.domain.student.dto;

import com.chukchuk.haksa.domain.student.model.Student;

import java.math.BigDecimal;

//POST로 쓰는 targetGpa가 DTO가 필요한지 의문 -> 없애도 될 것 같지만 일단 작성함
public class StudentTargetGpaDto {
    public record TargetGpa(BigDecimal targetGpa)
    {
        public static TargetGpa from (Student student){
         return new TargetGpa(student.getTargetGpa());
         }
    }
}
