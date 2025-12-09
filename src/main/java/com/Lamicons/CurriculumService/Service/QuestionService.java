package com.Lamicons.CurriculumService.Service;

import com.Lamicons.CurriculumService.DTO.Question.*;
import com.Lamicons.CurriculumService.Entity.Question.Question;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Question Admin Service for production-grade course builder workflow
 * Supports: Single question creation, bulk CSV upload, question bank, assignment linking
 */
public interface QuestionService {
    
    /**
     * Create a single question and optionally link to assessment
     * Question is stored in question bank for reusability
     */
    QuestionSummaryDto createQuestion(QuestionCreateRequestDto request);
    
    /**
     * Update an existing question
     */
    QuestionSummaryDto updateQuestion(UUID questionId, QuestionCreateRequestDto request);
    
    /**
     * Get question by ID
     */
    QuestionSummaryDto getQuestionById(UUID questionId);
    
    /**
     * List all questions in question bank
     * For admin dropdown selection
     */
    List<QuestionSummaryDto> getAllQuestions(QuestionType type, String topic);
    
    /**
     * Search questions by title or topic
     */
    List<QuestionSummaryDto> searchQuestions(String searchTerm);
    
    /**
     * Link existing questions to an assessment
     * Questions already exist in question bank
     */
    void linkQuestionsToAssessment(QuestionAttachmentRequestDto request);
    
    /**
     * Remove a question from an assessment (only removes link, keeps question in bank)
     */
    void removeQuestionFromAssessment(UUID assessmentId, UUID questionId);
    
    /**
     * Get all questions for a specific assessment
     */
    List<QuestionSummaryDto> getQuestionsByAssessmentId(UUID assessmentId);
    
    /**
     * Reorder questions within an assessment
     */
    void reorderQuestions(UUID assessmentId, List<UUID> questionIdsInOrder);
    
    /**
     * BULK CSV UPLOAD - Create multiple questions and link to assessment
     * CSV format: type, title, description, topic, score, negativeScore, options, answer
     * Each row creates a new question in question bank
     * Automatically creates assessment-question mapping
     * Returns summary: total processed, successful, failed
     */
    BulkUploadResultDto bulkUploadQuestions(MultipartFile csvFile, UUID assessmentId);
    
    /**
     * Delete question from question bank (soft delete)
     * Checks if question is used in any assessments
     */
    void deleteQuestion(UUID questionId);
}
