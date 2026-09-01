// 실제 저장된 학사 데이터로 편입생 졸업진단의 학점과 지정과목 현황을 검증한다.

package com.chukchuk.haksa.domain.graduation.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.chukchuk.haksa.domain.academic.record.model.StudentAcademicRecord;
import com.chukchuk.haksa.domain.academic.record.model.StudentCourse;
import com.chukchuk.haksa.domain.academic.record.repository.StudentAcademicRecordRepository;
import com.chukchuk.haksa.domain.academic.record.repository.StudentCourseRepository;
import com.chukchuk.haksa.domain.course.model.Course;
import com.chukchuk.haksa.domain.course.model.CourseOffering;
import com.chukchuk.haksa.domain.course.model.EvaluationType;
import com.chukchuk.haksa.domain.course.model.FacultyDivision;
import com.chukchuk.haksa.domain.course.repository.CourseOfferingRepository;
import com.chukchuk.haksa.domain.course.repository.CourseRepository;
import com.chukchuk.haksa.domain.department.model.Department;
import com.chukchuk.haksa.domain.department.repository.DepartmentRepository;
import com.chukchuk.haksa.domain.graduation.dto.DesignatedCourseCompletionStatus;
import com.chukchuk.haksa.domain.graduation.dto.DesignatedCourseProgressDto;
import com.chukchuk.haksa.domain.graduation.dto.GraduationProgressResponse;
import com.chukchuk.haksa.domain.graduation.dto.TransferGraduationProgressDto;
import com.chukchuk.haksa.domain.graduation.model.StudentGraduationProgress;
import com.chukchuk.haksa.domain.graduation.repository.StudentGraduationProgressRepository;
import com.chukchuk.haksa.domain.student.model.Grade;
import com.chukchuk.haksa.domain.student.model.GradeType;
import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.model.StudentDesignatedCourse;
import com.chukchuk.haksa.domain.student.model.StudentStatus;
import com.chukchuk.haksa.domain.student.repository.StudentDesignatedCourseRepository;
import com.chukchuk.haksa.domain.student.repository.StudentRepository;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.repository.UserRepository;
import com.chukchuk.haksa.infrastructure.portal.mapper.PortalDataMapper;
import com.chukchuk.haksa.infrastructure.portal.model.CourseInfo;
import com.chukchuk.haksa.infrastructure.portal.model.DesignatedCourseData;
import com.chukchuk.haksa.infrastructure.portal.model.PortalData;
import com.chukchuk.haksa.infrastructure.portal.model.SemesterCourseInfo;
import com.chukchuk.haksa.infrastructure.portal.dto.raw.RawPortalData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
    properties = {
      "scraping.scheduler.enabled=false",
      "scraping.publisher.enabled=false",
      "scraping.stale.enabled=false"
    })
@ActiveProfiles("test")
@Transactional
class TransferGraduationAnalysisIntegrationTest {

  @Autowired private TransferGraduationAnalysisService analysisService;

  @Autowired private StudentRepository studentRepository;

  @Autowired private StudentAcademicRecordRepository academicRecordRepository;

  @Autowired private StudentCourseRepository studentCourseRepository;

  @Autowired private StudentDesignatedCourseRepository designatedCourseRepository;

  @Autowired private StudentGraduationProgressRepository graduationProgressRepository;

  @Autowired private CourseRepository courseRepository;

  @Autowired private CourseOfferingRepository courseOfferingRepository;

  @Autowired private DepartmentRepository departmentRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private ObjectMapper objectMapper;

  @PersistenceContext private EntityManager entityManager;

