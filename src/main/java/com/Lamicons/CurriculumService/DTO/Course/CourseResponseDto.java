package com.Lamicons.CurriculumService.DTO.Course;

import lombok.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO for course response data.
 * 
 * Note: Unlike CourseRequestDto, this response includes file URLs (bannerUrl, thumbnailUrl,
 * promoVideoUrl, certificateUrl) to provide the complete state of the course. These URLs
 * are set through the CourseFileController endpoints and are included in responses
 * to show the current state of the course's associated files.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseDto {
    private UUID id;
    private String name;
    private String description;
    private String shortDescription;
    private CourseLevel level;
    private CourseCategory category;
    private String bannerUrl;
    private String thumbnailUrl;
    private String promoVideoUrl;
    private String certificateUrl;
    private Double rating;
    private Integer ratingCount;
    private Integer enrollmentCount;
    private CourseVisibility visibility;
    private String targetAudience;
    private Integer durationWeeks;
    private Instant createdAt;
    private Instant updatedAt;
}
