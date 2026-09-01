package com.jobmatch.backend.service;

import com.jobmatch.backend.dto.GreenhouseJobsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class GreenhouseJobService {
    private final RestClient restClient = RestClient.create("https://boards-api.greenhouse.io");

    public GreenhouseJobsResponse findPublishedJobs(String boardToken) {
        if (!boardToken.matches("[a-zA-Z0-9_-]+")) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid Greenhouse board token");
        }

        try {
            GreenhouseJobsResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/boards/{boardToken}/jobs")
                            .queryParam("content", "true")
                            .build(boardToken))
                    .retrieve()
                    .body(GreenhouseJobsResponse.class);

            if (response == null || response.jobs() == null) {
                throw new ResponseStatusException(BAD_GATEWAY, "The careers source returned no job data");
            }

            return response;
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ResponseStatusException(BAD_GATEWAY, "Unable to check this Greenhouse careers board");
        }
    }
    public java.util.List<com.jobmatch.backend.dto.JobListing> findJobsForCompany(
            String companyName,
            String boardToken
    ) {
        return findPublishedJobs(boardToken)
                .jobs()
                .stream()
                .map(job -> new com.jobmatch.backend.dto.JobListing(
                        job.id(),
                        companyName,
                        job.title(),
                        job.location() == null ? "Location not listed" : job.location().name(),
                        job.absolute_url(),
                        job.content()
                ))
                .toList();
    }

}
