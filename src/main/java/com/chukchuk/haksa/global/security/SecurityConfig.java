package com.chukchuk.haksa.global.security;

import com.chukchuk.haksa.global.security.filter.JwtAuthenticationFilter;
import com.chukchuk.haksa.global.security.handler.CustomAccessDeniedHandler;
import com.chukchuk.haksa.global.security.handler.CustomAuthenticationEntryPoint;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/** 척척학사의 보안 애플리케이션 설정을 제공한다. */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final UserDetailsService userDetailsService;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;

  /**
   * 인증과 인가 정책이 적용된 보안 필터 체인을 구성한다.
   *
   * @param http http 값
   * @param corsConfigurationSource cors configuration source 값
   * @return 보안 filter chain 결과
   * @throws Exception exception이 발생하는 경우
   */
  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
    return http.cors(cors -> cors.configurationSource(corsConfigurationSource))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers(PUBLIC_ENDPOINTS)
                    .permitAll()
                    .requestMatchers(SWAGGER_ENDPOINTS)
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            exception ->
                exception
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
                    .accessDeniedHandler(customAccessDeniedHandler))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  private static final String[] PUBLIC_ENDPOINTS = {
    "/",
    "/health",
    "/error",
    "/auth/kakao",
    "/sentry-test",
    "/api/users/signin",
    "/api/users/signin/**",
    "/api/auth/refresh",
    "/api/admin/test-users",
    "/api/admin/test-options",
    "/api/admin/departments",
    "/api/admin/course-offerings",
    "/api/admin/test-lecture-evaluations/**",
    "/internal/scrape-results",
    "/actuator/prometheus",
    "/actuator/health",
    "/actuator/info"
  };

  private static final String[] SWAGGER_ENDPOINTS = {
    "/v3/api-docs/**",
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/swagger-resources/**",
    "/swagger-config/**",
    "/webjars/**"
  };

  // ---- CORS (dev) ----
  /**
   * 개발 환경의 CORS 정책을 반환한다.
   *
   * @return cors configuration source 결과
   */
  @Bean("corsConfigurationSource")
  @Profile("dev")
  public CorsConfigurationSource devCors() {
    CorsConfiguration c = new CorsConfiguration();
    c.setAllowedOrigins(
        List.of(
            "http://localhost:3000",
            "https://dv.cchaksa.com",
            "https://*.cchaksa.com",
            "https://dev.api.cchaksa.com"));
    c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    c.setAllowedHeaders(List.of("*"));
    c.setAllowCredentials(true);
    c.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
    s.registerCorsConfiguration("/**", c);
    return s;
  }

  // ---- CORS (prod) ----
  /**
   * 운영 환경의 CORS 정책을 반환한다.
   *
   * @return cors configuration source 결과
   */
  @Bean("corsConfigurationSource")
  @Profile("prod")
  public CorsConfigurationSource prodCors() {
    CorsConfiguration c = new CorsConfiguration();
    c.setAllowedOrigins(
        List.of(
            "https://www.cchaksa.com",
            "https://cchaksa.com",
            "https://dv.cchaksa.com",
            "https://*.cchaksa.com",
            "https://api.cchaksa.com",
            "http://localhost:3000"));
    c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    c.setAllowedHeaders(List.of("*"));
    c.setAllowCredentials(true); // 쿠키 미사용이면 false 권장
    c.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
    s.registerCorsConfiguration("/**", c);
    return s;
  }

  /**
   * 기본 CORS 정책을 반환한다.
   *
   * @return cors configuration source 결과
   */
  @Bean("corsConfigurationSource")
  @Profile("local")
  public CorsConfigurationSource defaultCors() {
    CorsConfiguration c = new CorsConfiguration();
    c.setAllowedOrigins(List.of("*"));
    c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    c.setAllowedHeaders(List.of("*"));
    UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
    s.registerCorsConfiguration("/**", c);
    return s;
  }

  /**
   * Spring Security 인증 관리자를 반환한다.
   *
   * @return authentication manager 결과
   */
  @Bean
  public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    return new ProviderManager(authProvider);
  }
}
