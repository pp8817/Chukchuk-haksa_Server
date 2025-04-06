package com.chukchuk.haksa.domain.professor.repository;

import com.chukchuk.haksa.domain.professor.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByProfessorName(String name);
}
