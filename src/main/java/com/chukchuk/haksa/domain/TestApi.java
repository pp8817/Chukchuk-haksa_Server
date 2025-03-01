package com.chukchuk.haksa.domain;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestApi {
    @GetMapping("/test")
    public String test() {
        Log logger_info = LogFactory.getLog("INFO_LOG");
        logger_info.debug("log~~");
        return "test";
    }
}
