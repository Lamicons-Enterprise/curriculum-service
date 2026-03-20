package com.Lamicons.CurriculumService.Controller;

import com.Lamicons.CurriculumService.DTO.Portfolio.*;
import com.Lamicons.CurriculumService.DTO.University.ApiResponse;
import com.Lamicons.CurriculumService.Exception.UnauthorizedException;
import com.Lamicons.CurriculumService.Service.PortfolioService;
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
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/portfolio")
@Tag(name = "Portfolio Management", description = "APIs for managing Lamicons portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    private void validateAdminRole(String userRole) {
        if (userRole == null || 
            !(userRole.equalsIgnoreCase("ADMIN") || 
              userRole.equalsIgnoreCase("ROLE_ADMIN") || 
              userRole.equalsIgnoreCase("SUPER_ADMIN") || 
              userRole.equalsIgnoreCase("ROLE_SUPER_ADMIN"))) {
            log.warn("PortfolioController : Unauthorized access attempt with role: {}", userRole);
            throw new UnauthorizedException("Access denied. Admin role required.");
        }
    }

    @Operation(summary = "Get full portfolio", description = "Public API - Returns the complete Lamicons portfolio")
    @GetMapping
    public ResponseEntity<ApiResponse<PortfolioSummaryResponseDto>> getFullPortfolio() {
        log.info("PortfolioController : getFullPortfolio : Request received");
        PortfolioSummaryResponseDto portfolio = portfolioService.getFullPortfolio();
        log.info("PortfolioController : getFullPortfolio : Portfolio fetched successfully");
        return ResponseEntity.ok(ApiResponse.success("Portfolio retrieved successfully", portfolio));
    }

    @Operation(summary = "Get organisation details", description = "Public API - Returns organisation details")
    @GetMapping("/organisation")
    public ResponseEntity<ApiResponse<OrganisationDetailResponseDto>> getOrganisationDetails() {
        log.info("PortfolioController : getOrganisationDetails : Request received");
        OrganisationDetailResponseDto details = portfolioService.getOrganisationDetails();
        log.info("PortfolioController : getOrganisationDetails : Details fetched");
        return ResponseEntity.ok(ApiResponse.success("Organisation details retrieved successfully", details));
    }

    @Operation(summary = "Create or update organisation details", description = "Admin only - Upserts organisation details")
    @PutMapping("/organisation")
    public ResponseEntity<ApiResponse<OrganisationDetailResponseDto>> createOrUpdateOrganisationDetails(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Valid @RequestBody OrganisationDetailRequestDto requestDto) {
        log.info("PortfolioController : createOrUpdateOrganisationDetails : Request received from user: {}", userId);
        validateAdminRole(userRole);
        OrganisationDetailResponseDto response = portfolioService.createOrUpdateOrganisationDetails(requestDto);
        log.info("PortfolioController : createOrUpdateOrganisationDetails : Details saved");
        return ResponseEntity.ok(ApiResponse.success("Organisation details saved successfully", response));
    }

    @Operation(summary = "Get all featured trainers", description = "Public API - Returns all featured trainers")
    @GetMapping("/trainers")
    public ResponseEntity<ApiResponse<List<FeaturedTrainerResponseDto>>> getAllTrainers() {
        log.info("PortfolioController : getAllTrainers : Request received");
        List<FeaturedTrainerResponseDto> trainers = portfolioService.getAllTrainers();
        log.info("PortfolioController : getAllTrainers : Found {} trainers", trainers.size());
        return ResponseEntity.ok(ApiResponse.success("Trainers retrieved successfully", trainers));
    }

    @Operation(summary = "Get featured trainer by ID", description = "Public API - Returns a specific trainer")
    @GetMapping("/trainers/{id}")
    public ResponseEntity<ApiResponse<FeaturedTrainerResponseDto>> getTrainerById(@PathVariable UUID id) {
        log.info("PortfolioController : getTrainerById : Request received for ID: {}", id);
        FeaturedTrainerResponseDto trainer = portfolioService.getTrainerById(id);
        log.info("PortfolioController : getTrainerById : Trainer found: {}", trainer.getName());
        return ResponseEntity.ok(ApiResponse.success("Trainer retrieved successfully", trainer));
    }

    @Operation(summary = "Add a featured trainer", description = "Admin only - Adds a new featured trainer")
    @PostMapping("/trainers")
    public ResponseEntity<ApiResponse<FeaturedTrainerResponseDto>> createTrainer(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String userRole,
            @Valid @RequestBody FeaturedTrainerRequestDto requestDto) {
        log.info("PortfolioController : createTrainer : Request received from user: {}", userId);
        validateAdminRole(userRole);
        FeaturedTrainerResponseDto trainer = portfolioService.createTrainer(requestDto);
        log.info("PortfolioController : createTrainer : Trainer added with ID: {}", trainer.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Trainer added successfully", trainer));
    }

    @Operation(summary = "Update a featured trainer", description = "Admin only - Updates an existing trainer")
    @PutMapping("/trainers/{id}")
    public ResponseEntity<ApiResponse<FeaturedTrainerResponseDto>> updateTrainer(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String userRole,
            @PathVariable UUID id,
            @Valid @RequestBody FeaturedTrainerRequestDto requestDto) {
        log.info("PortfolioController : updateTrainer : Request received for ID: {} from user: {}", id, userId);
        validateAdminRole(userRole);
        FeaturedTrainerResponseDto trainer = portfolioService.updateTrainer(id, requestDto);
        log.info("PortfolioController : updateTrainer : Trainer updated: {}", trainer.getName());
        return ResponseEntity.ok(ApiResponse.success("Trainer updated successfully", trainer));
    }

    @Operation(summary = "Delete a featured trainer", description = "Admin only - Deletes a trainer")
    @DeleteMapping("/trainers/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTrainer(
            @RequestHeader("X-USER-ROLE") String userRole,
            @PathVariable UUID id) {
        log.info("PortfolioController : deleteTrainer : Request received for ID: {}", id);
        validateAdminRole(userRole);
        portfolioService.deleteTrainer(id);
        log.info("PortfolioController : deleteTrainer : Trainer deleted with ID: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Trainer deleted successfully", null));
    }

    @Operation(summary = "Get all featured students", description = "Public API - Returns all featured students")
    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<FeaturedStudentResponseDto>>> getAllStudents() {
        log.info("PortfolioController : getAllStudents : Request received");
        List<FeaturedStudentResponseDto> students = portfolioService.getAllStudents();
        log.info("PortfolioController : getAllStudents : Found {} students", students.size());
        return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", students));
    }

    @Operation(summary = "Get featured student by ID", description = "Public API - Returns a specific student")
    @GetMapping("/students/{id}")
    public ResponseEntity<ApiResponse<FeaturedStudentResponseDto>> getStudentById(@PathVariable UUID id) {
        log.info("PortfolioController : getStudentById : Request received for ID: {}", id);
        FeaturedStudentResponseDto student = portfolioService.getStudentById(id);
        log.info("PortfolioController : getStudentById : Student found: {}", student.getName());
        return ResponseEntity.ok(ApiResponse.success("Student retrieved successfully", student));
    }

    @Operation(summary = "Add a featured student", description = "Admin only - Adds a new featured student")
    @PostMapping("/students")
    public ResponseEntity<ApiResponse<FeaturedStudentResponseDto>> createStudent(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String userRole,
            @Valid @RequestBody FeaturedStudentRequestDto requestDto) {
        log.info("PortfolioController : createStudent : Request received from user: {}", userId);
        validateAdminRole(userRole);
        FeaturedStudentResponseDto student = portfolioService.createStudent(requestDto);
        log.info("PortfolioController : createStudent : Student added with ID: {}", student.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Student added successfully", student));
    }

    @Operation(summary = "Update a featured student", description = "Admin only - Updates an existing student")
    @PutMapping("/students/{id}")
    public ResponseEntity<ApiResponse<FeaturedStudentResponseDto>> updateStudent(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String userRole,
            @PathVariable UUID id,
            @Valid @RequestBody FeaturedStudentRequestDto requestDto) {
        log.info("PortfolioController : updateStudent : Request received for ID: {} from user: {}", id, userId);
        validateAdminRole(userRole);
        FeaturedStudentResponseDto student = portfolioService.updateStudent(id, requestDto);
        log.info("PortfolioController : updateStudent : Student updated: {}", student.getName());
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully", student));
    }

    @Operation(summary = "Delete a featured student", description = "Admin only - Deletes a student")
    @DeleteMapping("/students/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(
            @RequestHeader("X-USER-ROLE") String userRole,
            @PathVariable UUID id) {
        log.info("PortfolioController : deleteStudent : Request received for ID: {}", id);
        validateAdminRole(userRole);
        portfolioService.deleteStudent(id);
        log.info("PortfolioController : deleteStudent : Student deleted with ID: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", null));
    }

    @Operation(summary = "Get highlights by type", description = "Public API - Returns highlights filtered by type")
    @GetMapping("/highlights")
    public ResponseEntity<ApiResponse<List<PortfolioHighlightResponseDto>>> getHighlightsByType(
            @RequestParam PortfolioHighlightType type) {
        log.info("PortfolioController : getHighlightsByType : Request received for type: {}", type);
        List<PortfolioHighlightResponseDto> highlights = portfolioService.getHighlightsByType(type);
        log.info("PortfolioController : getHighlightsByType : Found {} highlights", highlights.size());
        return ResponseEntity.ok(ApiResponse.success("Highlights retrieved successfully", highlights));
    }

    @Operation(summary = "Get highlight by ID", description = "Public API - Returns a specific highlight")
    @GetMapping("/highlights/{id}")
    public ResponseEntity<ApiResponse<PortfolioHighlightResponseDto>> getHighlightById(@PathVariable UUID id) {
        log.info("PortfolioController : getHighlightById : Request received for ID: {}", id);
        PortfolioHighlightResponseDto highlight = portfolioService.getHighlightById(id);
        log.info("PortfolioController : getHighlightById : Highlight found: {}", highlight.getTitle());
        return ResponseEntity.ok(ApiResponse.success("Highlight retrieved successfully", highlight));
    }

    @Operation(summary = "Add a highlight", description = "Admin only - Adds a new highlight (achievement, client, or university partner)")
    @PostMapping("/highlights")
    public ResponseEntity<ApiResponse<PortfolioHighlightResponseDto>> createHighlight(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String userRole,
            @Valid @RequestBody PortfolioHighlightRequestDto requestDto) {
        log.info("PortfolioController : createHighlight : Request received from user: {}", userId);
        validateAdminRole(userRole);
        PortfolioHighlightResponseDto highlight = portfolioService.createHighlight(requestDto);
        log.info("PortfolioController : createHighlight : Highlight added with ID: {}", highlight.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Highlight added successfully", highlight));
    }

    @Operation(summary = "Update a highlight", description = "Admin only - Updates an existing highlight")
    @PutMapping("/highlights/{id}")
    public ResponseEntity<ApiResponse<PortfolioHighlightResponseDto>> updateHighlight(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String userRole,
            @PathVariable UUID id,
            @Valid @RequestBody PortfolioHighlightRequestDto requestDto) {
        log.info("PortfolioController : updateHighlight : Request received for ID: {} from user: {}", id, userId);
        validateAdminRole(userRole);
        PortfolioHighlightResponseDto highlight = portfolioService.updateHighlight(id, requestDto);
        log.info("PortfolioController : updateHighlight : Highlight updated: {}", highlight.getTitle());
        return ResponseEntity.ok(ApiResponse.success("Highlight updated successfully", highlight));
    }

    @Operation(summary = "Delete a highlight", description = "Admin only - Deletes a highlight")
    @DeleteMapping("/highlights/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHighlight(
            @RequestHeader("X-USER-ROLE") String userRole,
            @PathVariable UUID id) {
        log.info("PortfolioController : deleteHighlight : Request received for ID: {}", id);
        validateAdminRole(userRole);
        portfolioService.deleteHighlight(id);
        log.info("PortfolioController : deleteHighlight : Highlight deleted with ID: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Highlight deleted successfully", null));
    }
}
