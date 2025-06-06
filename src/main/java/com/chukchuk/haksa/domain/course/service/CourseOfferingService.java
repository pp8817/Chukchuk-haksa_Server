package com.chukchuk.haksa.domain.course.service;

import com.chukchuk.haksa.domain.course.dto.CreateOfferingCommand;
import com.chukchuk.haksa.domain.course.model.Course;
import com.chukchuk.haksa.domain.course.model.CourseOffering;
import com.chukchuk.haksa.domain.course.model.EvaluationType;
import com.chukchuk.haksa.domain.course.model.FacultyDivision;
import com.chukchuk.haksa.domain.course.repository.CourseOfferingRepository;
import com.chukchuk.haksa.domain.course.repository.CourseRepository;
import com.chukchuk.haksa.domain.department.model.Department;
import com.chukchuk.haksa.domain.department.repository.DepartmentRepository;
import com.chukchuk.haksa.domain.professor.model.Professor;
import com.chukchuk.haksa.domain.professor.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseOfferingService {

    private final CourseOfferingRepository courseOfferingRepository;
    private final CourseRepository courseRepository;
    private final ProfessorRepository professorRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public CourseOffering getOrCreateOffering(CreateOfferingCommand cmd) {
        // 1. 존재하는지 확인
        Optional<CourseOffering> existing = courseOfferingRepository.findByCourseIdAndYearAndSemesterAndClassSectionAndProfessorId(
                cmd.courseId(), cmd.year(), cmd.semester(), cmd.classSection(), cmd.professorId()
        );

        if (existing.isPresent()) return existing.get();

        // 2. 없으면 새로 생성
        Course course = courseRepository.getReferenceById(cmd.courseId());
        Professor professor = professorRepository.getReferenceById(cmd.professorId());
        Department department = cmd.departmentId() != null
                ? departmentRepository.getReferenceById(cmd.departmentId())
                : null;

        CourseOffering newOffering = new CourseOffering(
                cmd.subjectEstablishmentSemester(),
                cmd.isVideoLecture(),
                cmd.year(),
                cmd.semester(),
                cmd.hostDepartment(),
                cmd.classSection(),
                cmd.scheduleSummary(),
                cmd.originalAreaCode(),
                cmd.areaCode(),
                cmd.points(),
                EvaluationType.valueOf(cmd.evaluationType()),
                FacultyDivision.valueOf(cmd.facultyDivisionName()),
                course,
                professor,
                department
        );

        return courseOfferingRepository.save(newOffering);
    }


    @Transactional(readOnly = true)
    public Map<Long, CourseOffering> getOfferingMapByIds(List<Long> offeringIds) {
        return courseOfferingRepository.findAllById(offeringIds).stream()
                .collect(Collectors.toMap(CourseOffering::getId, o -> o));
    }

}
