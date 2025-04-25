package com.chukchuk.haksa.domain.student.wrapper;


import com.chukchuk.haksa.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import static com.chukchuk.haksa.domain.student.dto.StudentDto.Profile;

@Schema(name = "StudentProfileApiResponse", description = "학생 프로필 응답")
public class StudentProfileApiResponse extends ApiResponse<Profile> {
    public StudentProfileApiResponse() {
        super(true,
                new Profile(
                        "홍길동",                 // name
                        "20231234",             // studentCode
                        "컴퓨터공학과",         // departmentName
                        "컴퓨터SW학과",     // majorName
                        3,                      // gradeLevel
                        6,                      // currentSemester
                        "ENROLLED",            // status
                        "2024-04-25T10:00:00Z", // lastUpdatedAt
                        "2024-04-25T08:00:00Z"  // lastSyncedAt
                ),
                null,
                null
        );
    }
}