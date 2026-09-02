package com.jobmatch.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import jakarta.mail.internet.MimeMessage;

import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173", "https://jobmatch-ai-frontend-63y0wmy44-lovingwebsite1925.vercel.app", "https://jobmatch-ai-frontend-phi.vercel.app"})
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final JavaMailSender mailSender;

    public NotificationController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping("/test-email")
    public Map<String, String> sendTestEmail(@Valid @RequestBody EmailRequest request) {
        sendEmail(request.email(), "JobMatch AI notifications are ready",
                "<h2>JobMatch AI is connected</h2><p>You will receive strong-match job alerts at this address.</p>");
        return Map.of("message", "Test email sent");
    }

    @PostMapping("/job-alert")
    public Map<String, String> sendJobAlert(@Valid @RequestBody JobAlertRequest request) {
        if (request.matchScore() < 80) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Job alerts are only sent for 80%+ matches.");
        }
        String safeTitle = escapeHtml(request.title());
        String safeCompany = escapeHtml(request.company());
        String safeLocation = escapeHtml(request.location());
        String safeUrl = escapeHtml(request.applyUrl());
        sendEmail(request.email(), "New " + request.matchScore() + "% job match: " + request.title(),
                "<h2>Strong job match found</h2>"
                        + "<p><strong>" + safeTitle + "</strong> at " + safeCompany + "</p>"
                        + "<p>Location: " + safeLocation + " | Match score: " + request.matchScore() + "%</p>"
                        + "<p><a href=\"" + safeUrl + "\">Review application</a></p>");
        return Map.of("message", "Job alert sent");
    }

    private void sendEmail(String recipient, String subject, String html) {
        String from = System.getenv("MAIL_FROM");
        if (from == null || from.isBlank()) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                    "Email is not configured yet. Set MAIL_FROM and Gmail SMTP settings on the server.");
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception exception) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_GATEWAY,
                    "Unable to send the email through Gmail");
        }
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    public record EmailRequest(@Email @NotBlank String email) { }
    public record JobAlertRequest(@Email @NotBlank String email, @NotBlank String title, @NotBlank String company,
                                  @NotBlank String location, @NotBlank String applyUrl, int matchScore) { }
}
