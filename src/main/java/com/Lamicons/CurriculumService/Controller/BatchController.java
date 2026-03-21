package com.Lamicons.CurriculumService.Controller;

import com.Lamicons.CurriculumService.DTO.Batch.BatchRequestDto;
import com.Lamicons.CurriculumService.DTO.Batch.BatchResponseDto;
import com.Lamicons.CurriculumService.DTO.University.ApiResponse;
import com.Lamicons.CurriculumService.Exception.UnauthorizedException;
import com.Lamicons.CurriculumService.Service.BatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Slf4j
@RequestMapping("/api/v1/batches")
@Tag(name = "Batch Management", description = "APIs for managing batches - Create/Update/Delete requires Admin role")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    private void validateAdminRole(String userRole) {
        if (userRole == null || 
            !(userRole.equalsIgnoreCase("ADMIN") || 
              userRole.equalsIgnoreCase("ROLE_ADMIN") || 
              userRole.equalsIgnoreCase("SUPER_ADMIN") || 
              userRole.equalsIgnoreCase("ROLE_SUPER_ADMIN"))) {
            log.warn("BatchController : Unauthorized access attempt with role: {}", userRole);
            throw new UnauthorizedException("Access denied. Admin role required.");
        }
    }

    @Operation(summary = "Create a new batch", description = "Creates a new batch (Admin only)")
    @PostMapping
    public ResponseEntity<ApiResponse<BatchResponseDto>> createBatch(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Valid @RequestBody BatchRequestDto requestDto) {
        
        log.info("BatchController : createBatch : Request received from user: {}", userId);
        validateAdminRole(userRole);
        
        BatchResponseDto responseDto = batchService.createBatch(requestDto, UUID.fromString(userId));
        ApiResponse<BatchResponseDto> response = ApiResponse.success(
            "Batch created successfully", 
            responseDto
        );
        
        log.info("BatchController : createBatch : Batch created with ID: {}", responseDto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get batch by ID", description = "Retrieves a batch by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BatchResponseDto>> getBatchById(
            @Parameter(description = "ID of the batch to retrieve", required = true)
            @PathVariable UUID id) {
        
        log.info("BatchController : getBatchById : Request received for ID: {}", id);
        
        BatchResponseDto responseDto = batchService.getBatchById(id);
        ApiResponse<BatchResponseDto> response = ApiResponse.success(
            "Batch retrieved successfully", 
            responseDto
        );
        
        log.info("BatchController : getBatchById : Batch retrieved: {}", responseDto.getBatchName());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all batches", description = "Retrieves all batches")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BatchResponseDto>>> getAllBatches() {
        
        log.info("BatchController : getAllBatches : Request received");
        
        List<BatchResponseDto> batches = batchService.getAllBatches();
        ApiResponse<List<BatchResponseDto>> response = ApiResponse.success(
            "Batches retrieved successfully", 
            batches
        );
        
        log.info("BatchController : getAllBatches : Retrieved {} batches", batches.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get batches by course ID", description = "Retrieves all batches for a specific course")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<BatchResponseDto>>> getBatchesByCourseId(
            @Parameter(description = "ID of the course", required = true)
            @PathVariable UUID courseId) {
        
        log.info("BatchController : getBatchesByCourseId : Request received for course ID: {}", courseId);
        
        List<BatchResponseDto> batches = batchService.getBatchesByCourseId(courseId);
        ApiResponse<List<BatchResponseDto>> response = ApiResponse.success(
            "Batches retrieved successfully", 
            batches
        );
        
        log.info("BatchController : getBatchesByCourseId : Retrieved {} batches", batches.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get batches by university ID", description = "Retrieves all batches for a specific university")
    @GetMapping("/university/{universityId}")
    public ResponseEntity<ApiResponse<List<BatchResponseDto>>> getBatchesByUniversityId(
            @Parameter(description = "ID of the university", required = true)
            @PathVariable UUID universityId) {
        
        log.info("BatchController : getBatchesByUniversityId : Request received for university ID: {}", universityId);
        
        List<BatchResponseDto> batches = batchService.getBatchesByUniversityId(universityId);
        ApiResponse<List<BatchResponseDto>> response = ApiResponse.success(
            "Batches retrieved successfully", 
            batches
        );
        
        log.info("BatchController : getBatchesByUniversityId : Retrieved {} batches", batches.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update a batch", description = "Updates an existing batch (Admin only)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BatchResponseDto>> updateBatch(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "ID of the batch to update", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody BatchRequestDto requestDto) {
        
        log.info("BatchController : updateBatch : Request received for ID: {} by user: {}", id, userId);
        validateAdminRole(userRole);
        
        BatchResponseDto responseDto = batchService.updateBatch(id, requestDto, UUID.fromString(userId));
        ApiResponse<BatchResponseDto> response = ApiResponse.success(
            "Batch updated successfully", 
            responseDto
        );
        
        log.info("BatchController : updateBatch : Batch updated: {}", responseDto.getBatchName());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a batch", description = "Deletes a batch by its ID (Admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBatch(
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "ID of the batch to delete", required = true)
            @PathVariable UUID id) {
        
        log.info("BatchController : deleteBatch : Request received for ID: {}", id);
        validateAdminRole(userRole);
        
        batchService.deleteBatch(id);
        ApiResponse<Void> response = ApiResponse.success(
            "Batch deleted successfully", 
            null
        );
        
        log.info("BatchController : deleteBatch : Batch deleted with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Check instructor assignment", description = "Verifies if an instructor is assigned to a specific batch")
    @GetMapping("/{batchId}/instructors/{userId}/assigned")
    public ResponseEntity<ApiResponse<Boolean>> isInstructorAssignedToBatch(
            @Parameter(description = "ID of the batch", required = true)
            @PathVariable UUID batchId,
            @Parameter(description = "User ID of the instructor", required = true)
            @PathVariable UUID userId) {
        
        log.info("BatchController : isInstructorAssignedToBatch : Checking assignment for batch: {} and userId: {}", batchId, userId);
        
        boolean isAssigned = batchService.isInstructorAssignedToBatch(batchId, userId);
        ApiResponse<Boolean> response = ApiResponse.success(
            "Instructor assignment checked successfully", 
            isAssigned
        );
        
        return ResponseEntity.ok(response);
    }
}
