package com.Lamicons.CurriculumService.Controller;

import com.Lamicons.CurriculumService.Annotation.RequireRole;
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

    @Operation(summary = "Get full portfolio", description = "Returns the complete Lamicons portfolio")
    @GetMapping
    @RequireRole({"ADMIN", "SUPER_ADMIN", "STUDENT"})
    public ResponseEntity<ApiResponse<PortfolioSummaryResponseDto>> getFullPortfolio(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole
    ) {
        log.info("PortfolioController : getFullPortfolio : Request received from user: {}", userId);
        PortfolioSummaryResponseDto portfolio = portfolioService.getFullPortfolio();
        log.info("PortfolioController : getFullPortfolio : Portfolio fetched successfully for user: {}", userId);
        return ResponseEntity.ok(ApiResponse.success("Portfolio retrieved successfully", portfolio));
    }

    @Operation(summary = "Get organisation details", description = "Returns organisation details")
    @GetMapping("/organisation")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "STUDENT"})
    public ResponseEntity<ApiResponse<OrganisationDetailResponseDto>> getOrganisationDetails(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole
    ) {
        log.info("PortfolioController : getOrganisationDetails : Request received from user: {}", userId);
        OrganisationDetailResponseDto details = portfolioService.getOrganisationDetails();
        log.info("PortfolioController : getOrganisationDetails : Details fetched for user: {}", userId);
        return ResponseEntity.ok(ApiResponse.success("Organisation details retrieved successfully", details));
    }

    @Operation(summary = "Create or update organisation details", description = "Admin only - Upserts organisation details")
    @PutMapping("/organisation")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<OrganisationDetailResponseDto>> createOrUpdateOrganisationDetails(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Valid @RequestBody OrganisationDetailRequestDto requestDto) {
        log.info("PortfolioController : createOrUpdateOrganisationDetails : Request received from user: {}", userId);
        OrganisationDetailResponseDto response = portfolioService.createOrUpdateOrganisationDetails(requestDto);
        log.info("PortfolioController : createOrUpdateOrganisationDetails : Details saved by user: {}", userId);
        return ResponseEntity.ok(ApiResponse.success("Organisation details saved successfully", response));
    }

    @Operation(summary = "Get all featured trainers", description = "Returns all featured trainers")
    @GetMapping("/trainers")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "STUDENT"})
    public ResponseEntity<ApiResponse<List<FeaturedTrainerResponseDto>>> getAllTrainers(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole
    ) {
        log.info("PortfolioController : getAllTrainers : Request received from user: {}", userId);
        List<FeaturedTrainerResponseDto> trainers = portfolioService.getAllTrainers();
        log.info("PortfolioController : getAllTrainers : Found {} trainers for user: {}", trainers.size(), userId);
        return ResponseEntity.ok(ApiResponse.success("Trainers retrieved successfully", trainers));
    }

    @Operation(summary = "Get featured trainer by ID", description = "Returns a specific trainer")
    @GetMapping("/trainers/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "STUDENT"})
    public ResponseEntity<ApiResponse<FeaturedTrainerResponseDto>> getTrainerById(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @PathVariable UUID id) {
        log.info("PortfolioController : getTrainerById : Request received for ID: {} from user: {}", id, userId);
        FeaturedTrainerResponseDto trainer = portfolioService.getTrainerById(id);
        log.info("PortfolioController : getTrainerById : Trainer found: {} for user: {}", trainer.getName(), userId);
        return ResponseEntity.ok(ApiResponse.success("Trainer retrieved successfully", trainer));
    }

    @Operation(summary = "Add a featured trainer", description = "Admin only - Adds a new featured trainer")
    @PostMapping("/trainers")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<FeaturedTrainerResponseDto>> createTrainer(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String userRole,
            @Valid @RequestBody FeaturedTrainerRequestDto requestDto) {
        log.info("PortfolioController : createTrainer : Request received from user: {}", userId);
        FeaturedTrainerResponseDto trainer = portfolioService.createTrainer(requestDto);
        log.info("PortfolioController : createTrainer : Trainer added with ID: {} by user: {}", trainer.getId(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Trainer added successfully", trainer));
    }

    @Operation(summary = "Update a featured trainer", description = "Admin only - Updates an existing trainer")
    @PutMapping("/trainers/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<FeaturedTrainerResponseDto>> updateTrainer(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String userRole,
            @PathVariable UUID id,
            @Valid @RequestBody FeaturedTrainerRequestDto requestDto) {
        log.info("PortfolioController : updateTrainer : Request received for ID: {} from user: {}", id, userId);
        FeaturedTrainerResponseDto trainer = portfolioService.updateTrainer(id, requestDto);
        log.info("PortfolioController : updateTrainer : Trainer updated: {} by user: {}", trainer.getName(), userId);
        return ResponseEntity.ok(ApiResponse.success("Trainer updated successfully", trainer));
    }

    @Operation(summary = "Delete a featured trainer", description = "Admin only - Deletes a trainer")
    @DeleteMapping("/trainers/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<Void>> deleteTrainer(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String userRole,
            @PathVariable UUID id) {
        log.info("PortfolioController : deleteTrainer : Request received for ID: {} from user: {}", id, userId);
        portfolioService.deleteTrainer(id);
        log.info("PortfolioController : deleteTrainer : Trainer deleted with ID: {} by user: {}", id, userId);
        return ResponseEntity.ok(ApiResponse.success("Trainer deleted successfully", null));
    }

    @Operation(summary = "Get all featured students", description = "Returns all featured students")
    @GetMapping("/students")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "STUDENT"})
    public ResponseEntity<ApiResponse<List<FeaturedStudentResponseDto>>> getAllStudents(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole
    ) {
        log.info("PortfolioController : getAllStudents : Request received from user: {}", userId);
        List<FeaturedStudentResponseDto> students = portfolioService.getAllStudents();
        log.info("PortfolioController : getAllStudents : Found {} students for user: {}", students.size(), userId);
        return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", students));
    }

    @Operation(summary = "Get featured student by ID", description = "Returns a specific student")
    @GetMapping("/students/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "STUDENT"})
    public ResponseEntity<ApiResponse<FeaturedStudentResponseDto>> getStudentById(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @PathVariable UUID id) {
        log.info("PortfolioController : getStudentById : Request received for ID: {} from user: {}", id, userId);
        FeaturedStudentResponseDto student = portfolioService.getStudentById(id);
        log.info("PortfolioController : getStudentById : Student found: {} for user: {}", student.getName(), userId);
        return ResponseEntity.ok(ApiResponse.success("Student retrieved successfully", student));
    }

    @Operation(summary = "Add a featured student", description = "Admin only - Adds a new featured student")
    @PostMapping("/students")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<FeaturedStudentResponseDto>> createStudent(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String userRole,
            @Valid @RequestBody FeaturedStudentRequestDto requestDto) {
        log.info("PortfolioController : createStudent : Request received from user: {}", userId);
        FeaturedStudentResponseDto student = portfolioService.createStudent(requestDto);
        log.info("PortfolioController : createStudent : Student added with ID: {} by user: {}", student.getId(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Student added successfully", student));
    }

    @Operation(summary = "Update a featured student", description = "Admin only - Updates an existing student")
    @PutMapping("/students/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<FeaturedStudentResponseDto>> updateStudent(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String userRole,
            @PathVariable UUID id,
            @Valid @RequestBody FeaturedStudentRequestDto requestDto) {
        log.info("PortfolioController : updateStudent : Request received for ID: {} from user: {}", id, userId);
        FeaturedStudentResponseDto student = portfolioService.updateStudent(id, requestDto);
        log.info("PortfolioController : updateStudent : Student updated: {} by user: {}", student.getName(), userId);
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully", student));
    }

    @Operation(summary = "Delete a featured student", description = "Admin only - Deletes a student")
    @DeleteMapping("/students/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<Void>> deleteStudent(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String userRole,
            @PathVariable UUID id) {
        log.info("PortfolioController : deleteStudent : Request received for ID: {} from user: {}", id, userId);
        portfolioService.deleteStudent(id);
        log.info("PortfolioController : deleteStudent : Student deleted with ID: {} by user: {}", id, userId);
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", null));
    }

    @Operation(summary = "Get highlights by type", description = "Returns highlights filtered by type")
    @GetMapping("/highlights")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "STUDENT"})
    public ResponseEntity<ApiResponse<List<PortfolioHighlightResponseDto>>> getHighlightsByType(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @RequestParam PortfolioHighlightType type) {
        log.info("PortfolioController : getHighlightsByType : Request received for type: {} from user: {}", type, userId);
        List<PortfolioHighlightResponseDto> highlights = portfolioService.getHighlightsByType(type);
        log.info("PortfolioController : getHighlightsByType : Found {} highlights for user: {}", highlights.size(), userId);
        return ResponseEntity.ok(ApiResponse.success("Highlights retrieved successfully", highlights));
    }

    @Operation(summary = "Get highlight by ID", description = "Returns a specific highlight")
    @GetMapping("/highlights/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "STUDENT"})
    public ResponseEntity<ApiResponse<PortfolioHighlightResponseDto>> getHighlightById(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @PathVariable UUID id) {
        log.info("PortfolioController : getHighlightById : Request received for ID: {} from user: {}", id, userId);
        PortfolioHighlightResponseDto highlight = portfolioService.getHighlightById(id);
        log.info("PortfolioController : getHighlightById : Highlight found: {} for user: {}", highlight.getTitle(), userId);
        return ResponseEntity.ok(ApiResponse.success("Highlight retrieved successfully", highlight));
    }

    @Operation(summary = "Add a highlight", description = "Admin only - Adds a new highlight (achievement, client, or university partner)")
    @PostMapping("/highlights")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<PortfolioHighlightResponseDto>> createHighlight(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String userRole,
            @Valid @RequestBody PortfolioHighlightRequestDto requestDto) {
        log.info("PortfolioController : createHighlight : Request received from user: {}", userId);
        PortfolioHighlightResponseDto highlight = portfolioService.createHighlight(requestDto);
        log.info("PortfolioController : createHighlight : Highlight added with ID: {} by user: {}", highlight.getId(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Highlight added successfully", highlight));
    }

    @Operation(summary = "Update a highlight", description = "Admin only - Updates an existing highlight")
    @PutMapping("/highlights/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<PortfolioHighlightResponseDto>> updateHighlight(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String userRole,
            @PathVariable UUID id,
            @Valid @RequestBody PortfolioHighlightRequestDto requestDto) {
        log.info("PortfolioController : updateHighlight : Request received for ID: {} from user: {}", id, userId);
        PortfolioHighlightResponseDto highlight = portfolioService.updateHighlight(id, requestDto);
        log.info("PortfolioController : updateHighlight : Highlight updated: {} by user: {}", highlight.getTitle(), userId);
        return ResponseEntity.ok(ApiResponse.success("Highlight updated successfully", highlight));
    }

    @Operation(summary = "Delete a highlight", description = "Admin only - Deletes a highlight")
    @DeleteMapping("/highlights/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<Void>> deleteHighlight(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String userRole,
            @PathVariable UUID id) {
        log.info("PortfolioController : deleteHighlight : Request received for ID: {} from user: {}", id, userId);
        portfolioService.deleteHighlight(id);
        log.info("PortfolioController : deleteHighlight : Highlight deleted with ID: {} by user: {}", id, userId);
        return ResponseEntity.ok(ApiResponse.success("Highlight deleted successfully", null));
    }
}
