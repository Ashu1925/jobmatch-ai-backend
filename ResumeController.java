package com.jobmatch.backend.controller;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
@RestController
@RequestMapping("/api/resume")
public class ResumeController {
    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeExtraction> extractText(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String text = extractResumeText(file).trim();

        if (text.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok(new ResumeExtraction(file.getOriginalFilename(), text.length(), text));
    }

    private String extractResumeText(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();

        if (fileName.endsWith(".pdf")) {
            try (PDDocument document = Loader.loadPDF(file.getBytes())) {
                return new PDFTextStripper().getText(document);
            }
        }

        if (fileName.endsWith(".docx")) {
            try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
                return document.getParagraphs().stream()
                        .map(XWPFParagraph::getText)
                        .collect(Collectors.joining("\n"));
            }
        }

        if (fileName.endsWith(".txt")) {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        }

        throw new IllegalArgumentException("Only PDF, DOCX, and TXT resumes are supported");
    }

    public record ResumeExtraction(String fileName, int characterCount, String text) {
    }
}
