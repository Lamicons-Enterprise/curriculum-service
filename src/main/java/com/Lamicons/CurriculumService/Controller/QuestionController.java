package com.Lamicons.CurriculumService.Controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Question Management", description = "Unified Question API - CRUD operations, assessment linking, and bulk CSV upload")
public class QuestionController {
    
    private final QuestionService questionService;

    private void validateAdminRole(String userRole) {
        if (userRole == null || !userRole.equalsIgnoreCase("ADMIN")) {
            log.warn("QuestionController: Unauthorized access attempt with role: {}", userRole);
            throw new UnauthorizedException("Access denied. Admin role required.");
        }
    }

    
    @GetMapping
    @Operation(
        summary = "Get all questions (Question Bank)",
        description = "Public endpoint. Lists all questions with optional filters"
    )
    public ResponseEntity<ApiResponse<List<QuestionSummaryDto>>> getAllQuestions(
            @Parameter(description = "Filter by question type (MCQ/CODING)") @RequestParam(required = false) QuestionType type,
            @Parameter(description = "Filter by topic") @RequestParam(required = false) String topic
    ) {
        log.info("GET /api/questions - type: {}, topic: {}", type, topic);
        List<QuestionSummaryDto> questions = questionService.getAllQuestions(type, topic);
        ApiResponse<List<QuestionSummaryDto>> response = ApiResponse.success("Questions retrieved successfully", questions);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Get question by ID",
        description = "Public endpoint. Retrieves detailed information about a specific question"
    )
    public ResponseEntity<ApiResponse<QuestionSummaryDto>> getQuestionById(
            @Parameter(description = "Question ID", required = true) @PathVariable UUID id
    ) {
        log.info("GET /api/questions/{}", id);
        QuestionSummaryDto question = questionService.getQuestionById(id);
        ApiResponse<QuestionSummaryDto> response = ApiResponse.success("Question retrieved successfully", question);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
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
        log.info("POST /api/questions - Creating question: {}", request.getTitle());
        validateAdminRole(userRole);
        QuestionSummaryDto question = questionService.createQuestion(request);
        ApiResponse<QuestionSummaryDto> response = ApiResponse.success("Question created successfully", question);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
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
        log.info("PUT /api/questions/{} - Updating question", id);
        validateAdminRole(userRole);
        QuestionSummaryDto question = questionService.updateQuestion(id, request);
        ApiResponse<QuestionSummaryDto> response = ApiResponse.success("Question updated successfully", question);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
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
        log.info("DELETE /api/questions/{}", id);
        validateAdminRole(userRole);
        questionService.deleteQuestion(id);
        ApiResponse<Void> response = ApiResponse.success("Question deleted successfully", null);
        return ResponseEntity.ok(response);
    }
    
    // ==================== SEARCH & FILTER ====================
    
    @GetMapping("/search")
    @Operation(
        summary = "Search questions",
        description = "Public endpoint. Searches questions by title, topic, or description"
    )
    public ResponseEntity<ApiResponse<List<QuestionSummaryDto>>> searchQuestions(
            @Parameter(description = "Search term", required = true) @RequestParam String searchTerm
    ) {
        log.info("GET /api/questions/search - searchTerm: {}", searchTerm);
        List<QuestionSummaryDto> questions = questionService.searchQuestions(searchTerm);
        ApiResponse<List<QuestionSummaryDto>> response = ApiResponse.success("Questions found successfully", questions);
        return ResponseEntity.ok(response);
    }
    
    // ==================== ASSESSMENT LINKING ====================
    
    @PostMapping("/assessments/{assessmentId}/link")
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
        log.info("POST /api/questions/assessments/{}/link", assessmentId);
        validateAdminRole(userRole);
        request.setAssessmentId(assessmentId);
        questionService.linkQuestionsToAssessment(request);
        ApiResponse<String> response = ApiResponse.success("Questions linked successfully to assessment", "Questions linked successfully to assessment");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/assessments/{assessmentId}/questions")
    @Operation(
        summary = "Get questions by assessment",
        description = "Public endpoint. Lists all questions linked to a specific assessment in order"
    )
    public ResponseEntity<ApiResponse<List<QuestionSummaryDto>>> getQuestionsByAssessmentId(
            @Parameter(description = "Assessment ID", required = true) @PathVariable UUID assessmentId
    ) {
        log.info("GET /api/questions/assessments/{}/questions", assessmentId);
        List<QuestionSummaryDto> questions = questionService.getQuestionsByAssessmentId(assessmentId);
        ApiResponse<List<QuestionSummaryDto>> response = ApiResponse.success("Questions retrieved successfully", questions);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/assessments/{assessmentId}/questions/{questionId}")
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
        log.info("DELETE /api/questions/assessments/{}/questions/{}", assessmentId, questionId);
        validateAdminRole(userRole);
        questionService.removeQuestionFromAssessment(assessmentId, questionId);
        ApiResponse<Void> response = ApiResponse.success("Question removed from assessment successfully", null);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/assessments/{assessmentId}/questions/reorder")
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
        log.info("PUT /api/questions/assessments/{}/questions/reorder", assessmentId);
        validateAdminRole(userRole);
        questionService.reorderQuestions(assessmentId, questionIdsInOrder);
        ApiResponse<String> response = ApiResponse.success("Questions reordered successfully", "Questions reordered successfully");
        return ResponseEntity.ok(response);
    }
    
    // ==================== BULK CSV UPLOAD ====================
    
    @PostMapping(value = "/assessments/{assessmentId}/bulk-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
        log.info("POST /api/questions/assessments/{}/bulk-upload - file: {}", assessmentId, file.getOriginalFilename());
        validateAdminRole(userRole);
        BulkUploadResultDto result = questionService.bulkUploadQuestions(file, assessmentId);
        
        ApiResponse<BulkUploadResultDto> response = ApiResponse.success("Bulk upload completed", result);
        HttpStatus status = result.getFailedInserts() > 0 ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }
    
    @GetMapping("/csv-template")
    @Operation(
        summary = "Download CSV template",
        description = "Public endpoint. Returns a sample CSV template with correct format and example rows"
    )
    public ResponseEntity<ApiResponse<String>> downloadCsvTemplate() {
        log.info("GET /api/questions/csv-template");
        String template = """
                type,title,description,topic,score,negativeScore,optionA,optionB,optionC,optionD,correctOption,sampleInput,sampleOutput,constraints
                MCQ,"What is the time complexity of binary search?","Select the correct answer","Algorithms",10,-2,"O(n)","O(log n)","O(n^2)","O(1)","O(log n)","","",""
                CODING,"Reverse a String","Write a function to reverse a string","Strings",20,-5,"","","","","","hello","olleh","String length < 1000"
                """;
        
        ApiResponse<String> response = ApiResponse.success("CSV template retrieved successfully", template);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=question_template.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(response);
    }
}
