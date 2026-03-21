package com.Lamicons.CurriculumService.Util;

import com.Lamicons.CurriculumService.DTO.Course.CourseResponseDto;
import com.Lamicons.CurriculumService.DTO.Question.CodingResponseDto;
import com.Lamicons.CurriculumService.DTO.Question.McqResponseDto;
import com.Lamicons.CurriculumService.Entity.Course;
import com.Lamicons.CurriculumService.Entity.Question.CodingQuestion;
import com.Lamicons.CurriculumService.Entity.Question.McqQuestion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class MapperUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    public static CourseResponseDto toCourseResponseDto(Course c) {
        // Convert JSON string to List<String> if outcomes exists

        
        return CourseResponseDto.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .shortDescription(c.getShortDescription())
                .level(c.getLevel())
                .category(c.getCategory())
                .bannerUrl(c.getBannerUrl())
                .thumbnailUrl(c.getThumbnailUrl())
                .promoVideoUrl(c.getPromoVideoUrl())
                .certificateUrl(c.getCertificateUrl())
                .rating(c.getRating())
                .ratingCount(c.getRatingCount())
                .enrollmentCount(c.getEnrollmentCount())
                .visibility(c.getVisibility())
                .targetAudience(c.getTargetAudience())
                .durationWeeks(c.getDurationWeeks())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
    
    public static McqResponseDto toMcqResponseDto(McqQuestion q) {
        return McqResponseDto.builder()
                .id(q.getId())
                .title(q.getTitle())
                .description(q.getDescription())
                .topic(q.getTopic())
                .options(q.getOptions())
                .correctOption(q.getCorrectOption() != null && !q.getCorrectOption().isEmpty() 
                        ? String.join(", ", q.getCorrectOption()) 
                        : null)
                .score(q.getScore())
                .negativeScore(q.getNegativeScore())
                .type(q.getType())
                .createdAt(q.getCreatedAt())
                .updatedAt(q.getUpdatedAt())
                .build();
    }
    
    public static CodingResponseDto toCodingResponseDto(CodingQuestion q) {
        return CodingResponseDto.builder()
                .id(q.getId())
                .title(q.getTitle())
                .description(q.getDescription())
                .topic(q.getTopic())
                .timeLimit(q.getTimeLimit())
                .memoryLimit(q.getMemoryLimit())
                .score(q.getScore())
                .negativeScore(q.getNegativeScore())
                .type(q.getType())
                .createdAt(q.getCreatedAt())
                .updatedAt(q.getUpdatedAt())
                .build();
    }

    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
