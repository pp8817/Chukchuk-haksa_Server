package com.chukchuk.haksa.infrastructure.auth;

import com.chukchuk.haksa.domain.auth.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtAuthService implements AuthService {

    @Override
    public String getAuthenticatedUserId() {
        // 현재 SecurityContext에서 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나 인증되지 않았다면 예외를 발생시킴
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        // 인증된 사용자 정보에서 UserDetails를 가져옴
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            throw new IllegalStateException("Invalid user principal");
        }

        // 사용자 ID는 username에 저장되어 있다고 가정 (UUID or email)
        return ((UserDetails) principal).getUsername();  // username = userId
    }
}
