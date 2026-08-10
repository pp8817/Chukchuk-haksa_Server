package com.chukchuk.haksa.domain.professor.repository;

import com.chukchuk.haksa.domain.professor.model.Professor;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 교수 이름 하나 또는 이름 집합으로 교수를 조회하는 저장소다. */
@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
  /**
   * 교수를 메서드에 지정된 식별 조건과 정렬 기준으로 조회한다.
   *
   * @param name 이름
   * @return 조건에 일치하는 교수가 있으면 포함한 선택값
   */
  Optional<Professor> findByProfessorName(String name);

  /**
   * 교수를 메서드에 지정된 식별 조건과 정렬 기준으로 조회한다.
   *
   * @param names names
   * @return 조건에 일치하는 교수 목록
   */
  List<Professor> findByProfessorNameIn(Collection<String> names);
}
