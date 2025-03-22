package com.chukchuk.haksa.domain.user.controller;

import com.chukchuk.haksa.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @DeleteMapping("/users/delete")
    public ResponseEntity<?> deleteUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        userService.deleteUserByEmail(email);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
