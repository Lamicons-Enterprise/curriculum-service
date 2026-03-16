package com.Lamicons.CurriculumService.Controller;

import com.Lamicons.CurriculumService.DTO.Course.*;
import com.Lamicons.CurriculumService.DTO.University.ApiResponse;
import com.Lamicons.CurriculumService.Exception.UnauthorizedException;
import com.Lamicons.CurriculumService.Service.CourseService;
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
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Course Management", description = "Course CRUD operations and publishing workflow")
public class CourseController {
    
    private final CourseService courseService;

    private void validateAdminRole(String userRole) {
        if (userRole == null || !userRole.equalsIgnoreCase("ADMIN")) {
            log.warn("CourseController: Unauthorized access attempt with role: {}", userRole);
            throw new UnauthorizedException("Access denied. Admin role required.");
        }
    }

    @GetMapping
    @Operation(summary = "Get all courses", description = "Public endpoint. Lists all courses with optional status filter")
    public ResponseEntity<ApiResponse<List<CourseResponseDto>>> getAllCourses(
            @Parameter(description = "Filter by status") @RequestParam(required = false) CourseStatus status
    ) {
        log.info("GET /api/courses - status: {}", status);
        List<CourseResponseDto> courses = courseService.getAllCourses(status);
        ApiResponse<List<CourseResponseDto>> response = ApiResponse.success("Courses retrieved successfully", courses);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID", description = "Public endpoint. Retrieves basic course information")
    public ResponseEntity<ApiResponse<CourseResponseDto>> getCourseById(
            @Parameter(description = "Course ID", required = true) @PathVariable UUID id
    ) {
        log.info("GET /api/courses/{}", id);
        CourseResponseDto course = courseService.getCourseById(id);
        ApiResponse<CourseResponseDto> response = ApiResponse.success("Course retrieved successfully", course);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @Operation(summary = "Create new course [ADMIN]", description = "Admin endpoint. Creates a new course in DRAFT status")
    public ResponseEntity<ApiResponse<CourseResponseDto>> createCourse(
            @Parameter(description = "User ID from header", required = true) @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true) @RequestHeader("X-USER-ROLE") String userRole,
            @Valid @RequestBody CourseCreateRequestDto request
    ) {
        log.info("POST /api/courses - Creating course: {}", request.getName());
        validateAdminRole(userRole);
        CourseResponseDto course = courseService.createCourse(request);
        ApiResponse<CourseResponseDto> response = ApiResponse.success("Course created successfully", course);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update course [ADMIN]", description = "Admin endpoint. Updates course basic information")
    public ResponseEntity<ApiResponse<CourseResponseDto>> updateCourse(
            @Parameter(description = "User ID from header", required = true) @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true) @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Course ID", required = true) @PathVariable UUID id,
            @Valid @RequestBody CourseCreateRequestDto request
    ) {
        log.info("PUT /api/courses/{} - Updating course", id);
        validateAdminRole(userRole);
        CourseResponseDto course = courseService.updateCourse(id, request);
        ApiResponse<CourseResponseDto> response = ApiResponse.success("Course updated successfully", course);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete course [ADMIN]", description = "Admin endpoint. Permanently deletes a course and all its associations")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(
            @Parameter(description = "User ID from header", required = true) @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true) @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Course ID", required = true) @PathVariable UUID id
    ) {
        log.info("DELETE /api/courses/{}", id);
        validateAdminRole(userRole);
        courseService.deleteCourse(id);
        ApiResponse<Void> response = ApiResponse.success("Course deleted successfully", null);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/hierarchy")
    @Operation(summary = "Get course hierarchy [ADMIN]", description = "Admin endpoint. Retrieves complete course structure for review before publishing")
    public ResponseEntity<ApiResponse<CourseHierarchyResponseDto>> getCourseHierarchy(
            @Parameter(description = "User ID from header", required = true) @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true) @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Course ID", required = true) @PathVariable UUID id
    ) {
        log.info("GET /api/courses/{}/hierarchy", id);
        validateAdminRole(userRole);
        CourseHierarchyResponseDto hierarchy = courseService.getCourseHierarchy(id);
        ApiResponse<CourseHierarchyResponseDto> response = ApiResponse.success("Course hierarchy retrieved successfully", hierarchy);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/completeness")
    @Operation(summary = "Check course completeness [ADMIN]", description = "Admin endpoint. Validates if course is complete and ready for publishing")
    public ResponseEntity<ApiResponse<Boolean>> checkCompleteness(
            @Parameter(description = "User ID from header", required = true) @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true) @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Course ID", required = true) @PathVariable UUID id
    ) {
        log.info("GET /api/courses/{}/completeness", id);
        validateAdminRole(userRole);
        boolean isComplete = courseService.isCourseComplete(id);
        ApiResponse<Boolean> response = ApiResponse.success("Course completeness checked successfully", isComplete);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/publish")
    @Operation(summary = "Publish course [ADMIN]", description = "Admin endpoint. Publishes course (DRAFT → PUBLISHED)")
    public ResponseEntity<ApiResponse<CourseResponseDto>> publishCourse(
            @Parameter(description = "User ID from header", required = true) @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true) @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Course ID", required = true) @PathVariable UUID id
    ) {
        log.info("POST /api/courses/{}/publish", id);
        validateAdminRole(userRole);
        CourseResponseDto course = courseService.publishCourse(id);
        ApiResponse<CourseResponseDto> response = ApiResponse.success("Course published successfully", course);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/unpublish")
    @Operation(summary = "Unpublish course [ADMIN]", description = "Admin endpoint. Moves course back to DRAFT status")
    public ResponseEntity<ApiResponse<CourseResponseDto>> unpublishCourse(
            @Parameter(description = "User ID from header", required = true) @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true) @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Course ID", required = true) @PathVariable UUID id
    ) {
        log.info("POST /api/courses/{}/unpublish", id);
        validateAdminRole(userRole);
        CourseResponseDto course = courseService.unpublishCourse(id);
        ApiResponse<CourseResponseDto> response = ApiResponse.success("Course unpublished successfully", course);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/archive")
    @Operation(summary = "Archive course [ADMIN]", description = "Admin endpoint. Archives course (any status → ARCHIVED)")
    public ResponseEntity<ApiResponse<CourseResponseDto>> archiveCourse(
            @Parameter(description = "User ID from header", required = true) @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true) @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Course ID", required = true) @PathVariable UUID id
    ) {
        log.info("POST /api/courses/{}/archive", id);
        validateAdminRole(userRole);
        CourseResponseDto course = courseService.archiveCourse(id);
        ApiResponse<CourseResponseDto> response = ApiResponse.success("Course archived successfully", course);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update course status [ADMIN]", description = "Admin endpoint. Manually updates course status")
    public ResponseEntity<ApiResponse<CourseResponseDto>> updateStatus(
            @Parameter(description = "User ID from header", required = true) @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true) @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "Course ID", required = true) @PathVariable UUID id,
            @Parameter(description = "New status", required = true) @RequestParam CourseStatus status
    ) {
        log.info("PATCH /api/courses/{}/status - status: {}", id, status);
        validateAdminRole(userRole);
        CourseResponseDto course = courseService.updateCourseStatus(id, status);
        ApiResponse<CourseResponseDto> response = ApiResponse.success("Course status updated successfully", course);
        return ResponseEntity.ok(response);
    }
}
