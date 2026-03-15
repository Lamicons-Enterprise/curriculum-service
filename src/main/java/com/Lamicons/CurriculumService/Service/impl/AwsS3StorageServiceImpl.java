package com.Lamicons.CurriculumService.Service.impl;

import com.Lamicons.CurriculumService.Service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Profile("prod")
public class AwsS3StorageServiceImpl implements StorageService {

    private final S3Presigner s3Presigner;
    
    @Value("${aws.s3.bucket-name:default-bucket}")
    private String bucketName;
    
    @Value("${upload.max-file-size:5242880}")
    private long maxFileSize;

    public AwsS3StorageServiceImpl(S3Presigner s3Presigner) {
        this.s3Presigner = s3Presigner;
        log.info("AwsS3StorageServiceImpl initialized to 'prod' profile");
    }

    @Override
    public Map<String, Object> generateUploadSignature(String folderName, String fileName) {
        try {
            // Ensure unique object key if filename is generic
            String objectKey = folderName + "/" + UUID.randomUUID().toString() + "-" + fileName;
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15)) // URL valid for 15 mins
                    .putObjectRequest(putObjectRequest)
                    .build();

            PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(presignRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("uploadUrl", presignedPutObjectRequest.url().toString());
            response.put("fileKey", objectKey); // Save this on client, confirm endpoint to save it
            response.put("method", "PUT");
            response.put("maxFileSize", maxFileSize);

            log.info("Generated S3 presigned URL for key: {}", objectKey);
            return response;
        } catch (Exception e) {
            log.error("Failed to generate S3 presigned URL", e);
            throw new RuntimeException("Failed to generate upload signature");
        }
    }

    @Override
    public String getFileUrl(String fileKey) {
        // Return public URL or another presigned GET URL
        // If public bucket:
        return "https://" + bucketName + ".s3.amazonaws.com/" + fileKey;
    }
}