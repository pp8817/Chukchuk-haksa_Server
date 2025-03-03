package com.chukchuk.haksa.global.security;

import com.chukchuk.haksa.global.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Configures and returns the security filter chain for HTTP requests.
     *
     * <p>This method disables CSRF protection, applies default CORS settings, and sets the session
     * management policy to stateless for JWT-based authentication. It establishes authorization rules
     * that permit unrestricted access to endpoints under "/api/auth/**" and "/api/public", while requiring
     * authentication for "/api/private" and all other routes. Additionally, it integrates the JWT
     * authentication filter into the chain before the standard username-password authentication filter.
     *
     * @param http the HttpSecurity instance used to build the security configuration
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during the configuration of the security filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT 기반 인증
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // 인증이 필요 없는 경로
                        .requestMatchers("/api/public").permitAll()
                        .requestMatchers("/api/private").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Creates an AuthenticationManager bean using a DAO-based authentication provider.
     *
     * <p>This method configures a DaoAuthenticationProvider with the injected UserDetailsService to
     * perform user authentication and wraps it in a ProviderManager. The resulting AuthenticationManager
     * is integrated into the security filter chain for stateless authentication.</p>
     *
     * @return the configured AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager(authProvider);
    }
}
