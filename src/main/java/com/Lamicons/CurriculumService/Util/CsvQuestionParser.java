package com.Lamicons.CurriculumService.Util;

import com.Lamicons.CurriculumService.DTO.Question.QuestionBulkUploadDto;
import com.Lamicons.CurriculumService.DTO.Question.QuestionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Utility class for parsing question CSV files
 * Supports bulk upload of questions with error handling
 */
@Slf4j
public class CsvQuestionParser {
    
    private static final int MIN_COLUMNS = 7; // Minimum required columns
    
    /**
     * Parse CSV file and convert to QuestionBulkUploadDto list
     * 
     * CSV Format:
     * type,title,description,topic,score,negativeScore,optionA,optionB,optionC,optionD,correctOption,sampleInput,sampleOutput,constraints
     * 
     * @param file CSV file uploaded by admin
     * @param assessmentId Assessment to link questions to
     * @return List of parsed question DTOs
     * @throws Exception if file is invalid or parsing fails
     */
    public static List<QuestionBulkUploadDto> parseQuestionsCsv(MultipartFile file, UUID assessmentId) throws Exception {
        List<QuestionBulkUploadDto> questions = new ArrayList<>();
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("CSV file is empty");
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("CSV file has no header");
            }
            
            log.info("CSV Header: {}", headerLine);
            
            String line;
            int rowNumber = 1; // Start from 1 (0 is header)
            
            while ((line = reader.readLine()) != null) {
                rowNumber++;
                
                if (line.trim().isEmpty()) {
                    log.warn("Skipping empty line at row {}", rowNumber);
                    continue;
                }
                
                try {
                    QuestionBulkUploadDto question = parseLine(line, assessmentId, rowNumber);
                    questions.add(question);
                } catch (Exception e) {
                    log.error("Error parsing row {}: {}", rowNumber, e.getMessage());
                    throw new Exception("Error at row " + rowNumber + ": " + e.getMessage());
                }
            }
            
            log.info("Successfully parsed {} questions from CSV", questions.size());
            return questions;
            
        } catch (Exception e) {
            log.error("Failed to parse CSV file: {}", e.getMessage());
            throw new Exception("CSV parsing failed: " + e.getMessage());
        }
    }
    
    /**
     * Parse a single CSV line into QuestionBulkUploadDto
     */
    private static QuestionBulkUploadDto parseLine(String line, UUID assessmentId, int rowNumber) {
        String[] columns = parseCsvLine(line);
        
        if (columns.length < MIN_COLUMNS) {
            throw new IllegalArgumentException("Row has insufficient columns. Expected at least " + 
                    MIN_COLUMNS + ", got " + columns.length);
        }
        
        try {
            QuestionType type = QuestionType.valueOf(columns[0].trim().toUpperCase());
            String title = columns[1].trim();
            String description = columns.length > 2 ? columns[2].trim() : "";
            String topic = columns.length > 3 ? columns[3].trim() : "";
            
            Integer score = null;
            if (columns.length > 4 && !columns[4].trim().isEmpty()) {
                score = Integer.parseInt(columns[4].trim());
            }
            
            Integer negativeScore = null;
            if (columns.length > 5 && !columns[5].trim().isEmpty()) {
                negativeScore = Integer.parseInt(columns[5].trim());
            }
            
            QuestionBulkUploadDto.QuestionBulkUploadDtoBuilder builder = QuestionBulkUploadDto.builder()
                    .assessmentId(assessmentId)
                    .type(type)
                    .title(title)
                    .description(description)
                    .topic(topic)
                    .score(score)
                    .negativeScore(negativeScore)
                    .orderNumber(rowNumber - 1); // 0-based ordering
            
            // MCQ specific fields
            if (type == QuestionType.MCQ) {
                if (columns.length > 6) builder.optionA(columns[6].trim());
                if (columns.length > 7) builder.optionB(columns[7].trim());
                if (columns.length > 8) builder.optionC(columns[8].trim());
                if (columns.length > 9) builder.optionD(columns[9].trim());
                if (columns.length > 10) builder.correctOption(columns[10].trim().toUpperCase());
            }
            
            // CODING specific fields
            if (type == QuestionType.CODING) {
                if (columns.length > 11 && !columns[11].trim().isEmpty()) {
                    try { builder.timeLimit(Integer.parseInt(columns[11].trim())); } catch (NumberFormatException ignored) {}
                }
                if (columns.length > 12 && !columns[12].trim().isEmpty()) {
                    try { builder.memoryLimit(Integer.parseInt(columns[12].trim())); } catch (NumberFormatException ignored) {}
                }
            }
            
            return builder.build();
            
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid data format: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parsing line: " + e.getMessage());
        }
    }
    
    /**
     * Parse CSV line handling quoted fields with commas
     * Supports: "Title with, comma",Description,etc
     */
    private static String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        result.add(currentField.toString());
        
        return result.toArray(new String[0]);
    }
    
    /**
     * Validate CSV structure before processing
     */
    public static void validateCsvStructure(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("File must be a CSV file");
        }
        
        // Check file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }
    }
    
    /**
     * Generate CSV template for download
     */
    public static String generateCsvTemplate() {
        return "type,title,description,topic,score,negativeScore,optionA,optionB,optionC,optionD,correctOption,sampleInput,sampleOutput,constraints\n" +
               "MCQ,\"What is Java?\",\"Basic Java question\",Java,5,0,\"Programming Language\",\"Framework\",\"IDE\",\"OS\",A,,,\n" +
               "MCQ,\"OOP Concept?\",\"OOP fundamentals\",OOP,10,-2,\"Paradigm\",\"Tool\",\"Language\",\"None\",A,,,\n" +
               "CODING,\"Reverse String\",\"Write a function to reverse\",Strings,20,0,,,,,,\"hello\",\"olleh\",\"1 <= length <= 1000\"\n";
    }
}
