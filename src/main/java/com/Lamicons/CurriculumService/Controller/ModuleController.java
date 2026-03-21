package com.Lamicons.CurriculumService.Controller;

import com.Lamicons.CurriculumService.DTO.Course.CourseResponseDto;
import com.Lamicons.CurriculumService.DTO.Module.*;
import com.Lamicons.CurriculumService.DTO.University.ApiResponse;
import com.Lamicons.CurriculumService.Exception.UnauthorizedException;
import com.Lamicons.CurriculumService.Service.ModuleService;
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
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Module Management", description = "Module CRUD operations and course-module relationships")
public class ModuleController {
    
    private final ModuleService moduleService;

    private void validateAdminRole(String userRole) {
        if (userRole == null || 
            !(userRole.equalsIgnoreCase("ADMIN") || 
              userRole.equalsIgnoreCase("ROLE_ADMIN") || 
              userRole.equalsIgnoreCase("SUPER_ADMIN") || 
              userRole.equalsIgnoreCase("ROLE_SUPER_ADMIN"))) {
            log.warn("ModuleController: Unauthorized access attempt with role: {}", userRole);
            throw new UnauthorizedException("Access denied. Admin role required.");
        }
    }

    
    
    @GetMapping
    @Operation(
        summary = "Get all reusable modules",
        description = "Public endpoint. Lists all reusable modules for course builder"
    )
    public ResponseEntity<ApiResponse<List<ModuleSummaryDto>>> getAllReusableModules() {
        log.info("GET /api/modules - Getting all reusable modules");
        List<ModuleSummaryDto> modules = moduleService.getAllReusableModules();
        ApiResponse<List<ModuleSummaryDto>> response = ApiResponse.success("Modules retrieved successfully", modules);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Get module by ID",
        description = "Public endpoint. Retrieves module details"
    )
    public ResponseEntity<ApiResponse<ModuleSummaryDto>> getModuleById(
            @Parameter(description = "Module ID", required = true) @PathVariable UUID id
    ) {
        log.info("GET /api/modules/{}", id);
        ModuleSummaryDto module = moduleService.getModuleById(id);
        ApiResponse<ModuleSummaryDto> response = ApiResponse.success("Module retrieved successfully", module);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @Operation(
        summary = "Create standalone module [ADMIN]",
        description = "Admin endpoint. Creates a reusable module that can be attached to multiple courses"
    )
    public ResponseEntity<ApiResponse<ModuleSummaryDto>> createModule(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "User email from header", required = true)
            @RequestHeader("X-USER-EMAIL") String userEmail,
            @Valid @RequestBody ModuleAttachmentRequestDto.NewModuleDto request
    ) {
        log.info("POST /api/modules - Creating module: {} by user: {}", request.getTitle(), userEmail);
        validateAdminRole(userRole);
        ModuleSummaryDto module = moduleService.createStandaloneModule(request);
        ApiResponse<ModuleSummaryDto> response = ApiResponse.success("Module created successfully", module);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @Operation(
        summary = "Update module [ADMIN]",
        description = "Admin endpoint. Updates module details"
    )
    public ResponseEntity<ApiResponse<ModuleSummaryDto>> updateModule(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "User email from header", required = true)
            @RequestHeader("X-USER-EMAIL") String userEmail,
            @Parameter(description = "Module ID", required = true) @PathVariable UUID id,
            @Valid @RequestBody ModuleAttachmentRequestDto.NewModuleDto request
    ) {
        log.info("PUT /api/modules/{} - Updating module by user: {}", id, userEmail);
        validateAdminRole(userRole);
        ModuleSummaryDto module = moduleService.updateModule(id, request);
        ApiResponse<ModuleSummaryDto> response = ApiResponse.success("Module updated successfully", module);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete module [ADMIN]",
        description = "Admin endpoint. Soft deletes a module"
    )
    public ResponseEntity<ApiResponse<Void>> deleteModule(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "User email from header", required = true)
            @RequestHeader("X-USER-EMAIL") String userEmail,
            @Parameter(description = "Module ID", required = true) @PathVariable UUID id
    ) {
        log.info("DELETE /api/modules/{} by user: {}", id, userEmail);
        validateAdminRole(userRole);
        moduleService.deleteModule(id);
        ApiResponse<Void> response = ApiResponse.success("Module deleted successfully", null);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    @Operation(
        summary = "Search modules",
        description = "Public endpoint. Searches modules by title or tags"
    )
    public ResponseEntity<ApiResponse<List<ModuleSummaryDto>>> searchModules(
            @Parameter(description = "Search term", required = true) @RequestParam String searchTerm
    ) {
        log.info("GET /api/modules/search - searchTerm: {}", searchTerm);
        List<ModuleSummaryDto> modules = moduleService.searchModules(searchTerm);
        ApiResponse<List<ModuleSummaryDto>> response = ApiResponse.success("Modules found successfully", modules);
        return ResponseEntity.ok(response);
    }
    
    
    @GetMapping("/course/{courseId}")
    @Operation(
        summary = "Get modules by course",
        description = "Public endpoint. Lists all modules attached to a course"
    )
    public ResponseEntity<ApiResponse<List<ModuleSummaryDto>>> getModulesByCourse(
            @Parameter(description = "Course ID", required = true) @PathVariable UUID courseId
    ) {
        log.info("GET /api/modules/course/{}", courseId);
        List<ModuleSummaryDto> modules = moduleService.getModulesByCourseId(courseId);
        ApiResponse<List<ModuleSummaryDto>> response = ApiResponse.success("Modules retrieved successfully", modules);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/attach-to-course")
    @Operation(
        summary = "Attach modules to course [ADMIN]",
        description = "Admin endpoint. Attaches existing or new modules to a course"
    )
    public ResponseEntity<ApiResponse<CourseResponseDto>> attachModulesToCourse(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "User email from header", required = true)
            @RequestHeader("X-USER-EMAIL") String userEmail,
            @Valid @RequestBody ModuleAttachmentRequestDto request
    ) {
        log.info("POST /api/modules/attach-to-course - courseId: {} by user: {}", request.getCourseId(), userEmail);
        validateAdminRole(userRole);
        CourseResponseDto course = moduleService.attachModulesToCourse(request);
        ApiResponse<CourseResponseDto> response = ApiResponse.success("Modules attached to course successfully", course);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/course/{courseId}/modules/{moduleId}")
    @Operation(
        summary = "Remove module from course [ADMIN]",
        description = "Admin endpoint. Removes module from course (soft delete junction table entry)"
    )
    public ResponseEntity<ApiResponse<Void>> removeModuleFromCourse(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "User email from header", required = true)
            @RequestHeader("X-USER-EMAIL") String userEmail,
            @Parameter(description = "Course ID", required = true) @PathVariable UUID courseId,
            @Parameter(description = "Module ID", required = true) @PathVariable UUID moduleId
    ) {
        log.info("DELETE /api/modules/course/{}/modules/{} by user: {}", courseId, moduleId, userEmail);
        validateAdminRole(userRole);
        moduleService.removeModuleFromCourse(courseId, moduleId);
        ApiResponse<Void> response = ApiResponse.success("Module removed from course successfully", null);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/course/{courseId}/reorder")
    @Operation(
        summary = "Reorder modules in course [ADMIN]",
        description = "Admin endpoint. Updates the display order of modules within a course"
    )
    public ResponseEntity<ApiResponse<CourseResponseDto>> reorderModules(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "User email from header", required = true)
            @RequestHeader("X-USER-EMAIL") String userEmail,
            @Parameter(description = "Course ID", required = true) @PathVariable UUID courseId,
            @RequestBody List<UUID> moduleIdsInOrder
    ) {
        log.info("PUT /api/modules/course/{}/reorder by user: {}", courseId, userEmail);
        validateAdminRole(userRole);
        CourseResponseDto course = moduleService.reorderModules(courseId, moduleIdsInOrder);
        ApiResponse<CourseResponseDto> response = ApiResponse.success("Modules reordered successfully", course);
        return ResponseEntity.ok(response);
    }
}
