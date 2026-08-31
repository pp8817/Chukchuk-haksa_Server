// 지정과목 스냅샷 버전 비교를 실제 JPA 트랜잭션에서 검증한다.

package com.chukchuk.haksa.application.portal;

import static org.assertj.core.api.Assertions.assertThat;

import com.chukchuk.haksa.domain.department.model.Department;
import com.chukchuk.haksa.domain.department.repository.DepartmentRepository;
import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.model.StudentDesignatedCourse;
import com.chukchuk.haksa.domain.student.repository.StudentDesignatedCourseRepository;
import com.chukchuk.haksa.domain.student.repository.StudentRepository;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.repository.UserRepository;
import com.chukchuk.haksa.infrastructure.portal.model.DesignatedCourseData;
import com.chukchuk.haksa.infrastructure.portal.model.DesignatedCourseSnapshot;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SyncDesignatedCourseServiceIntegrationTests {

  @Autowired private SyncDesignatedCourseService service;

  @Autowired private StudentRepository studentRepository;

  @Autowired private StudentDesignatedCourseRepository designatedCourseRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private DepartmentRepository departmentRepository;

  @PersistenceContext private EntityManager entityManager;

  @Test
  @DisplayName("이미 저장된 최신 지정과목 스냅샷은 늦게 도착한 결과로 덮어쓰지 않는다")
  void ignoresOutOfOrderSnapshot() {
    Student student = saveStudent();
    Instant latest = Instant.parse("2026-08-30T02:00:00Z");
    Instant stale = Instant.parse("2026-08-30T01:00:00Z");

    service.sync(
        student.getUser().getId(),
        DesignatedCourseSnapshot.received(List.of(course("LATEST", 0))),
        latest);
    service.sync(
        student.getUser().getId(),
        DesignatedCourseSnapshot.received(List.of(course("STALE", 0))),
        stale);
    entityManager.flush();
    entityManager.clear();

    Student storedStudent = studentRepository.findById(student.getId()).orElseThrow();
    List<StudentDesignatedCourse> storedCourses =
        designatedCourseRepository.findAllByStudentIdOrderBySourceOrder(student.getId());

    assertThat(storedStudent.getDesignatedCoursesSnapshotVersion()).isEqualTo(latest);
    assertThat(storedCourses)
        .extracting(StudentDesignatedCourse::getSubjtCd)
        .containsExactly("LATEST");
  }

  private Student saveStudent() {
    User user =
        userRepository.save(
            User.builder()
                .email(UUID.randomUUID() + "@example.com")
                .profileNickname("tester")
                .build());
    Department department =
        departmentRepository.save(
            new Department("D-" + UUID.randomUUID(), "테스트학과-" + UUID.randomUUID()));
    return studentRepository.save(
        Student.builder()
            .studentCode("S-" + UUID.randomUUID())
            .name("홍길동")
            .department(department)
            .admissionYear(2024)
            .isTransferStudent(false)
            .user(user)
            .build());
  }

  private static DesignatedCourseData course(String code, int sourceOrder) {
    return new DesignatedCourseData(
        "01", code, "지정과목", 3, "TRANSFER", 2024, "1학기", "", sourceOrder);
  }
}
