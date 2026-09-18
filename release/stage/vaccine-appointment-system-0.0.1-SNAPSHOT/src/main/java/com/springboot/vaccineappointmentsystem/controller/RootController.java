package com.springboot.vaccineappointmentsystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public String root() {
        return "Backend API is running. Please visit frontend: http://localhost:5173";
    }
}
