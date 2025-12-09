package com.Lamicons.CurriculumService.Service;

import com.Lamicons.CurriculumService.DTO.Course.*;

import java.util.List;
import java.util.UUID;

/**
 * Course Admin Service - SOLID Compliant
 * Single Responsibility: Manages ONLY Course operations
 * Module operations moved to ModuleAdminService
 */
public interface CourseService {
    
    // ==================== COURSE CRUD ====================
    
    /**
     * Create a new course in DRAFT status
     */
    CourseResponseDto createCourse(CourseCreateRequestDto request);

    /**
     * Update course basic information
     */
    CourseResponseDto updateCourse(UUID courseId, CourseCreateRequestDto request);

    /**
     * Get course by ID
     */
    CourseResponseDto getCourseById(UUID courseId);

    /**
     * Get all courses (optionally filtered by status)
     */
    List<CourseResponseDto> getAllCourses(CourseStatus status);
    
    /**
     * Delete course permanently
     */
    void deleteCourse(UUID courseId);
    
    /**
     * Update course file URL (banner, thumbnail, promo video, certificate)
     */
    CourseResponseDto updateCourseFileUrl(UUID courseId, String fileType, String fileUrl);
    
    // ==================== PUBLISHING WORKFLOW ====================
    
    /**
     * Get complete course hierarchy (Course → Modules → Assessments → Questions)
     */
    CourseHierarchyResponseDto getCourseHierarchy(UUID courseId);

    /**
     * Check if course is complete and ready for publishing
     */
    boolean isCourseComplete(UUID courseId);

    /**
     * Publish course (DRAFT → PUBLISHED)
     */
    CourseResponseDto publishCourse(UUID courseId);

    /**
     * Unpublish course (PUBLISHED → DRAFT)
     */
    CourseResponseDto unpublishCourse(UUID courseId);

    /**
     * Archive course (any status → ARCHIVED)
     */
    CourseResponseDto archiveCourse(UUID courseId);

    /**
     * Manually update course status
     */
    CourseResponseDto updateCourseStatus(UUID courseId, CourseStatus status);
}
