package com.chukchuk.haksa.domain.professor.repository;

import com.chukchuk.haksa.domain.professor.model.Professor;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 구현체가 제공해야 할 professor repository 기능의 계약을 정의한다. */
@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param name 이름
   * @return 조회
   */
  Optional<Professor> findByProfessorName(String name);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param names names 값
   * @return 조회
   */
  List<Professor> findByProfessorNameIn(Collection<String> names);
}
