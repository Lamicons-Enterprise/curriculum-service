package com.Lamicons.CurriculumService.Service.impl;

import com.Lamicons.CurriculumService.DTO.Course.CourseResponseDto;
import com.Lamicons.CurriculumService.DTO.Module.*;
import com.Lamicons.CurriculumService.Entity.Course;
import com.Lamicons.CurriculumService.Entity.Module;
import com.Lamicons.CurriculumService.Entity.JunctionTable.CourseModule;
import com.Lamicons.CurriculumService.Repository.CourseRepository;
import com.Lamicons.CurriculumService.Repository.ModuleRepository;
import com.Lamicons.CurriculumService.Repository.CourseModuleRepository;
import com.Lamicons.CurriculumService.Service.CourseService;
import com.Lamicons.CurriculumService.Service.ModuleService;
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
public class ModuleServiceImpl implements ModuleService {
    
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final CourseService courseService;

    @Override
    @Transactional
    public ModuleSummaryDto createStandaloneModule(ModuleAttachmentRequestDto.NewModuleDto request) {
        log.info("Creating standalone module: {}", request.getTitle());
        
        Module module = Module.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .order(request.getOrder() != null ? request.getOrder() : 0)
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        
        Module savedModule = moduleRepository.save(module);
        log.info("Standalone module created successfully with ID: {}", savedModule.getId());
        
        return mapToSummaryDto(savedModule);
    }

    @Override
    @Transactional
    public ModuleSummaryDto updateModule(UUID moduleId, ModuleAttachmentRequestDto.NewModuleDto request) {
        log.info("Updating module: {}", moduleId);
        
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found with ID: " + moduleId));
        
        module.setTitle(request.getTitle());
        module.setDescription(request.getDescription());
        if (request.getOrder() != null) {
            module.setOrder(request.getOrder());
        }
        module.setUpdatedAt(Instant.now());
        
        Module updatedModule = moduleRepository.save(module);
        log.info("Module updated successfully: {}", moduleId);
        
        return mapToSummaryDto(updatedModule);
    }

