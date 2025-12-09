package com.Lamicons.CurriculumService.Service.impl;

import com.Lamicons.CurriculumService.DTO.Assessment.*;
import com.Lamicons.CurriculumService.DTO.Question.QuestionSummaryDto;
import com.Lamicons.CurriculumService.Entity.Assessment;
import com.Lamicons.CurriculumService.Entity.JunctionTable.AssessmentQuestion;
import com.Lamicons.CurriculumService.Entity.JunctionTable.ModuleAssessment;
import com.Lamicons.CurriculumService.Entity.Module;
import com.Lamicons.CurriculumService.Repository.*;
import com.Lamicons.CurriculumService.Service.AssessmentService;
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
public class AssessmentServiceImpl implements AssessmentService {
    
    private final AssessmentRepository assessmentRepository;
    private final ModuleRepository moduleRepository;
    private final ModuleAssessmentRepository moduleAssessmentRepository;
    private final AssessmentQuestionRepository assessmentQuestionRepository;

    
    @Override
    @Transactional
    public AssessmentResponseDto createStandaloneAssessment(AssessmentAttachmentRequestDto.NewAssessmentDto request) {
        log.info("Creating standalone assessment: {}", request.getTitle());
        
        Assessment assessment = Assessment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .totalMarks(request.getTotalMarks() != null ? request.getTotalMarks() : 0)
                .passMarks(request.getPassMarks() != null ? request.getPassMarks() : 0)
                .durationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 0)
                .order(request.getOrder() != null ? request.getOrder() : 0)
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        
        Assessment saved = assessmentRepository.save(assessment);
        log.info("Assessment created successfully with ID: {}", saved.getId());
        
