package com.chukchuk.haksa.infrastructure.portal.mapper;

import com.chukchuk.haksa.infrastructure.portal.dto.raw.*;
import com.chukchuk.haksa.infrastructure.portal.model.*;

import java.util.ArrayList;
import java.util.List;

public class PortalDataMapper {

    public static PortalData toPortalData(RawPortalData raw) {
        return new PortalData(
                toPortalStudentInfo(raw.student()),
                toPortalAcademicInfo(raw.semesters(), raw.academicRecords()),
                toPortalCurriculumInfo(raw.semesters())
        );
    }

    public static PortalStudentInfo toPortalStudentInfo(RawPortalStudentDTO s) {
        return new PortalStudentInfo(
                s.sno(),
                s.studNm(),
                new CodeName(s.univCd(), s.univNm()),
                new CodeName(s.dpmjCd(), s.dpmjNm()),
                new CodeName(s.mjorCd(), s.mjorNm()),
                s.the2MjorCd() != null ? new CodeName(s.the2MjorCd(), s.the2MjorNm()) : null,
                s.scrgStatNm(),
                new AdmissionInfo(
                        Integer.parseInt(s.enscYear()),
                        Integer.parseInt(s.enscSmrCd()),
                        s.enscDvcd()
                ),
                new PortalAcademicInfo( // ← 여기만 변경
                        s.studGrde(),
                        s.facSmrCnt(),
                        0,
                        0.0
                )
        );
    }

    public static PortalAcademicData toPortalAcademicInfo(List<RawPortalSemesterDTO> semesters, RawPortalGradeResponseDTO academicRecords) {
        List<SemesterCourseInfo> semesterCourses = new ArrayList<>();

        for (RawPortalSemesterDTO sem : semesters) {
            String[] parts = sem.semester().split("-");
            int year = Integer.parseInt(parts[0]);
            int semester = Integer.parseInt(parts[1]);

            List<CourseInfo> courses = new ArrayList<>();
            for (RawPortalCourseDTO c : sem.courses()) {
                courses.add(new CourseInfo(
                        c.subjtCd(),
                        c.subjtNm(),
                        c.ltrPrfsNm() != null ? c.ltrPrfsNm() : "미확인 교수",
                        c.estbDpmjNm(),
                        c.point(),
                        c.cretGrdCd(),
                        !"-".equals(c.refacYearSmr()),
                        c.timtSmryCn(),
                        c.facDvnm(),
                        parseIntSafe(c.cltTerrNm()),
                        parseIntSafe(c.cltTerrCd()),
                        parseIntSafe(c.subjtEstbSmrCd()),
                        parseDoubleSafe(c.gainPont())
                ));
            }

            semesterCourses.add(new SemesterCourseInfo(year, semester, courses));
        }

        List<SemesterGrade> grades = new ArrayList<>();
        for (RawPortalSemesterGradeDTO g : academicRecords.listSmrCretSumTabYearSmr()) {
            grades.add(new SemesterGrade(
                    Integer.parseInt(g.cretGainYear()),
                    Integer.parseInt(g.cretSmrCd()),
                    g.gainPoint(),
                    g.applPoint(),
                    g.gainAvmk(),
                    parseDoubleSafe(g.gainTavgPont()),
                    g.dpmjOrdp() != null ? parseRanking(g.dpmjOrdp()) : null
            ));
        }

        AcademicSummary summary = new AcademicSummary(
                Integer.parseInt(academicRecords.selectSmrCretSumTabSjTotal().applPoint()),
                Integer.parseInt(academicRecords.selectSmrCretSumTabSjTotal().gainPoint()),
                Double.parseDouble(academicRecords.selectSmrCretSumTabSjTotal().gainAvmk()),
                Double.parseDouble(academicRecords.selectSmrCretSumTabSjTotal().gainTavgPont())
        );

        return new PortalAcademicData(semesterCourses, new GradeSummary(grades, summary), summary);
    }

    public static PortalCurriculumData toPortalCurriculumInfo(List<RawPortalSemesterDTO> semesters) {
        List<CourseInfo> courses = new ArrayList<>();
        List<ProfessorInfo> professors = new ArrayList<>();
        List<OfferingInfo> offerings = new ArrayList<>();

        for (RawPortalSemesterDTO sem : semesters) {
            int year = Integer.parseInt(sem.semester().split("-")[0]);
            int semester = Integer.parseInt(sem.semester().split("-")[1]);

            for (RawPortalCourseDTO c : sem.courses()) {
                courses.add(new CourseInfo(
                        c.subjtCd(),                                     // code
                        c.subjtNm(),                                     // name
                        c.ltrPrfsNm() != null ? c.ltrPrfsNm() : "미확인 교수", // professor
                        c.estbDpmjNm(),                                  // department
                        c.point(),                                       // credits
                        c.cretGrdCd(),                                   // grade
                        !"-".equals(c.refacYearSmr()),                   // isRetake
                        c.timtSmryCn(),                                  // schedule
                        c.facDvnm(),                                     // areaType
                        parseIntSafe(c.cltTerrNm()),                     // areaCode
                        parseIntSafe(c.cltTerrCd()),                     // originalAreaCode
                        parseIntSafe(c.subjtEstbSmrCd()),                // establishmentSemester
                        parseDoubleSafe(c.gainPont())                    // originalScore
                ));
                professors.add(new ProfessorInfo(c.ltrPrfsNm() != null ? c.ltrPrfsNm() : "미확인 교수"));

                offerings.add(new OfferingInfo(
                        c.subjtCd(),
                        year,
                        parseIntSafe(c.subjtEstbSmrCd()),
                        c.diclNo(),
                        c.ltrPrfsNm(),
                        c.timtSmryCn(),
                        c.point(),
                        c.estbDpmjNm(),
                        c.facDvnm(),
                        parseIntSafe(c.subjtEstbSmrCd()),
                        parseIntSafe(c.cltTerrNm()),
                        parseIntSafe(c.cltTerrCd())
                ));
            }
        }

        return new PortalCurriculumData(courses, professors, offerings);
    }

    private static Integer parseIntSafe(String str) {
        try {
            return str != null ? Integer.parseInt(str) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Double parseDoubleSafe(String str) {
        try {
            return str != null ? Double.parseDouble(str) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Ranking parseRanking(String ordp) {
        String[] split = ordp.split("/");
        return new Ranking(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }
}