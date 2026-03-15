package com.Lamicons.CurriculumService.Controller;

import com.Lamicons.CurriculumService.DTO.University.ApiResponse;
import com.Lamicons.CurriculumService.Service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/storage")
@Tag(name = "Storage & Upload Management", description = "Endpoints for handling file uploads over object storage (S3/Cloudinary)")
@Slf4j
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @Operation(summary = "Get upload signature/URL", description = "Generates secure URL/credentials for client-side direct upload")
    @GetMapping("/upload-signature")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUploadSignature(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String userRole,
            @RequestParam("folder") String folder,
            @RequestParam(value = "fileName", required = false) String fileName) {
            
        // Check role/auth if needed
        Map<String, Object> credentials = storageService.generateUploadSignature(folder, fileName);
        return ResponseEntity.ok(ApiResponse.success("Upload credentials generated.", credentials));
    }
}