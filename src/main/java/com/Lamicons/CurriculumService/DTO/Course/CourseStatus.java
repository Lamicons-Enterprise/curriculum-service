package com.Lamicons.CurriculumService.DTO.Course;

/**
 * Represents the workflow status of a course in the admin course builder flow
 * Follows the Coursera/Udemy model of draft -> review -> publish lifecycle
 */
public enum CourseStatus {
    /**
     * Course is being created/edited, not visible to learners
     * Admin can add/remove modules, assignments, questions freely
     */
    DRAFT,
    
    /**
     * Course structure is complete, pending final review
     * Admin can review entire hierarchy before publishing
     */
    IN_REVIEW,
    
    /**
     * Course is live and visible to learners for enrollment
     * Changes should create new versions
     */
    PUBLISHED,
    
    /**
     * Course is no longer active but data is retained
     * Not visible to new learners, existing enrollments may continue
     */
    ARCHIVED
}