    @Override
    @Transactional(readOnly = true)
    public ModuleSummaryDto getModuleById(UUID moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found with ID: " + moduleId));
        return mapToSummaryDto(module);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModuleSummaryDto> getAllReusableModules() {
        log.info("Fetching all reusable modules");
        List<Module> modules = moduleRepository.findAll().stream()
                .filter(Module::getIsActive)
                .collect(Collectors.toList());
        
        return modules.stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModuleSummaryDto> searchModules(String searchTerm) {
        log.info("Searching modules with term: {}", searchTerm);
        List<Module> modules = moduleRepository.findAll().stream()
                .filter(Module::getIsActive)
                .filter(m -> m.getTitle().toLowerCase().contains(searchTerm.toLowerCase()) ||
                            (m.getDescription() != null && m.getDescription().toLowerCase().contains(searchTerm.toLowerCase())))
                .collect(Collectors.toList());
        
        return modules.stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModuleSummaryDto> getModulesByCourseId(UUID courseId) {
        log.info("Fetching modules for course: {}", courseId);
        List<CourseModule> courseModules = courseModuleRepository
                .findByCourseIdOrderByModuleOrder(courseId);
        
        return courseModules.stream()
                .map(cm -> mapToSummaryDto(cm.getModule()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteModule(UUID moduleId) {
        log.info("Deleting module: {}", moduleId);
        
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found with ID: " + moduleId));
        
        // Soft delete
        module.setIsActive(false);
        module.setUpdatedAt(Instant.now());
        moduleRepository.save(module);
        
        log.info("Module soft-deleted successfully: {}", moduleId);
    }

    // ==================== COURSE-MODULE RELATIONSHIP ====================

    @Override
    @Transactional
    public CourseResponseDto attachModulesToCourse(ModuleAttachmentRequestDto request) {
        log.info("Attaching modules to course: {}", request.getCourseId());
        
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + request.getCourseId()));
        
        // Get current max order
        int currentMaxOrder = courseModuleRepository.findByCourseIdOrderByModuleOrder(request.getCourseId())
                .stream()
                .mapToInt(CourseModule::getModuleOrder)
                .max()
                .orElse(-1);
        
        int nextOrder = currentMaxOrder + 1;
        
        // Attach existing modules
        if (request.getExistingModuleIds() != null && !request.getExistingModuleIds().isEmpty()) {
            for (UUID moduleId : request.getExistingModuleIds()) {
                Module module = moduleRepository.findById(moduleId)
                        .orElseThrow(() -> new RuntimeException("Module not found: " + moduleId));
                
                // Check if already attached
                Optional<CourseModule> existing = courseModuleRepository
                        .findByCourseIdAndModuleId(course.getId(), moduleId);
                
                if (existing.isEmpty()) {
                    CourseModule courseModule = CourseModule.builder()
                            .course(course)
                            .module(module)
                            .moduleOrder(nextOrder++)
                            .addedAt(Instant.now())
                            .isActive(true)
                            .build();
                    courseModuleRepository.save(courseModule);
                    log.info("Attached existing module {} to course {}", moduleId, course.getId());
                } else {
                    log.warn("Module {} already attached to course {}", moduleId, course.getId());
                }
            }
        }
        
        // Create and attach new modules
        if (request.getNewModules() != null && !request.getNewModules().isEmpty()) {
            for (ModuleAttachmentRequestDto.NewModuleDto newModuleDto : request.getNewModules()) {
                Module module = Module.builder()
                        .title(newModuleDto.getTitle())
                        .description(newModuleDto.getDescription())
                        .order(newModuleDto.getOrder() != null ? newModuleDto.getOrder() : 0)
                        .isActive(true)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();
                
                Module savedModule = moduleRepository.save(module);
                
                CourseModule courseModule = CourseModule.builder()
                        .course(course)
                        .module(savedModule)
                        .moduleOrder(nextOrder++)
                        .addedAt(Instant.now())
                        .isActive(true)
                        .build();
                courseModuleRepository.save(courseModule);
                log.info("Created and attached new module {} to course {}", savedModule.getId(), course.getId());
            }
        }

        // Return updated course
        return courseService.getCourseById(course.getId());
    }

    @Override
    @Transactional
    public void removeModuleFromCourse(UUID courseId, UUID moduleId) {
        log.info("Removing module {} from course {}", moduleId, courseId);
        
        CourseModule courseModule = courseModuleRepository
                .findByCourseIdAndModuleId(courseId, moduleId)
                .orElseThrow(() -> new RuntimeException("Module not attached to this course"));
        
        // Soft delete the relationship
        courseModule.setIsActive(false);
        courseModuleRepository.save(courseModule);
        
        log.info("Module removed successfully from course");
    }

    @Override
    @Transactional
    public CourseResponseDto reorderModules(UUID courseId, List<UUID> moduleIdsInOrder) {
        log.info("Reordering modules for course: {}", courseId);

        List<CourseModule> courseModules = courseModuleRepository.findByCourseIdOrderByModuleOrder(courseId);
        Map<UUID, CourseModule> moduleMap = courseModules.stream()
                .collect(Collectors.toMap(cm -> cm.getModule().getId(), cm -> cm));

        for (int i = 0; i < moduleIdsInOrder.size(); i++) {
            UUID moduleId = moduleIdsInOrder.get(i);
            CourseModule cm = moduleMap.get(moduleId);
            if (cm != null) {
                cm.setModuleOrder(i);
                courseModuleRepository.save(cm);
            } else {
                log.warn("Module {} not found in course {}", moduleId, courseId);
            }
        }

        log.info("Modules reordered successfully for course: {}", courseId);
        return courseService.getCourseById(courseId);
    }

    // ==================== HELPER METHODS ====================

    private ModuleSummaryDto mapToSummaryDto(Module module) {
        return ModuleSummaryDto.builder()
                .moduleId(module.getId())
                .title(module.getTitle())
                .description(module.getDescription())
                .moduleOrder(module.getOrder())
                .isActive(module.getIsActive())
                .totalAssessments(0) // Will be populated when needed
                .totalQuestions(0)    // Will be populated when needed
                .build();
    }
}
