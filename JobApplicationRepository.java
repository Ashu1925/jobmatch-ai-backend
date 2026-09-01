package com.jobmatch.backend.repository;

import com.jobmatch.backend.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> { }
