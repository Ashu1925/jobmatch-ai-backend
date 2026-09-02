package com.jobmatch.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;


@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173", "https://jobmatch-ai-frontend-63y0wmy44-lovingwebsite1925.vercel.app", "https://jobmatch-ai-frontend-phi.vercel.app"})
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public String health() {
        return "JobMatch API is running";
    }
}
