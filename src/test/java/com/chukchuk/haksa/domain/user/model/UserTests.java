// 사용자 병합 시 탈퇴 상태 전파 방지를 검증하는 도메인 테스트
package com.chukchuk.haksa.domain.user.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class UserTests {

    @Test
    void 탈퇴한_원본을_병합해도_대상_사용자는_활성_상태를_유지한다() {
        User target = User.builder()
                .email("target@example.com")
                .profileNickname("target")
                .build();
        User withdrawnOrigin = User.builder()
                .email("origin@example.com")
                .profileNickname("origin")
                .build();
        withdrawnOrigin.withdraw(Instant.now());

        target.absorbFrom(withdrawnOrigin);

        assertThat(target.getIsDeleted()).isFalse();
        assertThat(target.getDeletedAt()).isNull();
    }
}
