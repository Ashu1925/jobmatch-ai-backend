package com.jobmatch.backend.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "job_applications")
public class JobApplication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String title;
    private String company;
    private String location;
    private String applyUrl;
    private Integer matchScore;
    private String status;
    private Instant createdAt;

    protected JobApplication() { }
    public JobApplication(String title, String company, String location, String applyUrl, Integer matchScore) {
        this.title = title; this.company = company; this.location = location; this.applyUrl = applyUrl;
        this.matchScore = matchScore; this.status = "Saved"; this.createdAt = Instant.now();
    }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public String getApplyUrl() { return applyUrl; }
    public Integer getMatchScore() { return matchScore; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setStatus(String status) { this.status = status; }
}
