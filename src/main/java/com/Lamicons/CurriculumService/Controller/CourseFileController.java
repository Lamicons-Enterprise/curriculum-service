package com.Lamicons.CurriculumService.Controller;

import com.Lamicons.CurriculumService.DTO.File.FileUploadResponseDto;
import com.Lamicons.CurriculumService.DTO.University.ApiResponse;
import com.Lamicons.CurriculumService.Exception.UnauthorizedException;
import com.Lamicons.CurriculumService.Service.CourseService;
import com.Lamicons.CurriculumService.Service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses/files")
@Tag(name = "Course File Management", description = "APIs for managing course files like banners, thumbnails, videos, and certificates")
@Slf4j
@RequiredArgsConstructor
public class CourseFileController {

    private final FileUploadService fileUploadService;
    private final CourseService courseService;

    private void validateAdminRole(String userRole) {
        if (userRole == null || 
            !(userRole.equalsIgnoreCase("ADMIN") || 
              userRole.equalsIgnoreCase("ROLE_ADMIN") || 
              userRole.equalsIgnoreCase("SUPER_ADMIN") || 
              userRole.equalsIgnoreCase("ROLE_SUPER_ADMIN"))) {
            log.warn("CourseFileController: Unauthorized access attempt with role: {}", userRole);
            throw new UnauthorizedException("Access denied. Admin role required.");
        }
    }

    @Operation(summary = "Upload course banner image [ADMIN]", description = "Uploads a banner image for a course and updates the course record")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "File uploaded successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = FileUploadResponseDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file or course ID"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Course not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/{courseId}/banner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FileUploadResponseDto>> uploadBanner(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "ID of the course to update", required = true)
            @PathVariable UUID courseId,
            @Parameter(description = "Banner image file (JPEG/PNG)", required = true)
            @RequestParam("file") MultipartFile file) {
        
        log.info("CourseFileController : uploadBanner : Uploading banner for course ID: {}", courseId);
        validateAdminRole(userRole);
        
        FileUploadResponseDto response = fileUploadService.uploadFile(file, courseId, "banner");
        
        if (response.isSuccess()) {
            // Update course with new banner URL
            courseService.updateCourseFileUrl(courseId, "banner", response.getFileUrl());
            log.info("CourseFileController : uploadBanner : Banner uploaded successfully for course ID: {}", courseId);
        } else {
            log.error("CourseFileController : uploadBanner : Failed to upload banner for course ID: {}", courseId);
        }
        
        ApiResponse<FileUploadResponseDto> apiResponse = ApiResponse.success("Banner uploaded successfully", response);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Upload course thumbnail image [ADMIN]", description = "Uploads a thumbnail image for a course and updates the course record")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "File uploaded successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = FileUploadResponseDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file or course ID"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Course not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/{courseId}/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FileUploadResponseDto>> uploadThumbnail(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "ID of the course to update", required = true)
            @PathVariable UUID courseId,
            @Parameter(description = "Thumbnail image file (JPEG/PNG)", required = true)
            @RequestParam("file") MultipartFile file) {
        
        log.info("CourseFileController : uploadThumbnail : Uploading thumbnail for course ID: {}", courseId);
        validateAdminRole(userRole);
        
        FileUploadResponseDto response = fileUploadService.uploadFile(file, courseId, "thumbnail");
        
        if (response.isSuccess()) {
            // Update course with new thumbnail URL
            courseService.updateCourseFileUrl(courseId, "thumbnail", response.getFileUrl());
            log.info("CourseFileController : uploadThumbnail : Thumbnail uploaded successfully for course ID: {}", courseId);
        } else {
            log.error("CourseFileController : uploadThumbnail : Failed to upload thumbnail for course ID: {}", courseId);
        }
        
        ApiResponse<FileUploadResponseDto> apiResponse = ApiResponse.success("Thumbnail uploaded successfully", response);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Upload course promo video [ADMIN]", description = "Uploads a promotional video for a course and updates the course record")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "File uploaded successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = FileUploadResponseDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file or course ID"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Course not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/{courseId}/promovideo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FileUploadResponseDto>> uploadPromoVideo(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "ID of the course to update", required = true)
            @PathVariable UUID courseId,
            @Parameter(description = "Promotional video file (MP4/MPEG)", required = true)
            @RequestParam("file") MultipartFile file) {
        
        log.info("CourseFileController : uploadPromoVideo : Uploading promo video for course ID: {}", courseId);
        validateAdminRole(userRole);
        
        FileUploadResponseDto response = fileUploadService.uploadFile(file, courseId, "promovideo");
        
        if (response.isSuccess()) {
            // Update course with new promo video URL
            courseService.updateCourseFileUrl(courseId, "promovideo", response.getFileUrl());
            log.info("CourseFileController : uploadPromoVideo : Promo video uploaded successfully for course ID: {}", courseId);
        } else {
            log.error("CourseFileController : uploadPromoVideo : Failed to upload promo video for course ID: {}", courseId);
        }
        
        ApiResponse<FileUploadResponseDto> apiResponse = ApiResponse.success("Promo video uploaded successfully", response);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Upload course certificate [ADMIN]", description = "Uploads a certificate template for a course and updates the course record")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "File uploaded successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = FileUploadResponseDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file or course ID"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Course not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/{courseId}/certificate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FileUploadResponseDto>> uploadCertificate(
            @Parameter(description = "User ID from header", required = true)
            @RequestHeader("X-USER-ID") String userId,
            @Parameter(description = "User role from header", required = true)
            @RequestHeader("X-USER-ROLE") String userRole,
            @Parameter(description = "ID of the course to update", required = true)
            @PathVariable UUID courseId,
            @Parameter(description = "Certificate template file (PDF)", required = true)
            @RequestParam("file") MultipartFile file) {
        
        log.info("CourseFileController : uploadCertificate : Uploading certificate for course ID: {}", courseId);
        validateAdminRole(userRole);
        
        FileUploadResponseDto response = fileUploadService.uploadFile(file, courseId, "certificate");
        
        if (response.isSuccess()) {
            // Update course with new certificate URL
            courseService.updateCourseFileUrl(courseId, "certificate", response.getFileUrl());
            log.info("CourseFileController : uploadCertificate : Certificate uploaded successfully for course ID: {}", courseId);
        } else {
            log.error("CourseFileController : uploadCertificate : Failed to upload certificate for course ID: {}", courseId);
        }
        
        ApiResponse<FileUploadResponseDto> apiResponse = ApiResponse.success("Certificate uploaded successfully", response);
        return ResponseEntity.ok(apiResponse);
    }
}