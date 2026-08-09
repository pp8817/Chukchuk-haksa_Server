package com.chukchuk.haksa.domain.course.service;

import com.chukchuk.haksa.domain.course.dto.CreateOfferingCommand;
import com.chukchuk.haksa.domain.course.model.Course;
import com.chukchuk.haksa.domain.course.model.CourseOffering;
import com.chukchuk.haksa.domain.course.model.EvaluationType;
import com.chukchuk.haksa.domain.course.model.FacultyDivision;
import com.chukchuk.haksa.domain.course.model.LiberalArtsAreaCode;
import com.chukchuk.haksa.domain.course.repository.CourseOfferingRepository;
import com.chukchuk.haksa.domain.course.repository.CourseRepository;
import com.chukchuk.haksa.domain.course.repository.LiberalArtsAreaCodeRepository;
import com.chukchuk.haksa.domain.department.model.Department;
import com.chukchuk.haksa.domain.department.repository.DepartmentRepository;
import com.chukchuk.haksa.domain.professor.model.Professor;
import com.chukchuk.haksa.domain.professor.repository.ProfessorRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 과목 offering 비즈니스 흐름을 처리한다. */
@Service
@RequiredArgsConstructor
public class CourseOfferingService {

  private final CourseOfferingRepository courseOfferingRepository;
  private final CourseRepository courseRepository;
  private final LiberalArtsAreaCodeRepository liberalArtsAreaCodeRepository;
  private final ProfessorRepository professorRepository;
  private final DepartmentRepository departmentRepository;

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param cmd cmd 값
   * @return 조회
   */
  @Transactional
  public CourseOffering getOrCreateOffering(CreateOfferingCommand cmd) {
    return getOrCreateAll(List.of(cmd)).get(CourseOfferingKey.from(cmd));
  }

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param commands commands 값
   * @return 조회
   */
  @Transactional
  public Map<CourseOfferingKey, CourseOffering> getOrCreateAll(
      List<CreateOfferingCommand> commands) {
    if (commands == null || commands.isEmpty()) {
      return Map.of();
    }

    Map<CourseOfferingKey, CreateOfferingCommand> commandByKey = new HashMap<>();
    for (CreateOfferingCommand command : commands) {
      commandByKey.put(CourseOfferingKey.from(command), command);
    }
    registerAreaCodes(commandByKey.values());

    Set<Long> courseIds =
        commandByKey.keySet().stream().map(CourseOfferingKey::courseId).collect(Collectors.toSet());
    Set<Integer> years =
        commandByKey.keySet().stream().map(CourseOfferingKey::year).collect(Collectors.toSet());
    Set<Integer> semesters =
        commandByKey.keySet().stream().map(CourseOfferingKey::semester).collect(Collectors.toSet());

    List<CourseOffering> existing =
        courseOfferingRepository.findByCourseIdInAndYearInAndSemesterIn(
            courseIds, years, semesters);
    Map<CourseOfferingKey, CourseOffering> result = new HashMap<>();
    for (CourseOffering offering : existing) {
      CourseOfferingKey key = CourseOfferingKey.from(offering);
      if (commandByKey.containsKey(key)) {
        CreateOfferingCommand cmd = commandByKey.get(key);
        backfillMissionAreaCodeIfNeeded(offering, cmd);
        offering.backfillPoints(cmd.points());
        result.putIfAbsent(key, offering);
      }
    }

    List<CourseOffering> toInsert = new ArrayList<>();
    for (Map.Entry<CourseOfferingKey, CreateOfferingCommand> entry : commandByKey.entrySet()) {
      if (!result.containsKey(entry.getKey())) {
        CourseOffering created = createCourseOffering(entry.getValue());
        toInsert.add(created);
        result.put(entry.getKey(), created);
      }
    }

    if (!toInsert.isEmpty()) {
      courseOfferingRepository.saveAll(toInsert);
    }

    return result;
  }

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param offeringIds offering ids 식별자
   * @return 조회
   */
  @Transactional(readOnly = true)
  public Map<Long, CourseOffering> getOfferingMapByIds(List<Long> offeringIds) {
    return courseOfferingRepository.findAllById(offeringIds).stream()
        .collect(Collectors.toMap(CourseOffering::getId, o -> o));
  }

  private CourseOffering createCourseOffering(CreateOfferingCommand cmd) {
    Course course = courseRepository.getReferenceById(cmd.courseId());
    Professor professor = professorRepository.getReferenceById(cmd.professorId());
    Department department =
        cmd.departmentId() != null
            ? departmentRepository.getReferenceById(cmd.departmentId())
            : null;

    LiberalArtsAreaCode liberalArtsAreaCode = null;

    if (cmd.areaCode() != null && cmd.areaCode() != 0) {
      liberalArtsAreaCode = liberalArtsAreaCodeRepository.getReferenceById(cmd.areaCode());
    }

    EvaluationType evaluationType = resolveEvaluationType(cmd.evaluationType());
    FacultyDivisionResolution facultyDivision =
        FacultyDivisionResolution.from(cmd.facultyDivisionName());

    return new CourseOffering(
        cmd.subjectEstablishmentSemester(),
        cmd.isVideoLecture(),
        cmd.year(),
        cmd.semester(),
        cmd.hostDepartment(),
        cmd.classSection(),
        cmd.scheduleSummary(),
        cmd.originalAreaCode(),
        cmd.points(),
        evaluationType,
        facultyDivision.value(),
        facultyDivision.rawValue(),
        course,
        professor,
        department,
        liberalArtsAreaCode);
  }

