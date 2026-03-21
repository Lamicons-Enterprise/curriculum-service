package com.Lamicons.CurriculumService.Controller;

import com.Lamicons.CurriculumService.Annotation.RequireRole;
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
@Tag(name = "Batch Management", description = "APIs for managing batches")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @Operation(summary = "Create a new batch", description = "Creates a new batch (Admin only)")
    @PostMapping
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<BatchResponseDto>> createBatch(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Valid @RequestBody BatchRequestDto requestDto) {
        
        log.info("BatchController : createBatch : Request received from user: {}", userId);
        
        BatchResponseDto responseDto = batchService.createBatch(requestDto, UUID.fromString(userId));
        ApiResponse<BatchResponseDto> response = ApiResponse.success(
            "Batch created successfully", 
            responseDto
        );
        
        log.info("BatchController : createBatch : Batch created with ID: {}", responseDto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get batch by ID", description = "Retrieves a batch by its ID. Restricted to ADMIN, SUPER_ADMIN, or STUDENT.")
    @GetMapping("/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "STUDENT"})
    public ResponseEntity<ApiResponse<BatchResponseDto>> getBatchById(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "ID of the batch to retrieve", required = true)
            @PathVariable UUID id) {
        
        log.info("BatchController : getBatchById : Request received for ID: {} by user: {}", id, userId);
        
        BatchResponseDto responseDto = batchService.getBatchById(id);
        ApiResponse<BatchResponseDto> response = ApiResponse.success(
            "Batch retrieved successfully", 
            responseDto
        );
        
        log.info("BatchController : getBatchById : Batch retrieved: {} by user {}", responseDto.getBatchName(), userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all batches", description = "Retrieves all batches (Admin only)")
    @GetMapping
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<List<BatchResponseDto>>> getAllBatches(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole) {
        
        log.info("BatchController : getAllBatches : Request received from user: {}", userId);
        
        List<BatchResponseDto> batches = batchService.getAllBatches();
        ApiResponse<List<BatchResponseDto>> response = ApiResponse.success(
            "Batches retrieved successfully", 
            batches
        );
        
        log.info("BatchController : getAllBatches : Retrieved {} batches for user {}", batches.size(), userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get batches by course ID", description = "Retrieves all batches for a specific course (Admin only)")
    @GetMapping("/course/{courseId}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<List<BatchResponseDto>>> getBatchesByCourseId(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "ID of the course", required = true)
            @PathVariable UUID courseId) {
        
        log.info("BatchController : getBatchesByCourseId : Request received for course ID: {} by user: {}", courseId, userId);
        
        List<BatchResponseDto> batches = batchService.getBatchesByCourseId(courseId);
        ApiResponse<List<BatchResponseDto>> response = ApiResponse.success(
            "Batches retrieved successfully", 
            batches
        );
        
        log.info("BatchController : getBatchesByCourseId : Retrieved {} batches for course {} by user {}", batches.size(), courseId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get batches by university ID", description = "Retrieves all batches for a specific university (Admin only)")
    @GetMapping("/university/{universityId}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<List<BatchResponseDto>>> getBatchesByUniversityId(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "ID of the university", required = true)
            @PathVariable UUID universityId) {
        
        log.info("BatchController : getBatchesByUniversityId : Request received for university ID: {} by user: {}", universityId, userId);
        
        List<BatchResponseDto> batches = batchService.getBatchesByUniversityId(universityId);
        ApiResponse<List<BatchResponseDto>> response = ApiResponse.success(
            "Batches retrieved successfully", 
            batches
        );
        
        log.info("BatchController : getBatchesByUniversityId : Retrieved {} batches for university {} by user {}", batches.size(), universityId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update a batch", description = "Updates an existing batch (Admin only)")
    @PutMapping("/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<BatchResponseDto>> updateBatch(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "ID of the batch to update", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody BatchRequestDto requestDto) {
        
        log.info("BatchController : updateBatch : Request received for ID: {} by user: {}", id, userId);
        
        BatchResponseDto responseDto = batchService.updateBatch(id, requestDto, UUID.fromString(userId));
        ApiResponse<BatchResponseDto> response = ApiResponse.success(
            "Batch updated successfully", 
            responseDto
        );
        
        log.info("BatchController : updateBatch : Batch updated: {} by user {}", responseDto.getBatchName(), userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a batch", description = "Deletes a batch by its ID (Admin only)")
    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<Void>> deleteBatch(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "ID of the batch to delete", required = true)
            @PathVariable UUID id) {
        
        log.info("BatchController : deleteBatch : Request received for ID: {} by user {}", id, userId);
        
        batchService.deleteBatch(id);
        ApiResponse<Void> response = ApiResponse.success(
            "Batch deleted successfully", 
            null
        );
        
        log.info("BatchController : deleteBatch : Batch deleted with ID: {} by user {}", id, userId);
        return ResponseEntity.ok(response);
    }
}
