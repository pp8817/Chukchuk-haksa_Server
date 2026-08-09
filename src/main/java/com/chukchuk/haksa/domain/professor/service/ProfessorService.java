package com.chukchuk.haksa.domain.professor.service;

import com.chukchuk.haksa.domain.professor.model.Professor;
import com.chukchuk.haksa.domain.professor.repository.ProfessorRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 척척학사의 professor 비즈니스 흐름을 처리한다. */
@Service
@RequiredArgsConstructor
public class ProfessorService {
  private final ProfessorRepository professorRepository;

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param professorName professor 이름
   * @return 조회
   */
  @Transactional
  public Professor getOrCreate(String professorName) {
    return professorRepository
        .findByProfessorName(professorName)
        .orElseGet(
            () -> {
              Professor newProfessor = new Professor(professorName);
              return professorRepository.save(newProfessor);
            });
  }

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param professorNames professor names 값
   * @return 조회
   */
  @Transactional
  public Map<String, Professor> getOrCreateAll(Collection<String> professorNames) {
    if (professorNames == null || professorNames.isEmpty()) {
      return Collections.emptyMap();
    }

    Set<String> uniqueNames =
        professorNames.stream()
            .filter(name -> name != null && !name.isBlank())
            .collect(Collectors.toSet());
    if (uniqueNames.isEmpty()) {
      return Collections.emptyMap();
    }

    List<Professor> existing = professorRepository.findByProfessorNameIn(uniqueNames);
    Map<String, Professor> result = new HashMap<>();
    for (Professor professor : existing) {
      result.put(professor.getProfessorName(), professor);
    }

    List<Professor> toCreate =
        uniqueNames.stream().filter(name -> !result.containsKey(name)).map(Professor::new).toList();

    if (!toCreate.isEmpty()) {
      List<Professor> saved = professorRepository.saveAll(toCreate);
      for (Professor professor : saved) {
        result.put(professor.getProfessorName(), professor);
      }
    }

    return result;
  }
}
