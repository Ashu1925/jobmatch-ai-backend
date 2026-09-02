package com.jobmatch.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173", "https://jobmatch-ai-frontend-63y0wmy44-lovingwebsite1925.vercel.app", "https://jobmatch-ai-frontend-phi.vercel.app"})
@RestController
@RequestMapping("/api/matches")
public class MatchController {
    private static final List<String> SKILLS = List.of(
            "java", "spring", "spring boot", "react", "javascript", "typescript", "python", "sql",
            "mysql", "postgresql", "mongodb", "aws", "azure", "docker", "kubernetes", "git", "github",
            "html", "css", "tailwind", "node.js", "node", "express", "rest api", "microservices",
            "kotlin", "android", "figma", "excel", "power bi", "machine learning", "data analysis"
    );

    @PostMapping
    public MatchResult score(@Valid @RequestBody MatchRequest request) {
        String resume = normalise(request.resumeText());
        String description = normalise(request.jobDescription());
        List<String> requiredSkills = SKILLS.stream().filter(description::contains).toList();
        List<String> matchedSkills = requiredSkills.stream().filter(resume::contains).toList();
        Set<String> resumeWords = words(resume);
        Set<String> jobWords = words(description);
        jobWords.removeAll(Set.of("the", "and", "with", "for", "you", "will", "this", "that", "from", "our", "your", "are", "have", "role", "team", "job", "work"));
        long matchingWords = jobWords.stream().filter(resumeWords::contains).count();
        int keywordScore = jobWords.isEmpty() ? 0 : (int) Math.round((matchingWords * 100.0) / jobWords.size());
        int skillScore = requiredSkills.isEmpty() ? keywordScore : (int) Math.round((matchedSkills.size() * 100.0) / requiredSkills.size());
        int score = requiredSkills.isEmpty() ? keywordScore : (int) Math.round((skillScore * 0.7) + (keywordScore * 0.3));

        return new MatchResult(Math.min(score, 100), matchedSkills, requiredSkills.stream().filter(skill -> !matchedSkills.contains(skill)).toList());
    }

    private String normalise(String text) { return text.replaceAll("<[^>]*>", " ").toLowerCase(Locale.ROOT); }
    private Set<String> words(String text) { return new LinkedHashSet<>(Arrays.stream(text.replaceAll("[^a-z0-9+#.]", " ").split("\\s+")).filter(word -> word.length() >= 3).toList()); }

    public record MatchRequest(@NotBlank String resumeText, @NotBlank String jobDescription) { }
    public record MatchResult(int score, List<String> matchedSkills, List<String> missingSkills) { }
}
