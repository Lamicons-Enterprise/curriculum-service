package com.Lamicons.CurriculumService.DTO.Assessment;

public enum AssessmentType {
   PRACTICE_TEST, // For self-assessment and practice purposes, often with immediate feedback and no impact on final grades. (Multiple attempts allowed, Synchronous Processing)
    COURSE_TEST, // For formal evaluation at the end of a course module, contributing to final grades. (Single attempt,asynchronous processing )
    ASSIGNMENT, // For project-based evaluation, often requiring submission of work and manual grading. (Single attempt, synchronous processing)
    QUALIFICATION_EXAM // For high-stakes evaluation, often used for certification or qualification purposes, with strict grading criteria. (Single attempt, asynchronous processing)
}
