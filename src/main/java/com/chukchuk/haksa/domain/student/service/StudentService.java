package com.chukchuk.haksa.domain.student.service;

import com.chukchuk.haksa.domain.academic.record.repository.SemesterAcademicRecordRepository;
import com.chukchuk.haksa.domain.academic.record.repository.StudentAcademicRecordRepository;
import com.chukchuk.haksa.domain.academic.record.repository.StudentCourseRepository;
import com.chukchuk.haksa.domain.student.dto.StudentDto;
import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.repository.StudentRepository;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.repository.UserRepository;
import com.chukchuk.haksa.domain.user.service.UserService;
import com.chukchuk.haksa.global.exception.code.ErrorCode;
import com.chukchuk.haksa.global.exception.type.CommonException;
import com.chukchuk.haksa.global.exception.type.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 척척학사의 학생 비즈니스 흐름을 처리한다. */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

  private final StudentRepository studentRepository;
  private final UserRepository userRepository;
  private final UserService userService;
  private final StudentAcademicRecordRepository studentAcademicRecordRepository;
  private final SemesterAcademicRecordRepository semesterAcademicRecordRepository;
  private final StudentCourseRepository studentCourseRepository;

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param studentId 학생 식별자
   * @return 조회
   */
  public Student getStudentById(UUID studentId) {
    return studentRepository
        .findById(studentId)
        .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STUDENT_NOT_FOUND));
  }

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param userId 사용자 식별자
   * @return 조회
   */
  public Student getStudentByUserId(UUID userId) {
    User user = userService.getUserById(userId);

    return user.getStudent();
  }

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param studentCode 학번
   * @return 조회
   */
  public Optional<Student> findByStudentCode(String studentCode) {
    return studentRepository.findByStudentCode(studentCode);
  }

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param userId 사용자 식별자
   * @return 조회
   */
  public Optional<Student> findPortalPendingStudent(UUID userId) {
    return studentRepository.findPortalPendingStudent(userId);
  }

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param userId 사용자 식별자
   * @return 조회
   */
  public UUID getRequiredStudentIdByUserId(UUID userId) {
    Student student = getStudentByUserId(userId);
    if (student == null) {
      throw new CommonException(ErrorCode.USER_NOT_CONNECTED);
    }

    UUID studentId = student.getId();
    if (studentId == null) {
      throw new EntityNotFoundException(ErrorCode.STUDENT_NOT_FOUND);
    }

    return studentId;
  }

  /**
   * 사용자에 연결된 학생을 포털 재연동 상태로 변경한다.
   *
   * @param user 사용자 값
   */
  @Transactional
  public void markReconnectedByUser(User user) {
    Student student =
        studentRepository
            .findByUser(user)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STUDENT_NOT_FOUND));
    student.markReconnected();
    studentRepository.save(student);
  }

  /**
   * 전달된 데이터를 영속 저장소에 보관한다.
   *
   * @param student 학생 값
   */
  @Transactional
  public void save(Student student) {
    studentRepository.save(student);
  }

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param studentId 학생 식별자
   * @return 조회
   */
  public StudentDto.StudentProfileResponse getStudentProfile(UUID studentId) {
    Student student =
        studentRepository
            .findProfileByIdWithAssociations(studentId)
            .orElseThrow(() -> new CommonException(ErrorCode.STUDENT_NOT_FOUND));

    return buildStudentProfileResponse(student, student.getUser());
  }

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param userId 사용자 식별자
   * @return 조회
   */
  public StudentDto.StudentProfileResponse getStudentProfileByUserId(UUID userId) {
    User user =
        userRepository
            .findProfileByIdWithAssociations(userId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

    Student student = user.getStudent();
    if (student == null) {
      throw new CommonException(ErrorCode.USER_NOT_CONNECTED);
    }

    return buildStudentProfileResponse(student, user);
  }

  /**
   * 학생의 동기화 학사 데이터를 초기화한다.
   *
   * @param studentId 학생 식별자
   */
  @Transactional
  public void resetBy(UUID studentId) {
    studentCourseRepository.deleteByStudentId(studentId);
    semesterAcademicRecordRepository.deleteByStudentId(studentId);
    studentAcademicRecordRepository.deleteByStudentId(studentId);

    log.info("[BIZ] student.reset.done studentId={}", studentId);
  }

  /**
   * 전달된 값을 현재 객체에 설정한다.
   *
   * @param studentId 학생 식별자
   * @param targetGpa target gpa 값
   */
  @Transactional
  public void setStudentTargetGpa(UUID studentId, Double targetGpa) {
    studentRepository.updateTargetGpaByStudentId(studentId, targetGpa);
  }

  private static int getCurrentSemester(Integer gradeLevel, Integer completedSemesters) {

    int safeGradeLevel = (gradeLevel != null) ? gradeLevel : 0;
    int safeCompletedSemesters = (completedSemesters != null) ? completedSemesters : 0;

    int expectedCompleted = (safeGradeLevel - 1) * 2;
    int effectiveCompleted = Math.max(safeCompletedSemesters, expectedCompleted);
    int currentSemester = effectiveCompleted - expectedCompleted + 1;

    if (currentSemester < 1) {
      return 1;
    } else if (currentSemester > 2) {
      return 2;
    }
    return currentSemester;
  }

  private StudentDto.StudentProfileResponse buildStudentProfileResponse(
      Student student, User user) {
    StudentDto.StudentInfoDto studentInfo = StudentDto.StudentInfoDto.from(student);
    int currentSemester =
        getCurrentSemester(studentInfo.gradeLevel(), studentInfo.completedSemesters());
    String lastSyncedAt =
        user != null && user.getLastSyncedAt() != null ? user.getLastSyncedAt().toString() : "";

    return StudentDto.StudentProfileResponse.from(studentInfo, currentSemester, lastSyncedAt);
  }
}
