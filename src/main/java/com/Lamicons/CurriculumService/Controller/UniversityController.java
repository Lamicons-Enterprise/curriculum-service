package com.Lamicons.CurriculumService.Controller;

import com.Lamicons.CurriculumService.DTO.University.ApiResponse;
import com.Lamicons.CurriculumService.DTO.University.UniversityRequestDto;
import com.Lamicons.CurriculumService.DTO.University.UniversityResponseDto;
import com.Lamicons.CurriculumService.Exception.UnauthorizedException;
import com.Lamicons.CurriculumService.Service.UniversityService;
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
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/university")
@Tag(name = "University Management", description = "APIs for managing universities (Admin only)")
public class UniversityController {

    private final UniversityService universityService;

    private void validateAdminRole(String userRole) {
        if (userRole == null || !userRole.equalsIgnoreCase("ADMIN")) {
            log.warn("UniversityController : Unauthorized access attempt with role: {}", userRole);
            throw new UnauthorizedException("Access denied. Admin role required.");
        }
    }

    @Operation(summary = "Create a new university", description = "Creates a new university (Admin only)")
    @PostMapping
    public ResponseEntity<ApiResponse<UniversityResponseDto>> createUniversity(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Valid @RequestBody UniversityRequestDto requestDto) {
        
        log.info("UniversityController : createUniversity : Request received from user: {}", userId);
        validateAdminRole(userRole);
        UniversityResponseDto responseDto = universityService.createUniversity(requestDto, userId);
        ApiResponse<UniversityResponseDto> response = ApiResponse.success(
            "University created successfully", 
            responseDto
        );
        
        log.info("UniversityController : createUniversity : University created with ID: {}", responseDto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get university by ID", description = "Retrieves a university by its ID (Admin only)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UniversityResponseDto>> getUniversityById(
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "ID of the university to retrieve", required = true)
            @PathVariable UUID id) {
        
        log.info("UniversityController : getUniversityById : Request received for ID: {}", id);
        validateAdminRole(userRole);
        
        UniversityResponseDto responseDto = universityService.getUniversityById(id);
        ApiResponse<UniversityResponseDto> response = ApiResponse.success(
            "University retrieved successfully", 
            responseDto
        );
        
        log.info("UniversityController : getUniversityById : University retrieved: {}", responseDto.getName());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all universities", description = "Retrieves all universities (Admin only)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UniversityResponseDto>>> getAllUniversities(
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole) {
        
        log.info("UniversityController : getAllUniversities : Request received");
        validateAdminRole(userRole);
        
        List<UniversityResponseDto> universities = universityService.getAllUniversities();
        ApiResponse<List<UniversityResponseDto>> response = ApiResponse.success(
            "Universities retrieved successfully", 
            universities
        );
        
        log.info("UniversityController : getAllUniversities : Retrieved {} universities", universities.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update a university", description = "Updates an existing university (Admin only)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UniversityResponseDto>> updateUniversity(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "ID of the university to update", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UniversityRequestDto requestDto) {
        
        log.info("UniversityController : updateUniversity : Request received for ID: {} by user: {}", id, userId);
        validateAdminRole(userRole);
        
        UniversityResponseDto responseDto = universityService.updateUniversity(id, requestDto, userId);
        ApiResponse<UniversityResponseDto> response = ApiResponse.success(
            "University updated successfully", 
            responseDto
        );
        
        log.info("UniversityController : updateUniversity : University updated: {}", responseDto.getName());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a university", description = "Deletes a university by its ID (Admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUniversity(
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "ID of the university to delete", required = true)
            @PathVariable UUID id) {
        
        log.info("UniversityController : deleteUniversity : Request received for ID: {}", id);
        validateAdminRole(userRole);
        
        universityService.deleteUniversity(id);
        ApiResponse<Void> response = ApiResponse.success(
            "University deleted successfully", 
            null
        );
        
        log.info("UniversityController : deleteUniversity : University deleted with ID: {}", id);
        return ResponseEntity.ok(response);
    }
}
