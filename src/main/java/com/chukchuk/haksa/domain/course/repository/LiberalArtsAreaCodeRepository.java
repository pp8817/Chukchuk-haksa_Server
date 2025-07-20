package com.chukchuk.haksa.domain.course.repository;

import com.chukchuk.haksa.domain.course.model.LiberalArtsAreaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LiberalArtsAreaCodeRepository extends JpaRepository<LiberalArtsAreaCode, Integer> {
}
