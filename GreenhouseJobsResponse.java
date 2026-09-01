package com.jobmatch.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GreenhouseJobsResponse(List<GreenhouseJob> jobs) {
}