package com.chukchuk.haksa.domain.professor.service;

import com.chukchuk.haksa.domain.professor.model.Professor;
import com.chukchuk.haksa.domain.professor.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfessorService {
    private final ProfessorRepository professorRepository;

    @Transactional
    public Professor getOrCreate(String professorName) {
        return professorRepository.findByProfessorName(professorName)
                .orElseGet(() -> {
                    Professor newProfessor = new Professor(professorName);
                    return professorRepository.save(newProfessor);
                });
    }
}
