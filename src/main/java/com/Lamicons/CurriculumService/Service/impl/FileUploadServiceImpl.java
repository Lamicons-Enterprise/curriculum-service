package com.Lamicons.CurriculumService.Service.impl;

import com.Lamicons.CurriculumService.Config.S3Config;
import com.Lamicons.CurriculumService.DTO.File.FileUploadResponseDto;
import com.Lamicons.CurriculumService.Service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    private final S3Client s3Client;
    private final String bucketName;
    private final String prefix;
    private final boolean s3Enabled;

    @Value("${file.upload.max-size:10485760}") // 10MB default
    private String maxFileSizeStr;
    
    private long maxFileSize;

    @Value("${file.upload.allowed-image-types:image/jpeg,image/png,image/gif}")
    private String allowedImageTypes;

    @Value("${file.upload.allowed-video-types:video/mp4,video/mpeg,video/webm}")
    private String allowedVideoTypes;

    @Value("${file.upload.allowed-document-types:application/pdf}")
    private String allowedDocumentTypes;

    public FileUploadServiceImpl(S3Config s3Config, S3Client s3Client) {
        this.s3Client = s3Client;
        this.bucketName = s3Config.getBucketName();
        this.prefix = s3Config.getPrefix();
        this.s3Enabled = s3Config.isS3Enabled();
    }
    
    @PostConstruct
    public void init() {
        // Convert string value like "10MB" to bytes
        try {
            this.maxFileSize = parseFileSize(maxFileSizeStr);
            log.info("Max file size set to {} bytes", maxFileSize);
        } catch (Exception e) {
            // Default to 10MB if parsing fails
            this.maxFileSize = 10485760L;
            log.warn("Failed to parse max file size '{}', defaulting to {} bytes", maxFileSizeStr, maxFileSize);
        }
    }
    
    /**
     * Parses file size string with units (KB, MB, GB) to bytes
     * @param sizeStr String representing file size (e.g., "10MB")
     * @return Size in bytes
     */
    private long parseFileSize(String sizeStr) {
        if (sizeStr == null || sizeStr.isEmpty()) {
            return 10485760L; // Default 10MB
        }
        
        sizeStr = sizeStr.trim().toUpperCase();
        
        if (sizeStr.matches("\\d+")) {
            // Plain number, assume bytes
            return Long.parseLong(sizeStr);
        }
        
        // Extract the number and unit parts
        String number = sizeStr.replaceAll("[^\\d.]", "");
        String unit = sizeStr.replaceAll("[\\d.]", "").trim();
        
        double value = Double.parseDouble(number);
        
        // Convert to bytes based on unit
        switch (unit) {
            case "KB":
                return (long) (value * 1024);
            case "MB":
                return (long) (value * 1024 * 1024);
            case "GB":
                return (long) (value * 1024 * 1024 * 1024);
            default:
                return (long) value; // Assume bytes if no unit is recognized
        }
    }

    @Override
    public FileUploadResponseDto uploadFile(MultipartFile file, UUID courseId, String fileType) {
        log.info("FileUploadService: uploadFile: Starting upload for courseId: {}, fileType: {}", courseId, fileType);
        
        if (!s3Enabled) {
            log.warn("FileUploadService: uploadFile: S3 is not enabled. File upload is simulated.");
            return simulateFileUpload(file, courseId, fileType);
        }
        
        // Validate file
        if (!validateFile(file, fileType)) {
            log.error("FileUploadService: uploadFile: File validation failed");
            return FileUploadResponseDto.builder()
                    .success(false)
                    .message("File validation failed. Check file type and size.")
                    .build();
        }
        
        try {
            // Generate unique file name
            String originalFilename = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            // Include the prefix in the file path
            String uniqueFileName = prefix + fileType + "/" + courseId + "/" + timestamp + "_" + UUID.randomUUID() + "." + extension;
            
            // Upload to S3
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .contentType(file.getContentType())
                    .build();
            
            s3Client.putObject(request, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            // Construct file URL
            String fileUrl = "https://" + bucketName + ".s3.amazonaws.com/" + uniqueFileName;
            
            log.info("FileUploadService: uploadFile: Successfully uploaded file to S3 bucket: {}, key: {}, URL: {}", 
                     bucketName, uniqueFileName, fileUrl);
            
            return FileUploadResponseDto.builder()
                    .fileName(originalFilename)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .fileUrl(fileUrl)
                    .success(true)
                    .message("File uploaded successfully")
                    .build();
            
        } catch (IOException e) {
            log.error("FileUploadService: uploadFile: IO error while uploading file", e);
            return FileUploadResponseDto.builder()
                    .success(false)
                    .message("Failed to read file: " + e.getMessage())
                    .build();
        } catch (S3Exception e) {
            log.error("FileUploadService: uploadFile: S3 error while uploading file", e);
            return FileUploadResponseDto.builder()
                    .success(false)
                    .message("S3 upload failed: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("FileUploadService: uploadFile: Unexpected error during file upload", e);
            return FileUploadResponseDto.builder()
                    .success(false)
                    .message("Upload failed: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * For dev environments where S3 is not enabled, simulate a file upload
     */
    private FileUploadResponseDto simulateFileUpload(MultipartFile file, UUID courseId, String fileType) {
        if (!validateFile(file, fileType)) {
            return FileUploadResponseDto.builder()
                    .success(false)
                    .message("File validation failed. Check file type and size.")
                    .build();
        }
        
        // Create mock URL
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String mockUrl = "https://mock-s3-url.com/" + fileType + "/" + courseId + "/" + 
                timestamp + "_" + UUID.randomUUID() + "." + 
                FilenameUtils.getExtension(file.getOriginalFilename());
        
        return FileUploadResponseDto.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .fileUrl(mockUrl)
                .success(true)
                .message("File upload simulated (S3 disabled)")
                .build();
    }

    @Override
    public boolean validateFile(MultipartFile file, String fileType) {
        if (file.isEmpty()) {
            log.error("FileUploadService: validateFile: File is empty");
            return false;
        }
        
        // Check file size
        if (file.getSize() > maxFileSize) {
            log.error("FileUploadService: validateFile: File size {} exceeds maximum allowed size {}", 
                    file.getSize(), maxFileSize);
            return false;
        }
        
        String contentType = file.getContentType();
        if (contentType == null) {
            log.error("FileUploadService: validateFile: Content type is null");
            return false;
        }
        
        // Check file type based on upload category
        boolean isValidType = switch (fileType.toLowerCase()) {
            case "banner", "thumbnail" -> isAllowedImageType(contentType);
            case "promovideo" -> isAllowedVideoType(contentType);
            case "certificate" -> isAllowedDocumentType(contentType);
            // Support module content types
            case "pdf" -> isAllowedDocumentType(contentType);
            case "video" -> isAllowedVideoType(contentType);
            case "image" -> isAllowedImageType(contentType);
            default -> false;
        };
        
        if (!isValidType) {
            log.error("FileUploadService: validateFile: Invalid content type {} for file type {}", 
                    contentType, fileType);
        }
        
        return isValidType;
    }

    private boolean isAllowedImageType(String contentType) {
        List<String> allowedTypes = Arrays.asList(allowedImageTypes.split(","));
        return allowedTypes.contains(contentType.toLowerCase());
    }
    
    private boolean isAllowedVideoType(String contentType) {
        List<String> allowedTypes = Arrays.asList(allowedVideoTypes.split(","));
        return allowedTypes.contains(contentType.toLowerCase());
    }
    
    private boolean isAllowedDocumentType(String contentType) {
        List<String> allowedTypes = Arrays.asList(allowedDocumentTypes.split(","));
        return allowedTypes.contains(contentType.toLowerCase());
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        if (!s3Enabled) {
            log.warn("FileUploadService: deleteFile: S3 is not enabled. File deletion is simulated.");
            return true;
        }
        
        try {
            // Extract key from URL
            URI uri = new URI(fileUrl);
            String path = uri.getPath();
            Path keyPath = Paths.get(path);
            String key = keyPath.getFileName().toString();
            
            // Delete from S3
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            
            s3Client.deleteObject(request);
            log.info("FileUploadService: deleteFile: Successfully deleted file from S3. URL: {}", fileUrl);
            
            return true;
        } catch (URISyntaxException e) {
            log.error("FileUploadService: deleteFile: Invalid URL format", e);
            return false;
        } catch (S3Exception e) {
            log.error("FileUploadService: deleteFile: S3 error while deleting file", e);
            return false;
        } catch (Exception e) {
            log.error("FileUploadService: deleteFile: Unexpected error during file deletion", e);
            return false;
        }
    }
}