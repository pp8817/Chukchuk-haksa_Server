package com.chukchuk.haksa.domain.course.repository;

import com.chukchuk.haksa.domain.course.model.CourseOffering;
import com.chukchuk.haksa.domain.course.model.FacultyDivision;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/** 과목 offering repository 기능의 계약을 정의한다. */
public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long> {
  /**
   * 척척학사의 find by 과목 id and year and 학기 and class section and professor id and faculty division
   * name and host 학과 대상을 조회한다.
   *
   * @param courseId 과목 식별자
   * @param year 연도
   * @param semester 학기 값
   * @param classSection 분반
   * @param professorId 교수 식별자
   * @param facultyDivisionName faculty division 이름
   * @param hostDepartment 주관 학과
   * @return 조회
   */
  @Query(
      """
    SELECT o FROM CourseOffering o
    WHERE o.course.id = :courseId
      AND o.year = :year
      AND o.semester = :semester
      AND o.classSection = :classSection
      AND o.professor.id = :professorId
      AND o.facultyDivisionName = :facultyDivisionName
      AND o.hostDepartment = :hostDepartment
      """)
  Optional<CourseOffering> findMatchingOffering(
      Long courseId,
      Integer year,
      Integer semester,
      String classSection,
      Long professorId,
      FacultyDivision facultyDivisionName,
      String hostDepartment);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param courseIds 과목 ids 식별자
   * @param years years 값
   * @param semesters semesters 값
   * @return 조회
   */
  @Query(
      """
    SELECT o FROM CourseOffering o
    WHERE o.course.id IN :courseIds
      AND o.year IN :years
      AND o.semester IN :semesters
      """)
  List<CourseOffering> findByCourseIdInAndYearInAndSemesterIn(
      Collection<Long> courseIds, Collection<Integer> years, Collection<Integer> semesters);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param keyword 검색어
   * @param area area 값
   * @param year 연도
   * @param semester 학기 값
   * @param departmentName 학과 이름
   * @return 조회
   */
  @Query(
      """
        SELECT o FROM CourseOffering o
        JOIN FETCH o.course c
        LEFT JOIN FETCH o.department d
        LEFT JOIN FETCH o.professor p
        WHERE o.deletedAt IS NULL
          AND (:keyword IS NULL
               OR LOWER(c.courseName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:area IS NULL OR o.facultyDivisionName = :area)
          AND (:year IS NULL OR o.year = :year)
          AND (:semester IS NULL OR o.semester = :semester)
          AND (:departmentName IS NULL
               OR d.establishedDepartmentName = :departmentName
               OR o.hostDepartment = :departmentName)
        ORDER BY o.year DESC, o.semester DESC, c.courseName ASC
      """)
  List<CourseOffering> searchAdminCandidates(
      String keyword, FacultyDivision area, Integer year, Integer semester, String departmentName);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param year 연도
   * @param semester 학기 값
   * @return 조회
   */
  @Query(
      """
        SELECT o FROM CourseOffering o
        JOIN FETCH o.course c
        JOIN FETCH o.professor p
        LEFT JOIN FETCH o.department d
        LEFT JOIN FETCH o.liberalArtsAreaCode lac
        WHERE o.deletedAt IS NULL
          AND o.year = :year
          AND o.semester = :semester
        ORDER BY c.courseName ASC, p.professorName ASC, o.id ASC
      """)
  List<CourseOffering> findReusableLectureEvaluationTestOfferings(Integer year, Integer semester);

  /**
   * 대상 학기의 과목 개설 강의평가 유형을 미확인 상태로 초기화한다.
   *
   * @param year 연도
   * @param semester 학기 값
   * @return int
   */
  @Modifying
  @Query(
      value =
          """
        UPDATE course_offerings
        SET evaluation_type_code = 'UNKNOWN'
        WHERE deleted_at IS NULL
          AND year = :year
          AND semester = :semester
          AND (
              evaluation_type_code IS NULL
              OR evaluation_type_code NOT IN ('ABSOLUTE', 'RELATIVE', 'UNKNOWN')
          )
          """,
      nativeQuery = true)
  int normalizeUnsupportedEvaluationTypes(Integer year, Integer semester);
}
