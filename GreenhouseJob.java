package com.jobmatch.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GreenhouseJob(
        Long id,
        String title,
        String updated_at,
        String absolute_url,
        String content,
        Location location
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Location(String name) {
    }
}
