package com.Lamicons.CurriculumService.Controller;

import com.Lamicons.CurriculumService.Annotation.RequireRole;
import com.Lamicons.CurriculumService.DTO.Question.*;
import com.Lamicons.CurriculumService.DTO.University.ApiResponse;
import com.Lamicons.CurriculumService.Exception.UnauthorizedException;
import com.Lamicons.CurriculumService.Service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Question Management", description = "Unified Question API - CRUD operations, assessment linking, and bulk CSV upload")
public class QuestionController {
    
    private final QuestionService questionService;

    @GetMapping
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(
        summary = "Get all questions (Question Bank) [ADMIN]",
        description = "Admin endpoint. Lists all questions with optional filters. Restricted to prevent full question bank exposure."
    )
    public ResponseEntity<ApiResponse<List<QuestionSummaryDto>>> getAllQuestions(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Filter by question type (MCQ/CODING)") @RequestParam(required = false) QuestionType type,
            @Parameter(description = "Filter by topic") @RequestParam(required = false) String topic
    ) {
        log.info("GET /api/v1/questions by user {} - type: {}, topic: {}", userId, type, topic);
        List<QuestionSummaryDto> questions = questionService.getAllQuestions(type, topic);
        ApiResponse<List<QuestionSummaryDto>> response = ApiResponse.success("Questions retrieved successfully", questions);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(
        summary = "Get question by ID [ADMIN]",
        description = "Admin endpoint. Retrieves detailed information about a specific question."
    )
    public ResponseEntity<ApiResponse<QuestionSummaryDto>> getQuestionById(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Question ID", required = true) @PathVariable UUID id
    ) {
        log.info("GET /api/v1/questions/{} by user {}", id, userId);
        QuestionSummaryDto question = questionService.getQuestionById(id);
        ApiResponse<QuestionSummaryDto> response = ApiResponse.success("Question retrieved successfully", question);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(
        summary = "Create new question [ADMIN]",
        description = "Admin endpoint. Creates a standalone question in the question bank. Optionally link to an assessment."
    )
    public ResponseEntity<ApiResponse<QuestionSummaryDto>> createQuestion(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Valid @RequestBody QuestionCreateRequestDto request
    ) {
        log.info("POST /api/v1/questions - Creating question: {} by user {}", request.getTitle(), userId);
        QuestionSummaryDto question = questionService.createQuestion(request);
        ApiResponse<QuestionSummaryDto> response = ApiResponse.success("Question created successfully", question);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(
        summary = "Update existing question [ADMIN]",
        description = "Admin endpoint. Updates question details (title, description, score, etc.)"
    )
    public ResponseEntity<ApiResponse<QuestionSummaryDto>> updateQuestion(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Question ID", required = true) @PathVariable UUID id,
            @Valid @RequestBody QuestionCreateRequestDto request
    ) {
        log.info("PUT /api/v1/questions/{} - Updating question by user {}", id, userId);
        QuestionSummaryDto question = questionService.updateQuestion(id, request);
        ApiResponse<QuestionSummaryDto> response = ApiResponse.success("Question updated successfully", question);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(
        summary = "Delete question [ADMIN]",
        description = "Admin endpoint. Soft deletes a question and removes from all assessments"
    )
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Question ID", required = true) @PathVariable UUID id
    ) {
        log.info("DELETE /api/v1/questions/{} by user {}", id, userId);
        questionService.deleteQuestion(id);
        ApiResponse<Void> response = ApiResponse.success("Question deleted successfully", null);
        return ResponseEntity.ok(response);
    }
    
    // ==================== SEARCH & FILTER ====================
    
    @GetMapping("/search")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(
        summary = "Search questions [ADMIN]",
        description = "Admin endpoint. Searches questions by title, topic, or description."
    )
    public ResponseEntity<ApiResponse<List<QuestionSummaryDto>>> searchQuestions(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Search term", required = true) @RequestParam String searchTerm
    ) {
        log.info("GET /api/v1/questions/search - searchTerm: {} by user {}", searchTerm, userId);
        List<QuestionSummaryDto> questions = questionService.searchQuestions(searchTerm);
        ApiResponse<List<QuestionSummaryDto>> response = ApiResponse.success("Questions found successfully", questions);
        return ResponseEntity.ok(response);
    }
    
    // ==================== ASSESSMENT LINKING ====================
    
    @PostMapping("/assessments/{assessmentId}/link")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(
        summary = "Link questions to assessment [ADMIN]",
        description = "Admin endpoint. Attaches one or more existing questions from question bank to an assessment"
    )
    public ResponseEntity<ApiResponse<String>> linkQuestionsToAssessment(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Assessment ID", required = true) @PathVariable UUID assessmentId,
            @Valid @RequestBody QuestionAttachmentRequestDto request
    ) {
        log.info("POST /api/v1/questions/assessments/{}/link by user {}", assessmentId, userId);
        request.setAssessmentId(assessmentId);
        questionService.linkQuestionsToAssessment(request);
        ApiResponse<String> response = ApiResponse.success("Questions linked successfully to assessment", "Questions linked successfully to assessment");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/assessments/{assessmentId}/questions")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "STUDENT"})
    @Operation(
        summary = "Get questions by assessment",
        description = "Retrieves all questions linked to a specific assessment in order. Accessible by ADMIN, SUPER_ADMIN, and STUDENT."
    )
    public ResponseEntity<ApiResponse<List<QuestionSummaryDto>>> getQuestionsByAssessmentId(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Assessment ID", required = true) @PathVariable UUID assessmentId
    ) {
        log.info("GET /api/v1/questions/assessments/{}/questions by user {}", assessmentId, userId);
        List<QuestionSummaryDto> questions = questionService.getQuestionsByAssessmentId(assessmentId);
        ApiResponse<List<QuestionSummaryDto>> response = ApiResponse.success("Questions retrieved successfully", questions);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/assessments/{assessmentId}/questions/{questionId}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(
        summary = "Remove question from assessment [ADMIN]",
        description = "Admin endpoint. Unlinks a question from an assessment (does not delete the question)"
    )
    public ResponseEntity<ApiResponse<Void>> removeQuestionFromAssessment(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Assessment ID", required = true) @PathVariable UUID assessmentId,
            @Parameter(description = "Question ID", required = true) @PathVariable UUID questionId
    ) {
        log.info("DELETE /api/v1/questions/assessments/{}/questions/{} by user {}", assessmentId, questionId, userId);
        questionService.removeQuestionFromAssessment(assessmentId, questionId);
        ApiResponse<Void> response = ApiResponse.success("Question removed from assessment successfully", null);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/assessments/{assessmentId}/questions/reorder")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(
        summary = "Reorder questions in assessment [ADMIN]",
        description = "Admin endpoint. Changes the display order of questions within an assessment"
    )
    public ResponseEntity<ApiResponse<String>> reorderQuestions(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Assessment ID", required = true) @PathVariable UUID assessmentId,
            @RequestBody List<UUID> questionIdsInOrder
    ) {
        log.info("PUT /api/v1/questions/assessments/{}/questions/reorder by user {}", assessmentId, userId);
        questionService.reorderQuestions(assessmentId, questionIdsInOrder);
        ApiResponse<String> response = ApiResponse.success("Questions reordered successfully", "Questions reordered successfully");
        return ResponseEntity.ok(response);
    }
    
    // ==================== BULK CSV UPLOAD ====================
    
    @PostMapping(value = "/assessments/{assessmentId}/bulk-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(
        summary = "Bulk upload questions via CSV [ADMIN]",
        description = """
                Admin endpoint. Upload multiple questions to an assessment using CSV file.
                
                CSV Format (comma-separated):
                type,title,description,topic,score,negativeScore,optionA,optionB,optionC,optionD,correctOption,sampleInput,sampleOutput,constraints
                
                Example for MCQ:
                MCQ,"What is Java?","Select the correct answer","Programming",10,-2,"A language","A platform","An island","A coffee","A language","","",""
                
                Example for CODING:
                CODING,"Two Sum","Find two numbers that add up to target","Arrays",25,-5,"","","","","","[2,7,11,15], target=9","[0,1]","Array length < 10000"
                
                Notes:
                - Use quotes for fields containing commas
                - Leave MCQ-specific fields empty for CODING questions
                - Leave CODING-specific fields empty for MCQ questions
                - Maximum file size: 10MB
                """,
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Upload completed (check response for details)",
                        content = @Content(schema = @Schema(implementation = BulkUploadResultDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid CSV format"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Assessment not found")
        }
    )
    public ResponseEntity<ApiResponse<BulkUploadResultDto>> bulkUploadQuestions(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Assessment ID to link questions to", required = true) @PathVariable UUID assessmentId,
            @Parameter(description = "CSV file containing questions", required = true) @RequestParam("file") MultipartFile file
    ) {
        log.info("POST /api/v1/questions/assessments/{}/bulk-upload - file: {} by user {}", assessmentId, file.getOriginalFilename(), userId);
        BulkUploadResultDto result = questionService.bulkUploadQuestions(file, assessmentId);
        
        ApiResponse<BulkUploadResultDto> response = ApiResponse.success("Bulk upload completed", result);
        HttpStatus status = result.getFailedInserts() > 0 ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/csv-template")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(
        summary = "Download CSV template [ADMIN]",
        description = "Admin endpoint. Returns a dynamically generated CSV template based on Question entity fields"
    )
    public ResponseEntity<ApiResponse<String>> downloadCsvTemplate(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole
    ) {
        log.info("GET /api/v1/questions/csv-template by user {}", userId);
        
        // Define fields to exclude (internal metadata)
        Set<String> excludedFields = new HashSet<>(Arrays.asList(
            "id", "createdAt", "updatedAt", "codingQuestion", "testCases", "languageConfigs"
        ));

        // Get fields from base and subclasses using reflection
        List<Field> allFields = new ArrayList<>();
        allFields.addAll(Arrays.asList(FieldUtils.getAllFields(com.Lamicons.CurriculumService.Entity.Question.Question.class)));
        allFields.addAll(Arrays.asList(FieldUtils.getAllFields(com.Lamicons.CurriculumService.Entity.Question.McqQuestion.class)));
        allFields.addAll(Arrays.asList(FieldUtils.getAllFields(com.Lamicons.CurriculumService.Entity.Question.CodingQuestion.class)));

        String headers = allFields.stream()
                .map(Field::getName)
                .filter(name -> !excludedFields.contains(name))
                .distinct()
                .collect(Collectors.joining(","));

        // Append example rows matching the current bulkUpload format
        String template = headers + "\n" +
                "MCQ,\"What is the time complexity of binary search?\",\"Select the correct answer\",\"Algorithms\",10,-2,\"O(n)\",\"O(log n)\",\"O(n^2)\",\"O(1)\",\"O(log n)\",\"\",\"\",\"\"\n" +
                "CODING,\"Reverse a String\",\"Write a function to reverse a string\",\"Strings\",20,-5,\"\",\"\",\"\",\"\",\"\",\"hello\",\"olleh\",\"String length < 1000\"";
        
        ApiResponse<String> response = ApiResponse.success("CSV template generated and retrieved successfully", template);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=question_template.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(response);
    }
}
