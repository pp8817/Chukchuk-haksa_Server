package com.chukchuk.haksa.infrastructure.portal.mapper;

import com.chukchuk.haksa.application.academic.AcademicSummary;
import com.chukchuk.haksa.application.academic.SemesterGrade;
import com.chukchuk.haksa.domain.academic.record.model.SemesterAcademicRecord;
import com.chukchuk.haksa.domain.academic.record.model.StudentAcademicRecord;
import com.chukchuk.haksa.domain.student.model.Student;

import java.math.BigDecimal;

public class AcademicRecordMapper {

    public static StudentAcademicRecord toEntity(Student student, AcademicSummary summary) {
        return new StudentAcademicRecord(
                student,
                summary.getTotalAttemptedCredits(),
                summary.getTotalEarnedCredits(),
                BigDecimal.valueOf(summary.getCumulativeGpa()),
                BigDecimal.valueOf(summary.getPercentile())
        );
    }

    public static SemesterAcademicRecord toEntity(Student student, SemesterGrade grade) {
        return new SemesterAcademicRecord(
                student,
                grade.getYear(),
                grade.getSemester(),
                grade.getAttemptedCredits(),
                grade.getEarnedCredits(),
                BigDecimal.valueOf(grade.getSemesterGpa()),
                BigDecimal.valueOf(grade.getSemesterPercentile()),
                BigDecimal.valueOf(grade.getAttemptedCreditsGpa()),
                grade.getClassRank(),
                grade.getTotalStudents()
        );
    }
}