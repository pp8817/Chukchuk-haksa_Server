package com.chukchuk.haksa.global.security.filter;

import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.repository.UserRepository;
import com.chukchuk.haksa.global.security.service.CustomUserDetailsService;
import com.chukchuk.haksa.global.security.service.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    /**
     * Processes JWT authentication for an incoming HTTP request.
     *
     * <p>This method extracts the JWT from the "Authorization" header if it is present and formatted as a Bearer token.
     * It then uses the token to retrieve the user ID and email, ensuring the user exists in the repository by creating
     * a new user with a default nickname if necessary. After loading the user details, it establishes the user's authentication
     * in the security context. If any exception occurs during this process, a 401 Unauthorized response is sent.</p>
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response
     * @param filterChain the chain of filters to delegate further processing
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            String uid = jwtUtil.getUserIdFromToken(token);
            String email = jwtUtil.getEmailFromToken(token);

            // User 조회
            userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        // 사용자 정보가 없으면 새로 저장
                        User newUser = User.builder()
                            .id(UUID.fromString(uid))
                            .email(email)
                            .profileNickname("Unkonwn User")
                            .build();
                    return userRepository.save(newUser);
            });

            //UserDetails 생성 후 인증 객체 저장
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invaild Token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

//