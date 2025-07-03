package com.chukchuk.haksa.domain.graduation.service;

import com.chukchuk.haksa.domain.course.model.FacultyDivision;
import com.chukchuk.haksa.domain.graduation.dto.AreaProgressDto;
import com.chukchuk.haksa.domain.graduation.dto.GraduationProgressResponse;
import com.chukchuk.haksa.domain.graduation.repository.GraduationQueryRepository;
import com.chukchuk.haksa.domain.requirement.model.MajorRole;
import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.service.StudentService;
import com.chukchuk.haksa.infrastructure.redis.RedisCacheStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GraduationService {

    private final StudentService studentService;
    private final GraduationQueryRepository graduationQueryRepository;
    private final RedisCacheStore redisCacheStore;


    /* 졸업 요건 진행 상황 조회 */
    public GraduationProgressResponse getGraduationProgress(UUID studentId) {

        try {
            GraduationProgressResponse cached = redisCacheStore.getGraduationProgress(studentId);
            if (cached != null) {
                return cached;
            }
        } catch (Exception e) {
            // Redis 장애 시 로그 남기고 계속 진행
            log.warn("Redis cache retrieval failed for studentId: {}", studentId, e);
        }

        Student student = studentService.getStudentById(studentId);
        GraduationProgressResponse response;

        if (student.hasSecondaryMajor()) {
            response = getDualMajorGraduationProgress(student);
        } else {
            response = getSingleMajorGraduationProgress(student);
        }

        try {
            redisCacheStore.setGraduationProgress(studentId, response);
        } catch (Exception e) {
            log.warn("Redis 캐시 저장 실패 - studentId: {}", studentId, e);
        }

        return response;
    }

    /* 단일 전공자 처리 */
    private GraduationProgressResponse getSingleMajorGraduationProgress(Student student) {
        Long departmentId = student.getMajorOrDepartmentId();
        Integer admissionYear = student.getAcademicInfo().getAdmissionYear();

        List<AreaProgressDto> areaList = graduationQueryRepository.getStudentAreaProgress(
                student.getId(), departmentId, admissionYear
        );

        return new GraduationProgressResponse(areaList);
    }

    /**
     *  복수 전공자 처리
     *  TODO: 1전공, 2전공에서 겹치는 전공 기초 교양 존재하는 경우 예외 처리 추가 필요
     *  TODO: AreaProgressDto에 majorType (MAJOR1 / MAJOR2) 필드 추가 필요
     *  */

    private GraduationProgressResponse getDualMajorGraduationProgress(Student student) {
        UUID studentId = student.getId();
        Integer admissionYear = student.getAcademicInfo().getAdmissionYear();

        // 1전공 정보
        Long major1Id = student.getMajorOrDepartmentId();
        List<AreaProgressDto> major1List = graduationQueryRepository.getStudentAreaProgress(
                studentId, major1Id, admissionYear
        );
        major1List = filterOutMajorElective(major1List); // 전공선택 제거
        major1List.addAll(graduationQueryRepository.getDualMajorAreaProgress(studentId, major1Id, MajorRole.PRIMARY)); // 1전공 전공 선택
        major1List.add(graduationQueryRepository.getBasicCourseProgress(studentId, major1Id)); // 과목 리스트 포함된 전공기초교양
//        major1List.forEach(dto -> dto.setMajorType("MAJOR1"));

        // 2전공 정보
        Long major2Id = student.getSecondaryMajor().getId();
        List<AreaProgressDto> major2List = graduationQueryRepository.getDualMajorAreaProgress(studentId, major2Id, MajorRole.SECONDARY);
        major2List.add(graduationQueryRepository.getBasicCourseProgress(studentId, major2Id)); // 과목 리스트 포함된 전공기초교양
//        major2List.forEach(dto -> dto.setMajorType("MAJOR2"));

        // 병합
        List<AreaProgressDto> combined = new ArrayList<>();
        combined.addAll(major1List);
        combined.addAll(major2List);

        return new GraduationProgressResponse(combined);
    }

    private List<AreaProgressDto> filterOutMajorElective(List<AreaProgressDto> list) {
        return list.stream()
                .filter(dto -> !dto.getAreaType().equals(FacultyDivision.전선))
                .toList();
    }
}
