package com.chukchuk.haksa.domain.requirement.model;

import com.chukchuk.haksa.domain.BaseEntity;
import com.chukchuk.haksa.domain.department.model.Department;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "dual_major_requirements")
public class DualMajorRequirement extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "required_credits", nullable = false)
    private Integer requiredCredits;

    @Enumerated(EnumType.STRING)
    @Column(name = "majorRole", nullable = false)
    private MajorRole majorRole;

    @Column(name = "area_type", nullable = false)
    private String areaType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}