  @Test
  @DisplayName("포털 fixture의 편입 인정학점과 지정과목 이수 현황을 저장 데이터에서 계산한다")
  void analyzesTransferStudentFromStoredPortalData() throws Exception {
    RawPortalData raw = objectMapper.readValue(fixture(), RawPortalData.class);
    PortalData portalData = PortalDataMapper.toPortalData(raw);
    Student student = saveStudent(portalData);

    academicRecordRepository.save(
        new StudentAcademicRecord(
            student,
            portalData.academic().summary().appliedCredits(),
            portalData.academic().summary().totalCredits(),
            BigDecimal.valueOf(portalData.academic().summary().gpa()),
            BigDecimal.valueOf(portalData.academic().summary().score())));
    graduationProgressRepository.save(StudentGraduationProgress.createForLanguageCert(student, true));
    saveDesignatedCourses(student, portalData);
    saveStudentCourses(student, portalData);
    entityManager.flush();
    entityManager.clear();

    Student storedStudent = studentRepository.findById(student.getId()).orElseThrow();
    GraduationProgressResponse response = analysisService.analyze(storedStudent);
    TransferGraduationProgressDto progress = response.getTransferProgress();

    assertThat(storedStudent.isTransferStudent()).isTrue();
    assertThat(response.getLanguageCertFulfilled()).isTrue();
    assertThat(progress.totalEarnedCredits()).isEqualTo(112);
    assertThat(progress.recognizedTransferCredits()).isEqualTo(65);
    assertThat(progress.totalEarnedCredits()).isNotEqualTo(177);
    assertThat(progress.designatedCourses())
        .extracting(DesignatedCourseProgressDto::status)
        .containsExactly(
            DesignatedCourseCompletionStatus.COMPLETED,
            DesignatedCourseCompletionStatus.NOT_COMPLETED);
  }

  private Student saveStudent(PortalData portalData) {
    Department department =
        departmentRepository.save(
            new Department(
                portalData.student().department().code(), portalData.student().department().name()));
    User user =
        userRepository.save(
            User.builder()
                .email(UUID.randomUUID() + "@example.com")
                .profileNickname("transfer-test")
                .build());
    return studentRepository.save(
        Student.builder()
            .studentCode(portalData.student().studentCode())
            .name(portalData.student().name())
            .department(department)
            .admissionYear(portalData.student().admission().year())
            .semesterEnrolled(portalData.student().admission().semester())
            .isTransferStudent(true)
            .isGraduated(false)
            .status(StudentStatus.재학)
            .gradeLevel(portalData.student().academic().gradeLevel())
            .completedSemesters(portalData.student().academic().completedSemesters())
            .admissionType(portalData.student().admission().type())
            .user(user)
            .build());
  }

  private void saveDesignatedCourses(Student student, PortalData portalData) {
    List<StudentDesignatedCourse> courses =
        portalData.designatedCourses().courses().stream()
            .map(course -> new StudentDesignatedCourse(student, course))
            .toList();
    designatedCourseRepository.saveAll(courses);
  }

  private void saveStudentCourses(Student student, PortalData portalData) {
    List<StudentCourse> studentCourses = new ArrayList<>();
    for (SemesterCourseInfo semester : portalData.academic().semesters()) {
      for (CourseInfo courseInfo : semester.courses()) {
        Course course = courseRepository.save(new Course(courseInfo.code(), courseInfo.name()));
        CourseOffering offering =
            courseOfferingRepository.save(
                new CourseOffering(
                    courseInfo.establishmentSemester(),
                    false,
                    semester.year(),
                    semester.semester(),
                    courseInfo.department(),
                    "A",
                    courseInfo.schedule(),
                    courseInfo.originalAreaCode(),
                    courseInfo.credits(),
                    EvaluationType.UNKNOWN,
                    parseFacultyDivision(courseInfo.areaType()),
                    course,
                    null,
                    null,
                    null));
        studentCourses.add(
            new StudentCourse(
                student,
                offering,
                new Grade(GradeType.from(courseInfo.grade())),
                courseInfo.credits(),
                courseInfo.isRetake(),
                courseInfo.originalScore() == null ? null : courseInfo.originalScore().intValue(),
                courseInfo.isRetakeDeleted()));
      }
    }
    studentCourseRepository.saveAll(studentCourses);
  }

  private FacultyDivision parseFacultyDivision(String areaType) {
    return FacultyDivision.valueOf(areaType);
  }

  private static String fixture() throws Exception {
    try (InputStream input =
        TransferGraduationAnalysisIntegrationTest.class.getResourceAsStream(
            "/fixtures/portal/transfer-graduation.json")) {
      return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
}
