package com.jobmatch.backend.controller;

import com.jobmatch.backend.dto.GreenhouseJob;
import com.jobmatch.backend.dto.JobListing;
import com.jobmatch.backend.service.GreenhouseJobService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173", "https://jobmatch-ai-frontend-63y0wmy44-lovingwebsite1925.vercel.app"})
@RestController
@RequestMapping("/api/jobs")
public class GreenhouseJobsController {
    private final GreenhouseJobService greenhouseJobService;

    public GreenhouseJobsController(GreenhouseJobService greenhouseJobService) {
        this.greenhouseJobService = greenhouseJobService;
    }

    @GetMapping("/greenhouse/{boardToken}")
    public List<GreenhouseJob> getGreenhouseJobs(@PathVariable String boardToken) {
        return greenhouseJobService.findPublishedJobs(boardToken).jobs();
  
    }
    @GetMapping("/greenhouse/listing/{companyName}/{boardToken}")
    public List<JobListing> getCompanyJobListings(
            @PathVariable String companyName,
            @PathVariable String boardToken
    ) {
        return greenhouseJobService.findJobsForCompany(companyName, boardToken);
    }


}