  private EvaluationType resolveEvaluationType(String rawValue) {
    if (rawValue == null || rawValue.isBlank()) {
      return EvaluationType.UNKNOWN;
    }
    return EvaluationType.valueOf(rawValue);
  }

  private void registerAreaCodes(Collection<CreateOfferingCommand> commands) {
    commands.stream()
        .map(CreateOfferingCommand::areaCode)
        .filter(code -> code != null && code > 0)
        .collect(Collectors.toSet())
        .forEach(code -> liberalArtsAreaCodeRepository.insertIfAbsent(code, code + "영역"));
  }

  /**
   * 선교(미션) 영역의 historical NULL area_code 단방향 backfill (Issue #226 2차 작업).
   *
   * <p>4개 조건이 모두 충족될 때만 도메인 메서드를 호출한다:
   *
   * <ol>
   *   <li>{@code existing.facultyDivisionName == FacultyDivision.선교}
   *   <li>{@code existing.liberalArtsAreaCode == null}
   *   <li>{@code cmd.areaCode() != null}
   *   <li>{@code cmd.areaCode() != 0} (영역 코드 파싱 실패 시 0 반환을 거름)
   * </ol>
   *
   * <p>가드 통과 시 {@code @Transactional} dirty checking 으로 자동 UPDATE flush.
   */
  private void backfillMissionAreaCodeIfNeeded(CourseOffering existing, CreateOfferingCommand cmd) {
    if (!shouldBackfillMissionAreaCode(existing, cmd)) {
      return;
    }
    LiberalArtsAreaCode area = liberalArtsAreaCodeRepository.getReferenceById(cmd.areaCode());
    existing.backfillMissionLiberalAreaCode(area);
  }

  private boolean shouldBackfillMissionAreaCode(
      CourseOffering existing, CreateOfferingCommand cmd) {
    return existing.getFacultyDivisionName() == FacultyDivision.선교
        && existing.getLiberalArtsAreaCode() == null
        && cmd.areaCode() != null
        && cmd.areaCode() != 0;
  }

  /**
   * 과목 offering key 데이터를 전달한다.
   *
   * @param courseId 과목 식별자
   * @param year 연도
   * @param semester 학기 값
   * @param classSection 분반
   * @param professorId 교수 식별자
   * @param facultyDivisionName faculty division 이름
   * @param rawFacultyDivisionName raw faculty division 이름
   * @param hostDepartment 주관 학과
   */
  public record CourseOfferingKey(
      Long courseId,
      Integer year,
      Integer semester,
      String classSection,
      Long professorId,
      String facultyDivisionName,
      String rawFacultyDivisionName,
      String hostDepartment) {
    /**
     * 과목 개설 명령에서 중복 판별 키를 생성한다.
     *
     * @param cmd cmd 값
     * @return 과목 offering key 결과
     */
    public static CourseOfferingKey from(CreateOfferingCommand cmd) {
      FacultyDivisionResolution facultyDivision =
          FacultyDivisionResolution.from(cmd.facultyDivisionName());
      return new CourseOfferingKey(
          cmd.courseId(),
          cmd.year(),
          cmd.semester(),
          normalizeBlank(cmd.classSection()),
          cmd.professorId(),
          facultyDivision.value() != null ? facultyDivision.value().name() : null,
          facultyDivision.rawValue(),
          normalizeBlank(cmd.hostDepartment()));
    }

    /**
     * 과목 개설 엔티티에서 중복 판별 키를 생성한다.
     *
     * @param offering offering 값
     * @return 과목 offering key 결과
     */
    public static CourseOfferingKey from(CourseOffering offering) {
      return new CourseOfferingKey(
          offering.getCourse().getId(),
          offering.getYear(),
          offering.getSemester(),
          normalizeBlank(offering.getClassSection()),
          offering.getProfessor().getId(),
          offering.getFacultyDivisionName() != null
              ? normalizeBlank(offering.getFacultyDivisionName().name())
              : null,
          normalizeBlank(offering.getRawFacultyDivisionName()),
          normalizeBlank(offering.getHostDepartment()));
    }

    private static String normalizeBlank(String value) {
      if (value == null || value.isBlank()) {
        return null;
      }
      return value.trim();
    }
  }

  private record FacultyDivisionResolution(FacultyDivision value, String rawValue) {
    private static FacultyDivisionResolution from(String rawValue) {
      String normalized = CourseOfferingKey.normalizeBlank(rawValue);
      if (normalized == null) {
        return new FacultyDivisionResolution(null, null);
      }

      try {
        return new FacultyDivisionResolution(FacultyDivision.valueOf(normalized), null);
      } catch (IllegalArgumentException ignored) {
        return new FacultyDivisionResolution(FacultyDivision.기타, normalized);
      }
    }
  }
}
