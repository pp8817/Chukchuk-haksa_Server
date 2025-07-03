package com.chukchuk.haksa.domain.graduation.repository;

import com.chukchuk.haksa.domain.course.model.FacultyDivision;
import com.chukchuk.haksa.domain.graduation.dto.AreaProgressDto;
import com.chukchuk.haksa.domain.graduation.dto.CourseDto;
import com.chukchuk.haksa.domain.requirement.model.MajorRole;
import com.chukchuk.haksa.global.exception.CommonException;
import com.chukchuk.haksa.global.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GraduationQueryRepository {
    private final EntityManager em;
    private final ObjectMapper ob;

    /* 필요 졸업 학점 계산 로직 */
    @Cacheable(
            cacheNames = "requiredCredits",
            key = "#departmentId + '_' + #admissionYear"
    )
    public Integer getTotalRequiredGraduationCredits(Long departmentId, Integer admissionYear) {
        String sql = """
        SELECT COALESCE(SUM(dar.required_credits), 0)
        FROM department_area_requirements dar
        WHERE dar.department_id = :departmentId
          AND dar.admission_year = :admissionYear
    """;

        Query query = em.createNativeQuery(sql);
        query.setParameter("departmentId", departmentId);
        query.setParameter("admissionYear", admissionYear);

        Object result = query.getSingleResult();
        return ((Number) result).intValue();
    }

    // TODO: Join 방식 변경 필요 -> 현재 방식은 Join 범위가 넓기 때문에 데이터가 많이질수록 비효율적
    public List<AreaProgressDto> getStudentAreaProgress(UUID studentId, Long departmentId, Integer admissionYear) {
        String sql = """
    WITH latest_courses AS (
        SELECT DISTINCT ON (c.course_code)
            sc.offering_id,
            co.faculty_division_name AS area_type,
            co.points,
            sc.grade,
            c.course_name,
            co.semester,
            co.year,
            c.course_code,
            sc.original_score
        FROM student_courses sc
        JOIN course_offerings co ON sc.offering_id = co.id
        JOIN courses c ON co.course_id = c.id
        WHERE sc.grade != 'F'
          AND sc.student_id = :studentId
        ORDER BY c.course_code, sc.original_score DESC
    ),
    area_requirements AS (
        SELECT 
            dar.area_type,
            dar.required_credits,
            dar.required_elective_courses,
            dar.total_elective_courses
        FROM department_area_requirements dar
        WHERE dar.department_id = :effectiveDepartmentId
          AND dar.admission_year = :admissionYear
    ),
    aggregated_progress AS (
        SELECT 
            ar.area_type AS areaType,
            ar.required_credits AS requiredCredits,
            CAST(COALESCE(SUM(lc.points), 0) AS INTEGER) AS earnedCredits, -- ✅ 수정
            ar.required_elective_courses AS requiredElectiveCourses,
            CAST(COUNT(DISTINCT lc.offering_id) AS INTEGER) AS completedElectiveCourses, -- ✅ 수정
            ar.total_elective_courses AS totalElectiveCourses
        FROM area_requirements ar
        LEFT JOIN latest_courses lc ON lc.area_type = ar.area_type
        GROUP BY 
            ar.area_type,
            ar.required_credits,
            ar.required_elective_courses,
            ar.total_elective_courses
    )
    SELECT 
        ap.*,
        CASE 
            WHEN COUNT(lc.offering_id) = 0 THEN NULL 
            ELSE CAST(json_agg(
                json_build_object(
                    'courseName', lc.course_name,
                    'credits', lc.points,
                    'grade', lc.grade,
                    'semester', lc.semester,
                    'year', lc.year,
                    'originalScore', lc.original_score
                )
            ) AS TEXT) -- ✅ 수정
        END AS courses
    FROM aggregated_progress ap
    LEFT JOIN latest_courses lc ON lc.area_type = ap.areaType
    GROUP BY ap.areaType, ap.requiredCredits, ap.earnedCredits, 
             ap.requiredElectiveCourses, ap.completedElectiveCourses, ap.totalElectiveCourses;
""";

        Query query = em.createNativeQuery(sql);
        query.setParameter("studentId", studentId);
        query.setParameter("effectiveDepartmentId", departmentId);
        query.setParameter("admissionYear", admissionYear);

        List<Object[]> results = query.getResultList();

        if (results.isEmpty()) {
            throw new CommonException(ErrorCode.GRADUATION_REQUIREMENTS_NOT_FOUND);
        }

        return results.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    /* 복수전공 요건 조회 */
    public List<AreaProgressDto> getDualMajorAreaProgress(UUID studentId, Long departmentId, MajorRole majorRole) {
        String sql = """
        SELECT
            dmr.area_type,
            dmr.required_credits,
            CAST(COALESCE(SUM(co.points), 0) AS INTEGER) AS earned_credits,
            0 AS required_elective_courses,
            0 AS completed_elective_courses,
            0 AS total_elective_courses,
            CASE
                WHEN COUNT(sc.offering_id) = 0 THEN NULL
                ELSE CAST(json_agg(
                    json_build_object(
                        'courseName', c.course_name,
                        'credits', co.points,
                        'grade', sc.grade,
                        'semester', co.semester,
                        'year', co.year,
                        'originalScore', sc.original_score
                    )
                ) AS TEXT)
            END AS courses
        FROM student_courses sc
        JOIN course_offerings co ON sc.offering_id = co.id
        JOIN courses c ON co.course_id = c.id
        JOIN dual_major_requirements dmr
          ON dmr.department_id = :departmentId
         AND dmr.major_role = :majorRole
         AND dmr.area_type = co.faculty_division_name
        WHERE sc.student_id = :studentId
          AND sc.grade != 'F'
        GROUP BY dmr.area_type, dmr.required_credits
    """;

        Query query = em.createNativeQuery(sql);
        query.setParameter("studentId", studentId);
        query.setParameter("departmentId", departmentId);
        query.setParameter("majorRole", majorRole.name());

        List<Object[]> results = query.getResultList();

        if (results.isEmpty()) {
            throw new CommonException(ErrorCode.GRADUATION_REQUIREMENTS_NOT_FOUND);
        }

        return results.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    /* 전공기초 교양 조회 */
    public AreaProgressDto getBasicCourseProgress(UUID studentId, Long departmentId) {
        String sql = """
        SELECT
            '전교' AS area_type,
            mbr.required_credits,
            CAST(COALESCE(SUM(co.points), 0) AS INTEGER) AS earned_credits,
            0, 0, 0,
            CASE
                WHEN COUNT(sc.offering_id) = 0 THEN NULL
                ELSE CAST(json_agg(
                    json_build_object(
                        'courseName', c.course_name,
                        'credits', co.points,
                        'grade', sc.grade,
                        'semester', co.semester,
                        'year', co.year,
                        'originalScore', sc.original_score
                    )
                ) AS TEXT)
            END AS courses
        FROM student_courses sc
        JOIN course_offerings co ON sc.offering_id = co.id
        JOIN courses c ON co.course_id = c.id
        JOIN major_basic_requirements mbr ON mbr.department_id = :departmentId
        WHERE sc.student_id = :studentId
          AND sc.grade != 'F'
          AND co.faculty_division_name = '전교'
        GROUP BY mbr.required_credits
    """;

        Query query = em.createNativeQuery(sql);
        query.setParameter("studentId", studentId);
        query.setParameter("departmentId", departmentId);

        Object[] row = (Object[]) query.getSingleResult();
        return mapToDto(row);
    }

    /******/

    private AreaProgressDto mapToDto(Object[] row) {
        FacultyDivision areaType = FacultyDivision.valueOf((String) row[0]);

        Integer requiredCredits = (Integer) row[1];
        Integer earnedCredits = (Integer) row[2];
        Integer requiredElectiveCourses = (Integer) row[3];
        Integer completedElectiveCourses = (Integer) row[4];
        Integer totalElectiveCourses = (Integer) row[5];

        // JSON 데이터 변환
        List<CourseDto> courses = new ArrayList<>();
        if (row[6] != null) {
            try {
                courses = ob.readValue((String) row[6], new TypeReference<List<CourseDto>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON 변환 오류", e);
            }
        }

        return new AreaProgressDto(areaType, requiredCredits, earnedCredits, requiredElectiveCourses, completedElectiveCourses, totalElectiveCourses, courses);
    }
}