        return mapToSummaryDto(saved);
    }
    
    @Override
    @Transactional
    public AssessmentResponseDto updateAssessment(UUID assessmentId, AssessmentAttachmentRequestDto.NewAssessmentDto request) {
        log.info("Updating assessment: {}", assessmentId);
        
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found with ID: " + assessmentId));

        assessment.setTitle(request.getTitle());
        assessment.setDescription(request.getDescription());
        assessment.setType(request.getType());
        assessment.setTotalMarks(request.getTotalMarks() != null ? request.getTotalMarks() : 0);
        assessment.setPassMarks(request.getPassMarks() != null ? request.getPassMarks() : 0);
        assessment.setDurationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 0);
        assessment.setUpdatedAt(Instant.now());
        
        Assessment updated = assessmentRepository.save(assessment);
        log.info("Assessment updated successfully: {}", assessmentId);
        
        return mapToSummaryDto(updated);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AssessmentResponseDto getAssessmentById(UUID assessmentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found with ID: " + assessmentId));
        
        return mapToSummaryDtoWithQuestions(assessment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AssessmentResponseDto> getAssessmentsByModuleId(UUID moduleId) {
        List<ModuleAssessment> moduleAssessments = moduleAssessmentRepository
                .findByModuleIdOrderByAssessmentOrder(moduleId);
        
        return moduleAssessments.stream()
                .map(ma -> {
                    AssessmentResponseDto dto = mapToSummaryDto(ma.getAssessment());
                    dto.setAssessmentOrder(ma.getAssessmentOrder());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void attachAssessmentsToModule(AssessmentAttachmentRequestDto request) {
        log.info("Attaching assessments to module: {}", request.getModuleId());
        
        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module not found"));
        
        int currentMaxOrder = moduleAssessmentRepository.findByModuleIdOrderByAssessmentOrder(request.getModuleId())
                .stream()
                .mapToInt(ModuleAssessment::getAssessmentOrder)
                .max()
                .orElse(-1);
        
        int nextOrder = currentMaxOrder + 1;
        
        //  Attach existing assessments
        if (request.getExistingAssessmentIds() != null && !request.getExistingAssessmentIds().isEmpty()) {
            for (UUID assessmentId : request.getExistingAssessmentIds()) {
                Assessment assessment = assessmentRepository.findById(assessmentId)
                        .orElseThrow(() -> new RuntimeException("Assessment not found: " + assessmentId));
                
                // Check if already attached
                Optional<ModuleAssessment> existing = moduleAssessmentRepository
                        .findByModuleIdAndAssessmentId(module.getId(), assessmentId);
                
                if (existing.isEmpty()) {
                    ModuleAssessment moduleAssessment = ModuleAssessment.builder()
                            .module(module)
                            .assessment(assessment)
                            .assessmentOrder(nextOrder++)
                            .addedAt(Instant.now())
                            .isActive(true)
                            .build();
                    moduleAssessmentRepository.save(moduleAssessment);
                    log.info("Attached existing assessment {} to module {}", assessmentId, module.getId());
                }
            }
        }
        
        //  Create and attach new assessments
        if (request.getNewAssessments() != null && !request.getNewAssessments().isEmpty()) {
            for (AssessmentAttachmentRequestDto.NewAssessmentDto newAssessmentDto : request.getNewAssessments()) {
                Assessment assessment = Assessment.builder()
                        .title(newAssessmentDto.getTitle())
                        .description(newAssessmentDto.getDescription())
                        .type(newAssessmentDto.getType())
                        .totalMarks(newAssessmentDto.getTotalMarks() != null ? newAssessmentDto.getTotalMarks() : 0)
                        .passMarks(newAssessmentDto.getPassMarks() != null ? newAssessmentDto.getPassMarks() : 0)
                        .durationMinutes(newAssessmentDto.getDurationMinutes() != null ? newAssessmentDto.getDurationMinutes() : 0)
                        .order(newAssessmentDto.getOrder() != null ? newAssessmentDto.getOrder() : 0)
                        .isActive(true)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();
                
                Assessment savedAssessment = assessmentRepository.save(assessment);
                
                ModuleAssessment moduleAssessment = ModuleAssessment.builder()
                        .module(module)
                        .assessment(savedAssessment)
                        .assessmentOrder(nextOrder++)
                        .addedAt(Instant.now())
                        .isActive(true)
                        .build();
                moduleAssessmentRepository.save(moduleAssessment);
                log.info("Created and attached new assessment {} to module {}", savedAssessment.getId(), module.getId());
            }
        }
        
        log.info("Successfully attached assessments to module {}", request.getModuleId());
    }
    
    @Override
    @Transactional
    public void removeAssessmentFromModule(UUID moduleId, UUID assessmentId) {
        log.info("Removing assessment {} from module {}", assessmentId, moduleId);
        
        ModuleAssessment moduleAssessment = moduleAssessmentRepository
                .findByModuleIdAndAssessmentId(moduleId, assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not attached to this module"));
        
        moduleAssessment.setIsActive(false);
        moduleAssessmentRepository.save(moduleAssessment);
        
        log.info("Assessment removed successfully");
    }

//    FUTURE FEATURE: Reordering assessments within a module

//
//    @Override
//    @Transactional
//    public void reorderAssessments(UUID moduleId, List<UUID> assessmentIdsInOrder) {
//        log.info("Reordering assessments for module: {}", moduleId);
//
//        List<ModuleAssessment> moduleAssessments = moduleAssessmentRepository
//                .findByModuleIdOrderByAssessmentOrder(moduleId);
//        Map<UUID, ModuleAssessment> assessmentMap = moduleAssessments.stream()
//                .collect(Collectors.toMap(ma -> ma.getAssessment().getId(), ma -> ma));
//
//        for (int i = 0; i < assessmentIdsInOrder.size(); i++) {
//            UUID assessmentId = assessmentIdsInOrder.get(i);
//            ModuleAssessment ma = assessmentMap.get(assessmentId);
//            if (ma != null) {
//                ma.setAssessmentOrder(i);
//                moduleAssessmentRepository.save(ma);
//            }
//        }
//
//        log.info("Assessments reordered successfully");
//    }
    
    @Override
    @Transactional
    public void deleteAssessment(UUID assessmentId) {
        log.info("Deleting assessment: {}", assessmentId);
        
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));
        
        // Check if used in any modules
        List<ModuleAssessment> moduleAssessments = moduleAssessmentRepository.findByAssessmentId(assessmentId);
        if (!moduleAssessments.isEmpty()) {
            throw new RuntimeException("Cannot delete assessment - it is being used in " + 
                    moduleAssessments.size() + " module(s)");
        }
        
        assessment.setIsActive(false);
        assessmentRepository.save(assessment);
        
        log.info("Assessment deleted successfully");
    }
    
    private AssessmentResponseDto mapToSummaryDto(Assessment assessment) {
        Long questionCount = assessmentQuestionRepository.countByAssessmentId(assessment.getId());
        
        return AssessmentResponseDto.builder()
                .assessmentId(assessment.getId())
                .title(assessment.getTitle())
                .description(assessment.getDescription())
                .type(assessment.getType())
                .totalMarks(assessment.getTotalMarks())
                .passMarks(assessment.getPassMarks())
                .durationMinutes(assessment.getDurationMinutes())
                .totalQuestions(questionCount.intValue())
                .isActive(assessment.getIsActive())
                .questions(new ArrayList<>()) // Empty by default
                .build();
    }
    
    private AssessmentResponseDto mapToSummaryDtoWithQuestions(Assessment assessment) {
        List<AssessmentQuestion> assessmentQuestions = assessmentQuestionRepository
                .findByAssessmentIdOrderByOrderNumber(assessment.getId());
        
        List<QuestionSummaryDto> questionSummaries = assessmentQuestions.stream()
                .map(aq -> {
                    QuestionSummaryDto.QuestionSummaryDtoBuilder builder = QuestionSummaryDto.builder()
                            .questionId(aq.getQuestion().getId())
                            .type(aq.getQuestion().getType())
                            .title(aq.getQuestion().getTitle())
                            .description(aq.getQuestion().getDescription())
                            .topic(aq.getQuestion().getTopic())
                            .score(aq.getQuestion().getScore())
                            .negativeScore(aq.getQuestion().getNegativeScore())
                            .orderNumber(aq.getOrderNumber());
                    
                    // Add MCQ-specific fields
                    if (aq.getQuestion() instanceof com.Lamicons.CurriculumService.Entity.Question.McqQuestion) {
                        com.Lamicons.CurriculumService.Entity.Question.McqQuestion mcq = 
                            (com.Lamicons.CurriculumService.Entity.Question.McqQuestion) aq.getQuestion();
                        builder.options(mcq.getOptions())
                               .correctOption(mcq.getCorrectOption());
                    }
                    
                    // Add Coding-specific fields
                    if (aq.getQuestion() instanceof com.Lamicons.CurriculumService.Entity.Question.CodingQuestion) {
                        com.Lamicons.CurriculumService.Entity.Question.CodingQuestion coding = 
                            (com.Lamicons.CurriculumService.Entity.Question.CodingQuestion) aq.getQuestion();
                        builder.testcases(coding.getTestcases());
                    }
                    
                    return builder.build();
                })
                .collect(Collectors.toList());
        
        return AssessmentResponseDto.builder()
                .assessmentId(assessment.getId())
                .title(assessment.getTitle())
                .description(assessment.getDescription())
                .type(assessment.getType())
                .totalMarks(assessment.getTotalMarks())
                .passMarks(assessment.getPassMarks())
                .durationMinutes(assessment.getDurationMinutes())
                .totalQuestions(questionSummaries.size())
                .isActive(assessment.getIsActive())
                .questions(questionSummaries)
                .build();
    }
}
