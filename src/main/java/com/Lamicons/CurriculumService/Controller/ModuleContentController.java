package com.Lamicons.CurriculumService.Controller;

import com.Lamicons.CurriculumService.DTO.Module.ModuleContentRequestDto;
import com.Lamicons.CurriculumService.DTO.Module.ModuleContentResponseDto;
import com.Lamicons.CurriculumService.DTO.University.ApiResponse;
import com.Lamicons.CurriculumService.Entity.ModuleContent;
import com.Lamicons.CurriculumService.Exception.UnauthorizedException;
import com.Lamicons.CurriculumService.Service.ModuleContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/module-content")
@Tag(name = "Module Content Management", description = "APIs for managing module content items")
@Slf4j
@RequiredArgsConstructor
public class ModuleContentController {

    private final ModuleContentService moduleContentService;

    private void validateAdminRole(String userRole) {
        if (userRole == null || !userRole.equalsIgnoreCase("ADMIN")) {
            log.warn("ModuleContentController: Unauthorized access attempt with role: {}", userRole);
            throw new UnauthorizedException("Access denied. Admin role required.");
        }
    }

    @Operation(summary = "Create new module content [ADMIN]", description = "Creates a new content item for a specific module")
    @PostMapping("/module/{moduleId}")
    public ResponseEntity<ApiResponse<ModuleContentResponseDto>> createModuleContent(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "ID of the module to add content to", required = true)
            @PathVariable UUID moduleId,
            @Parameter(description = "Content data", required = true)
            @Valid @RequestBody ModuleContentRequestDto request) {
        log.info("ModuleContentController : createModuleContent : Creating new content for module ID: {}", moduleId);
        validateAdminRole(userRole);
        ModuleContentResponseDto content = moduleContentService.createModuleContent(moduleId, request);

                
        log.info("ModuleContentController : createModuleContent : Content created successfully with ID: {}", content.getId());
        ApiResponse<ModuleContentResponseDto> response = ApiResponse.success("Module content created successfully", content);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get content by ID", description = "Retrieves a specific content item by its ID")
    @GetMapping("/{contentId}")
    public ResponseEntity<ApiResponse<ModuleContentResponseDto>> getModuleContent(
            @Parameter(description = "ID of the content to retrieve", required = true)
            @PathVariable UUID contentId) {
        log.info("ModuleContentController : getModuleContent : Retrieving content with ID: {}", contentId);
        ModuleContentResponseDto content = moduleContentService.getModuleContent(contentId);
                
        log.info("ModuleContentController : getModuleContent : Content retrieved successfully");
        ApiResponse<ModuleContentResponseDto> response = ApiResponse.success("Module content retrieved successfully", content);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update content [ADMIN]", description = "Updates an existing content item")
    @PatchMapping("/{contentId}")
    public ResponseEntity<ApiResponse<ModuleContentResponseDto>> updateModuleContent(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "ID of the content to update", required = true)
            @PathVariable UUID contentId,
            @Parameter(description = "Updated content data", required = true)
            @Valid @RequestBody ModuleContentRequestDto request) {
        log.info("ModuleContentController : updateModuleContent : Updating content with ID: {}", contentId);
        validateAdminRole(userRole);
        ModuleContentResponseDto content = moduleContentService.updateModuleContent(contentId, request);
                
        log.info("ModuleContentController : updateModuleContent : Content updated successfully");
        ApiResponse<ModuleContentResponseDto> response = ApiResponse.success("Module content updated successfully", content);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Delete content [ADMIN]", description = "Deletes a content item")
    @DeleteMapping("/{contentId}")
    public ResponseEntity<ApiResponse<Void>> deleteModuleContent(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "ID of the content to delete", required = true)
            @PathVariable UUID contentId) {
        log.info("ModuleContentController : deleteModuleContent : Deleting content with ID: {}", contentId);
        validateAdminRole(userRole);
        moduleContentService.deleteModuleContent(contentId);
        log.info("ModuleContentController : deleteModuleContent : Content deleted successfully");
        ApiResponse<Void> response = ApiResponse.success("Module content deleted successfully", null);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List all content for a module", description = "Retrieves all content items for a specific module")
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<ApiResponse<List<ModuleContentResponseDto>>> listContentsByModule(
            @Parameter(description = "ID of the module to get content for", required = true)
            @PathVariable UUID moduleId) {
        log.info("ModuleContentController : listContentsByModule : Retrieving content for module ID: {}", moduleId);
        List<ModuleContentResponseDto> contentList = moduleContentService.listContentsByModule(moduleId);
        log.info("ModuleContentController : listContentsByModule : Found {} content items for module", contentList.size());
        ApiResponse<List<ModuleContentResponseDto>> response = ApiResponse.success("Module content list retrieved successfully", contentList);
        return ResponseEntity.ok(response);
    }
}