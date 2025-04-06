package com.chukchuk.haksa.domain.user.model.init;

import com.chukchuk.haksa.domain.user.service.TestUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestUserInit implements ApplicationRunner {

    private final TestUserService testUserService;

    @Override
    public void run(ApplicationArguments args) {
        testUserService.createTestUser();
    }
}
