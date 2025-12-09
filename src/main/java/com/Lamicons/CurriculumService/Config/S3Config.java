package com.Lamicons.CurriculumService.Config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Getter
@Configuration
public class S3Config {

    @Value("${aws.s3.access-key:placeholder}")
    private String accessKey;

    @Value("${aws.s3.secret-key:placeholder}")
    private String secretKey;

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    @Value("${aws.s3.bucket-name:lamicons-course-assets}")
    private String bucketName;
    
    @Value("${aws.s3.prefix:}")
    private String prefix;

    @Value("${aws.s3.enabled:false}")
    private boolean s3Enabled;

    /**
     * Creates an S3 client bean for interacting with AWS S3 service.
     *
     * @return S3Client instance configured with provided credentials
     */
    @Bean
    public S3Client s3Client() {
        if (!s3Enabled) {
            return null; // For dev environments where S3 might not be available
        }
        
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(region))
                .build();
    }
    
    /**
     * Creates an S3 presigner for generating presigned URLs for objects.
     * 
     * @return S3Presigner instance
     */
    @Bean
    public S3Presigner s3Presigner() {
        if (!s3Enabled) {
            return null; // For dev environments where S3 might not be available
        }
        
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        
        return S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(region))
                .build();
    }
}