package com.jobmatch.backend.dto;

public record JobListing(
        long id,
        String company,
        String title,
        String location,
        String applyUrl,
        String description
) {
}
