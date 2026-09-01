package com.jobmatch.backend.controller;

import com.jobmatch.backend.model.JobApplication;
import com.jobmatch.backend.repository.JobApplicationRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Comparator;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {
    private final JobApplicationRepository repository;
    public JobApplicationController(JobApplicationRepository repository) { this.repository = repository; }

    @GetMapping
    public List<JobApplication> list() { return repository.findAll().stream().sorted(Comparator.comparing(JobApplication::getCreatedAt).reversed()).toList(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobApplication create(@Valid @RequestBody CreateRequest request) {
        return repository.save(new JobApplication(request.title(), request.company(), request.location(), request.applyUrl(), request.matchScore()));
    }

    @PostMapping("/{id}/status")
    @Transactional
    public JobApplication updateStatus(@PathVariable("id") Long id, @Valid @RequestBody StatusRequest request) {
        JobApplication job = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tracked job not found"));
        job.setStatus(request.status());
        return job;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable("id") Long id) {
        if (!repository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tracked job not found");
        repository.deleteById(id);
    }

    public record CreateRequest(@NotBlank String title, @NotBlank String company, @NotBlank String location, @NotBlank String applyUrl, Integer matchScore) { }
    public record StatusRequest(@NotBlank String status) { }
}
