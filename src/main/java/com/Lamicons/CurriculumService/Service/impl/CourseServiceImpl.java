package com.Lamicons.CurriculumService.Service.impl;

import com.Lamicons.CurriculumService.DTO.Assessment.AssessmentResponseDto;
import com.Lamicons.CurriculumService.DTO.Course.*;
import com.Lamicons.CurriculumService.DTO.Module.ModuleSummaryDto;
import com.Lamicons.CurriculumService.Entity.Course;
import com.Lamicons.CurriculumService.Entity.JunctionTable.AssessmentQuestion;
import com.Lamicons.CurriculumService.Entity.JunctionTable.CourseModule;
import com.Lamicons.CurriculumService.Entity.JunctionTable.ModuleAssessment;
import com.Lamicons.CurriculumService.Repository.*;
import com.Lamicons.CurriculumService.Service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {
    
    private final CourseRepository courseRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final ModuleAssessmentRepository moduleAssessmentRepository;
    private final AssessmentQuestionRepository assessmentQuestionRepository;

    @Override
    @Transactional
    public CourseResponseDto createCourse(CourseCreateRequestDto request) {
        log.info("Creating new course: {}", request.getName());
        
        Course course = Course.builder()
                .name(request.getName())
                .description(request.getDescription())
                .shortDescription(request.getShortDescription())
                .level(request.getLevel())
                .category(request.getCategory())
                .bannerUrl(request.getBannerUrl())
                .thumbnailUrl(request.getThumbnailUrl())
                .promoVideoUrl(request.getPromoVideoUrl())
                .targetAudience(request.getTargetAudience())
                .durationWeeks(request.getDurationWeeks())
                .status(CourseStatus.DRAFT)
                .visibility(CourseVisibility.DRAFT)
                .isComplete(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        
        Course savedCourse = courseRepository.save(course);
        log.info("Course created successfully with ID: {}", savedCourse.getId());
        return mapToResponseDto(savedCourse);
    }
    
    @Override
    @Transactional
    public CourseResponseDto updateCourse(UUID courseId, CourseCreateRequestDto request) {
        log.info("Updating course: {}", courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setShortDescription(request.getShortDescription());
        course.setLevel(request.getLevel());
        course.setCategory(request.getCategory());
        course.setBannerUrl(request.getBannerUrl());
        course.setThumbnailUrl(request.getThumbnailUrl());
        course.setPromoVideoUrl(request.getPromoVideoUrl());
        course.setTargetAudience(request.getTargetAudience());
        course.setDurationWeeks(request.getDurationWeeks());
        course.setUpdatedAt(Instant.now());
        
        Course updatedCourse = courseRepository.save(course);
        log.info("Course updated successfully: {}", courseId);
        return mapToResponseDto(updatedCourse);
    }
    
    @Override
    @Transactional
    public void deleteCourse(UUID courseId) {
        log.info("Deleting course: {}", courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        
        List<CourseModule> courseModules = courseModuleRepository.findByCourseIdOrderByModuleOrder(courseId);
        courseModuleRepository.deleteAll(courseModules);
        courseRepository.delete(course);
        log.info("Course deleted successfully: {}", courseId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CourseResponseDto getCourseById(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        return mapToResponseDto(course);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDto> getAllCourses(CourseStatus status) {
        List<Course> courses;
        if (status != null) {
            courses = courseRepository.findAll().stream()
                    .filter(c -> c.getStatus() == status)
                    .collect(Collectors.toList());
        } else {
            courses = courseRepository.findAll();
        }
        return courses.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CourseResponseDto updateCourseFileUrl(UUID courseId, String fileType, String fileUrl) {
        log.info("Updating {} for course: {}", fileType, courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        
        switch (fileType.toLowerCase()) {
            case "banner": course.setBannerUrl(fileUrl); break;
            case "thumbnail": course.setThumbnailUrl(fileUrl); break;
            case "promo":
            case "promo_video": course.setPromoVideoUrl(fileUrl); break;
            case "certificate": course.setCertificateUrl(fileUrl); break;
            default: throw new RuntimeException("Invalid file type: " + fileType);
        }
        
        course.setUpdatedAt(Instant.now());
        Course updated = courseRepository.save(course);
        log.info("Course file URL updated successfully");
        return mapToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseHierarchyResponseDto getCourseHierarchy(UUID courseId) {
        log.info("Fetching course hierarchy for: {}", courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<CourseModule> courseModules = courseModuleRepository.findByCourseIdOrderByModuleOrder(courseId);
        int totalAssessments = 0;
        int totalQuestions = 0;
        List<ModuleSummaryDto> moduleSummaries = new ArrayList<>();

        for (CourseModule cm : courseModules) {
            List<ModuleAssessment> moduleAssessments = moduleAssessmentRepository
                    .findByModuleIdOrderByAssessmentOrder(cm.getModule().getId());
            List<AssessmentResponseDto> assessmentSummaries = new ArrayList<>();

            for (ModuleAssessment ma : moduleAssessments) {
                List<AssessmentQuestion> assessmentQuestions = assessmentQuestionRepository
                        .findByAssessmentIdOrderByOrderNumber(ma.getAssessment().getId());
                int questionCount = assessmentQuestions.size();
                totalQuestions += questionCount;

                assessmentSummaries.add(AssessmentResponseDto.builder()
                        .assessmentId(ma.getAssessment().getId())
                        .title(ma.getAssessment().getTitle())
                        .description(ma.getAssessment().getDescription())
                        .type(ma.getAssessment().getType())
                        .totalMarks(ma.getAssessment().getTotalMarks())
                        .passMarks(ma.getAssessment().getPassMarks())
                        .durationMinutes(ma.getAssessment().getDurationMinutes())
                        .assessmentOrder(ma.getAssessmentOrder())
                        .totalQuestions(questionCount)
                        .isActive(ma.getIsActive())
                        .build());
            }

            totalAssessments += assessmentSummaries.size();
            moduleSummaries.add(ModuleSummaryDto.builder()
                    .moduleId(cm.getModule().getId())
                    .title(cm.getModule().getTitle())
                    .description(cm.getModule().getDescription())
                    .moduleOrder(cm.getModuleOrder())
                    .totalAssessments(assessmentSummaries.size())
                    .totalQuestions(assessmentSummaries.stream().mapToInt(AssessmentResponseDto::getTotalQuestions).sum())
                    .isActive(cm.getIsActive())
                    .assessments(assessmentSummaries)
                    .build());
        }

        return CourseHierarchyResponseDto.builder()
                .courseId(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .status(course.getStatus())
                .level(course.getLevel())
                .category(course.getCategory())
                .isComplete(course.getIsComplete())
                .totalModules(moduleSummaries.size())
                .totalAssessments(totalAssessments)
                .totalQuestions(totalQuestions)
                .createdAt(course.getCreatedAt())
                .publishedAt(course.getPublishedAt())
                .modules(moduleSummaries)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isCourseComplete(UUID courseId) {
        Long moduleCount = courseModuleRepository.countByCourseId(courseId);
        if (moduleCount == 0) return false;

        List<CourseModule> courseModules = courseModuleRepository.findByCourseIdOrderByModuleOrder(courseId);
        for (CourseModule cm : courseModules) {
            Long assessmentCount = moduleAssessmentRepository.countByModuleId(cm.getModule().getId());
            if (assessmentCount == 0) return false;

            List<ModuleAssessment> assessments = moduleAssessmentRepository
                    .findByModuleIdOrderByAssessmentOrder(cm.getModule().getId());
            for (ModuleAssessment ma : assessments) {
                Long questionCount = assessmentQuestionRepository.countByAssessmentId(ma.getAssessment().getId());
                if (questionCount == 0) return false;
            }
        }
        return true;
    }
    
    @Override
    @Transactional
    public CourseResponseDto publishCourse(UUID courseId) {
        log.info("Publishing course: {}", courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setStatus(CourseStatus.PUBLISHED);
        course.setVisibility(CourseVisibility.PUBLIC);
        course.setPublishedAt(Instant.now());
        course.setUpdatedAt(Instant.now());
        course.setIsComplete(true);

        Course published = courseRepository.save(course);
        log.info("Course published successfully: {}", courseId);
        return mapToResponseDto(published);
    }

    @Override
    @Transactional
    public CourseResponseDto unpublishCourse(UUID courseId) {
        log.info("Unpublishing course: {}", courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setStatus(CourseStatus.DRAFT);
        course.setVisibility(CourseVisibility.DRAFT);
        course.setUpdatedAt(Instant.now());

        Course unpublished = courseRepository.save(course);
        log.info("Course unpublished successfully: {}", courseId);
        return mapToResponseDto(unpublished);
    }

    @Override
    @Transactional
    public CourseResponseDto archiveCourse(UUID courseId) {
        log.info("Archiving course: {}", courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setStatus(CourseStatus.ARCHIVED);
        course.setVisibility(CourseVisibility.HIDDEN);
        course.setUpdatedAt(Instant.now());

        Course archived = courseRepository.save(course);
        log.info("Course archived successfully: {}", courseId);
        return mapToResponseDto(archived);
    }

    @Override
    @Transactional
    public CourseResponseDto updateCourseStatus(UUID courseId, CourseStatus status) {
        log.info("Updating course status to: {}", status);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        course.setStatus(status);
        course.setUpdatedAt(Instant.now());
        Course updated = courseRepository.save(course);
        log.info("Course status updated successfully");
        return mapToResponseDto(updated);
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private CourseResponseDto mapToResponseDto(Course course) {
        List<String> outcomesList = null;

        
        return CourseResponseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .shortDescription(course.getShortDescription())
                .level(course.getLevel())
                .category(course.getCategory())
                .bannerUrl(course.getBannerUrl())
                .thumbnailUrl(course.getThumbnailUrl())
                .promoVideoUrl(course.getPromoVideoUrl())
                .certificateUrl(course.getCertificateUrl())
                .rating(course.getRating())
                .ratingCount(course.getRatingCount())
                .enrollmentCount(course.getEnrollmentCount())
                .visibility(course.getVisibility())
                .targetAudience(course.getTargetAudience())
                .durationWeeks(course.getDurationWeeks())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}
