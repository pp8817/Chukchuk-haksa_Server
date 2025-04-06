package com.chukchuk.haksa.domain.course.model;

import com.chukchuk.haksa.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "courses")
public class Course extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_code", nullable = false)
    private String courseCode;

    @Column(name = "course_name")
    private String courseName;

    /**
      * 소프트 삭제를 위한 타임스탬프 필드
      * null이면 활성 상태, 값이 있으면 해당 시점에 삭제된 것으로 간주
     */
    @Column(name = "delete_at")
    private Instant deleteAt;

    public Course(String courseCode, String courseName) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        // deleteAt은 초기에는 null로 설정 (soft delete 사용 시 삭제 시점에 설정)
        this.deleteAt = null;
    }
}
