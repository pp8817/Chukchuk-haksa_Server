package com.chukchuk.haksa.domain;

import com.chukchuk.haksa.global.security.CustomUserDetails;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestApi {
    @GetMapping("/test")
    public String test() {
        Log logger_info = LogFactory.getLog("INFO_LOG");
        logger_info.debug("log~~");
        return "test";
    }

    @GetMapping("/public")
    public String publicTest() {
        return "public";
    }

    @GetMapping("/private")
    public String privateTest(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return "private, Current User: " + userDetails.getEmail();
    }
}
