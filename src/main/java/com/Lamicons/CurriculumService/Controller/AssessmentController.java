package com.Lamicons.CurriculumService.Controller;

import com.Lamicons.CurriculumService.Annotation.RequireRole;
import com.Lamicons.CurriculumService.DTO.Assessment.*;
import com.Lamicons.CurriculumService.DTO.University.ApiResponse;
import com.Lamicons.CurriculumService.Exception.UnauthorizedException;
import com.Lamicons.CurriculumService.Service.AssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assessments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Assessment Management", description = "Unified Assessment API - CRUD operations and module linking")
public class AssessmentController {
    
    private final AssessmentService assessmentService;

    @GetMapping("/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "STUDENT"})
    @Operation(
        summary = "Get assessment by ID",
        description = "Retrieves assessment details with all linked questions. Restricted to ADMIN, SUPER_ADMIN, or STUDENT."
    )
    public ResponseEntity<ApiResponse<AssessmentResponseDto>> getAssessmentById(
            @Parameter(description = "Assessment ID", required = true) @PathVariable UUID id,
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole
    ) {
        log.info("GET /api/v1/assessments/{} by user {} with role {}", id, userId, userRole);
        AssessmentResponseDto assessment = assessmentService.getAssessmentById(id, userId, userRole);
        ApiResponse<AssessmentResponseDto> response = ApiResponse.success("Assessment retrieved successfully", assessment);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(
        summary = "Create new assessment [ADMIN]",
        description = "Admin endpoint. Creates a standalone assessment (can be reusable or module-specific)"
    )
    public ResponseEntity<ApiResponse<AssessmentResponseDto>> createAssessment(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Valid @RequestBody AssessmentAttachmentRequestDto.NewAssessmentDto request
    ) {
        log.info("POST /api/v1/assessments - Creating assessment: {} by user {}", request.getTitle(), userId);
        AssessmentResponseDto assessment = assessmentService.createStandaloneAssessment(request);
        ApiResponse<AssessmentResponseDto> response = ApiResponse.success("Assessment created successfully", assessment);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(
        summary = "Update assessment [ADMIN]",
        description = "Admin endpoint. Updates assessment details"
    )
    public ResponseEntity<ApiResponse<AssessmentResponseDto>> updateAssessment(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Assessment ID", required = true) @PathVariable UUID id,
            @Valid @RequestBody AssessmentAttachmentRequestDto.NewAssessmentDto request
    ) {
        log.info("PUT /api/v1/assessments/{} - Updating assessment by user {}", id, userId);
        AssessmentResponseDto assessment = assessmentService.updateAssessment(id, request);
        ApiResponse<AssessmentResponseDto> response = ApiResponse.success("Assessment updated successfully", assessment);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(
        summary = "Delete assessment [ADMIN]",
        description = "Admin endpoint. Soft deletes an assessment (only if not used in any modules)"
    )
    public ResponseEntity<ApiResponse<Void>> deleteAssessment(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Assessment ID", required = true) @PathVariable UUID id
    ) {
        log.info("DELETE /api/v1/assessments/{} by user {}", id, userId);
        assessmentService.deleteAssessment(id);
        ApiResponse<Void> response = ApiResponse.success("Assessment deleted successfully", null);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/attach-to-module")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(
        summary = "Attach assessments to module [ADMIN]",
        description = "Admin endpoint. Attaches existing or new assessments to a module. " +
                      "Supports both linking existing reusable assessments and creating new assessments inline."
    )
    public ResponseEntity<ApiResponse<Void>> attachAssessmentsToModule(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Valid @RequestBody AssessmentAttachmentRequestDto request
    ) {
        log.info("POST /api/v1/assessments/attach-to-module - moduleId: {} by user {}", request.getModuleId(), userId);
        assessmentService.attachAssessmentsToModule(request);
        ApiResponse<Void> response = ApiResponse.success("Assessments attached to module successfully", null);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/by-module/{moduleId}")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "STUDENT"})
    @Operation(
        summary = "Get assessments by module",
        description = "Retrieves all assessments attached to a specific module. Restricted to ADMIN, SUPER_ADMIN, or STUDENT."
    )
    public ResponseEntity<ApiResponse<List<AssessmentResponseDto>>> getAssessmentsByModule(
            @Parameter(description = "Module ID", required = true) @PathVariable UUID moduleId,
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole
    ) {
        log.info("GET /api/v1/assessments/by-module/{} by user {} with role {}", moduleId, userId, userRole);
        List<AssessmentResponseDto> assessments = assessmentService.getAssessmentsByModuleId(moduleId, userId, userRole);
        ApiResponse<List<AssessmentResponseDto>> response = ApiResponse.success("Assessments retrieved successfully", assessments);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/modules/{moduleId}/assessments/{assessmentId}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(
        summary = "Remove assessment from module [ADMIN]",
        description = "Admin endpoint. Removes assessment from module (soft delete from junction table)"
    )
    public ResponseEntity<ApiResponse<Void>> removeAssessmentFromModule(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Module ID", required = true) @PathVariable UUID moduleId,
            @Parameter(description = "Assessment ID", required = true) @PathVariable UUID assessmentId
    ) {
        log.info("DELETE /api/v1/assessments/modules/{}/assessments/{} by user {}", moduleId, assessmentId, userId);
        assessmentService.removeAssessmentFromModule(moduleId, assessmentId);
        ApiResponse<Void> response = ApiResponse.success("Assessment removed from module successfully", null);
        return ResponseEntity.ok(response);
    }


//    Future enhancement: List all assessments with pagination

//    @GetMapping
//    @Operation(
//        summary = "Get all assessments",
//        description = "Public endpoint. Lists all assessments"
//    )
//    public ResponseEntity<List<AssessmentSummaryDto>> getAllAssessments() {
//        log.info("GET /api/assessments - Retrieving all assessments");
//        // Note: Consider adding pagination in production
//        List<AssessmentSummaryDto> assessments = assessmentService.getAllReusableAssessments();
//        return ResponseEntity.ok(assessments);
//    }
//
//    @PutMapping("/modules/{moduleId}/reorder")
//    @Operation(
//        summary = "Reorder assessments in module [ADMIN]",
//        description = "Admin endpoint. Updates the display order of assessments within a module"
//    )
//
//    public ResponseEntity<Void> reorderAssessments(
//            @Parameter(description = "Module ID", required = true) @PathVariable UUID moduleId,
//            @RequestBody List<UUID> assessmentIdsInOrder
//    ) {
//        log.info("PUT /api/assessments/modules/{}/reorder", moduleId);
//        assessmentService.reorderAssessments(moduleId, assessmentIdsInOrder);
//        return ResponseEntity.ok().build();
//    }
}
