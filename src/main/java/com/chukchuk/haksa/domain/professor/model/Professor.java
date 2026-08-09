package com.chukchuk.haksa.domain.professor.model;

import com.chukchuk.haksa.domain.department.model.Department;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "professor")
public class Professor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "professor_code")
  private String professorCode;

  @Column(name = "professor_name", nullable = false, unique = true)
  private String professorName;

  @CreatedDate
  @Column(name = "created_at")
  private Instant createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id")
  private Department department;

  public Professor(String professorName) {
    this.professorName = professorName;
  }
}
