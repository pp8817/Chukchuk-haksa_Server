package com.chukchuk.haksa.global.security.service;

import com.chukchuk.haksa.domain.user.repository.UserRepository;
import com.chukchuk.haksa.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads user details by email.
     *
     * <p>This method queries the user repository for a user associated with the provided email.
     * If a user is found, it is wrapped in a CustomUserDetails object and returned.
     * Otherwise, a UsernameNotFoundException is thrown with a message indicating that no user exists with the specified email.
     *
     * @param email the email address of the user to be loaded
     * @return the user details wrapped as a CustomUserDetails object
     * @throws UsernameNotFoundException if no user is found with the provided email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return userRepository.findByEmail(email)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
