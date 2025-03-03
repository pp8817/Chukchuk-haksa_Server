package com.chukchuk.haksa.global.security.service;

import com.chukchuk.haksa.global.security.CustomUserDetails;
import com.chukchuk.haksa.global.security.SupabaseUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SupabaseAuthService supabaseAuthService;

    @Override
    public UserDetails loadUserByUsername(String token) throws UsernameNotFoundException {
        SupabaseUser user = supabaseAuthService.verifyToken(token);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid token");
        }

        return new CustomUserDetails(user.getId(), user.getEmail(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
