package com.jobmatch.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.RestClient;

import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final RestClient client = RestClient.create("https://api.resend.com");

    @PostMapping("/test-email")
    public Map<String, String> sendTestEmail(@Valid @RequestBody EmailRequest request) {
        String apiKey = System.getenv("RESEND_API_KEY");
        String from = System.getenv("JOBMATCH_FROM_EMAIL");
        if (apiKey == null || apiKey.isBlank() || from == null || from.isBlank()) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                    "Email is not configured yet. Set RESEND_API_KEY and JOBMATCH_FROM_EMAIL on the server.");
        }
        try {
            client.post().uri("/emails")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "from", from,
                            "to", new String[]{request.email()},
                            "subject", "JobMatch AI notifications are ready",
                            "html", "<h2>JobMatch AI is connected</h2><p>You will receive strong-match job alerts at this address.</p>"
                    ))
                    .retrieve().toBodilessEntity();
            return Map.of("message", "Test email sent");
        } catch (Exception exception) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_GATEWAY,
                    "Unable to send the email through Resend");
        }
    }

    public record EmailRequest(@Email @NotBlank String email) { }
}
