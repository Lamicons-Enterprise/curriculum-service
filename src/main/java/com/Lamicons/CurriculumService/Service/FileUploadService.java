package com.Lamicons.CurriculumService.Service;

import com.Lamicons.CurriculumService.DTO.File.FileUploadResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Service interface for file upload operations.
 */
public interface FileUploadService {
    
    /**
     * Uploads a file to S3 storage with validation.
     *
     * @param file The file to be uploaded
     * @param courseId Associated course ID
     * @param fileType Type of file (banner, thumbnail, promoVideo, certificate)
     * @return FileUploadResponseDto containing upload status and file URL
     */
    FileUploadResponseDto uploadFile(MultipartFile file, UUID courseId, String fileType);
    
    /**
     * Validates if a file meets the requirements for upload.
     *
     * @param file The file to be validated
     * @param fileType Type of file (banner, thumbnail, promoVideo, certificate)
     * @return True if valid, otherwise false
     */
    boolean validateFile(MultipartFile file, String fileType);
    
    /**
     * Deletes a file from S3 storage.
     *
     * @param fileUrl The URL of the file to be deleted
     * @return True if deleted successfully, otherwise false
     */
    boolean deleteFile(String fileUrl);
}