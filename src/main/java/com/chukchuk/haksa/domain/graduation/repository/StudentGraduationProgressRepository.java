// 학생별 졸업 진행 상태를 조회하고 저장하는 JPA Repository

package com.chukchuk.haksa.domain.graduation.repository;

import com.chukchuk.haksa.domain.graduation.model.StudentGraduationProgress;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** 학생별 졸업 부가요건 진행 상태를 조회하고 삭제하는 저장소다. */
public interface StudentGraduationProgressRepository
    extends JpaRepository<StudentGraduationProgress, UUID> {
  /**
   * 졸업 요건를 메서드에 지정된 식별 조건과 정렬 기준으로 조회한다.
   *
   * @param studentId 학생 식별자
   * @return 조건에 일치하는 졸업 요건가 있으면 포함한 선택값
   */
  Optional<StudentGraduationProgress> findByStudentId(UUID studentId);

  /**
   * 지정된 조건에 해당하는 졸업 요건를 삭제한다.
   *
   * @param studentId 학생 식별자
   */
  void deleteByStudentId(UUID studentId);
}
