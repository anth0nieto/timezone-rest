package com.tutenlabs.timezonerest.controller;

import static com.tutenlabs.timezonerest.constants.Constants.APP_VERSION_URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Value("${build.version}")
    private String buildVersion;

    @GetMapping("/")
    public String index() {
        logger.info("index api");
        return "index";
    }

    @GetMapping(APP_VERSION_URL)
    @ResponseBody
    public ResponseEntity<String> version() {
        logger.info("version api");
        String response = "version - " + buildVersion;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
