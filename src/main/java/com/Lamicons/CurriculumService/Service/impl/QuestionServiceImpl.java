package com.Lamicons.CurriculumService.Service.impl;

import com.Lamicons.CurriculumService.DTO.Question.*;
import com.Lamicons.CurriculumService.Entity.Assessment;
import com.Lamicons.CurriculumService.Entity.JunctionTable.AssessmentQuestion;
import com.Lamicons.CurriculumService.Entity.Question.CodingQuestion;
import com.Lamicons.CurriculumService.Entity.Question.CodingTestCase;
import com.Lamicons.CurriculumService.Entity.Question.McqQuestion;
import com.Lamicons.CurriculumService.Entity.Question.ProblemLanguageConfig;
import com.Lamicons.CurriculumService.Entity.Question.Question;
import com.Lamicons.CurriculumService.Repository.AssessmentQuestionRepository;
import com.Lamicons.CurriculumService.Repository.AssessmentRepository;
import com.Lamicons.CurriculumService.Repository.QuestionRepository;
import com.Lamicons.CurriculumService.Service.QuestionService;
import com.Lamicons.CurriculumService.Util.CsvQuestionParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of Question Admin Service
 * Handles single/bulk question creation, CSV upload, and assessment linking
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    
    private final QuestionRepository questionRepository;
    private final AssessmentRepository assessmentRepository;
    private final AssessmentQuestionRepository assessmentQuestionRepository;
    
    // ===== Single Question Management =====
    
    @Override
    @Transactional
    public QuestionSummaryDto createQuestion(QuestionCreateRequestDto request) {
        log.info("Creating question: {}", request.getTitle());
        
        Question question;
        
        switch (request.getType()) {
            case MCQ:
                McqQuestion mcq = McqQuestion.builder()
                        .type(request.getType())
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .topic(request.getTopic())
                        .score(request.getScore())
                        .negativeScore(request.getNegativeScore())
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();
                
                // Set MCQ specific fields
                if (request.getOptions() != null && !request.getOptions().isEmpty()) {
                    List<String> correctOptions = request.getOptions().stream()
                            .filter(QuestionCreateRequestDto.McqOptionDto::getIsCorrect)
                            .map(QuestionCreateRequestDto.McqOptionDto::getOptionText)
                            .collect(Collectors.toList());
                    mcq.setCorrectOption(correctOptions);
                }
                
                question = questionRepository.save(mcq);
                break;
                
            case CODING:
                CodingQuestion coding = CodingQuestion.builder()
                        .type(request.getType())
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .topic(request.getTopic())
                        .score(request.getScore())
                        .negativeScore(request.getNegativeScore())
                        .timeLimit(request.getTimeLimit() != null ? request.getTimeLimit() : 2000)
                        .memoryLimit(request.getMemoryLimit() != null ? request.getMemoryLimit() : 256)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                question = questionRepository.save(coding);

                // Save test cases
                if (request.getTestCases() != null) {
                    for (CodingTestCaseDto tcDto : request.getTestCases()) {
                        CodingTestCase tc = CodingTestCase.builder()
                                .codingQuestion(coding)
                                .input(tcDto.getInput())
                                .output(tcDto.getOutput())
                                .visibility(tcDto.getVisibility() != null ? tcDto.getVisibility() : TestCaseVisibility.HIDDEN)
                                .orderNumber(tcDto.getOrderNumber() != null ? tcDto.getOrderNumber() : 0)
                                .build();
                        coding.getTestCases().add(tc);
                    }
                }

                // Save language configs
                if (request.getLanguageConfigs() != null) {
                    for (LanguageConfigDto lcDto : request.getLanguageConfigs()) {
                        ProblemLanguageConfig plc = ProblemLanguageConfig.builder()
                                .codingQuestion(coding)
                                .language(lcDto.getLanguage())
                                .boilerplate(lcDto.getBoilerplate())
                                .hiddenCode(lcDto.getHiddenCode())
                                .build();
                        coding.getLanguageConfigs().add(plc);
                    }
                }

                question = questionRepository.save(coding);
                break;
                
            default:
                throw new IllegalArgumentException("Unsupported question type: " + request.getType());
        }
        
        log.info("Question created with ID: {}", question.getId());
        
        // If assessmentId provided, link to assessment
        if (request.getAssessmentId() != null) {
            linkQuestionToAssessment(question.getId(), request.getAssessmentId());
        }
        
        return mapToSummaryDto(question, 0);
    }
    
    @Override
    @Transactional
    public QuestionSummaryDto updateQuestion(UUID questionId, QuestionCreateRequestDto request) {
        log.info("Updating question: {}", questionId);
        
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + questionId));
        
        question.setTitle(request.getTitle());
        question.setDescription(request.getDescription());
        question.setTopic(request.getTopic());
        question.setTags(request.getTags());
        question.setScore(request.getScore());
        question.setNegativeScore(request.getNegativeScore());
        question.setUpdatedAt(Instant.now());
        
        if (question instanceof McqQuestion && request.getType() == QuestionType.MCQ) {
            McqQuestion mcq = (McqQuestion) question;
            if (request.getOptions() != null) {
                List<String> correctOptions = request.getOptions().stream()
                        .filter(QuestionCreateRequestDto.McqOptionDto::getIsCorrect)
                        .map(QuestionCreateRequestDto.McqOptionDto::getOptionText)
                        .collect(Collectors.toList());
                mcq.setCorrectOption(correctOptions);
            }
        } else if (question instanceof CodingQuestion && request.getType() == QuestionType.CODING) {
            CodingQuestion coding = (CodingQuestion) question;
            coding.setTimeLimit(request.getTimeLimit() != null ? request.getTimeLimit() : coding.getTimeLimit());
            coding.setMemoryLimit(request.getMemoryLimit() != null ? request.getMemoryLimit() : coding.getMemoryLimit());

            if (request.getTestCases() != null) {
                coding.getTestCases().clear();
                for (CodingTestCaseDto tcDto : request.getTestCases()) {
                    CodingTestCase tc = CodingTestCase.builder()
                            .codingQuestion(coding)
                            .input(tcDto.getInput())
                            .output(tcDto.getOutput())
                            .visibility(tcDto.getVisibility() != null ? tcDto.getVisibility() : TestCaseVisibility.HIDDEN)
                            .orderNumber(tcDto.getOrderNumber() != null ? tcDto.getOrderNumber() : 0)
                            .build();
                    coding.getTestCases().add(tc);
                }
            }

            if (request.getLanguageConfigs() != null) {
                coding.getLanguageConfigs().clear();
                for (LanguageConfigDto lcDto : request.getLanguageConfigs()) {
                    ProblemLanguageConfig plc = ProblemLanguageConfig.builder()
                            .codingQuestion(coding)
                            .language(lcDto.getLanguage())
                            .boilerplate(lcDto.getBoilerplate())
                            .hiddenCode(lcDto.getHiddenCode())
                            .build();
                    coding.getLanguageConfigs().add(plc);
                }
            }
        }
        
        Question updated = questionRepository.save(question);
        log.info("Question updated successfully");
        
        return mapToSummaryDto(updated, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public QuestionSummaryDto getQuestionById(UUID questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + questionId));
        return mapToSummaryDto(question, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<QuestionSummaryDto> getAllQuestions(QuestionType type, String topic) {
        List<Question> questions = questionRepository.findAll().stream()
                .filter(q -> type == null || q.getType() == type)
                .filter(q -> topic == null || (q.getTopic() != null && q.getTopic().equalsIgnoreCase(topic)))
                .collect(Collectors.toList());
        
        return questions.stream()
                .map(q -> mapToSummaryDto(q, 0))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<QuestionSummaryDto> searchQuestions(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String searchLower = searchTerm.toLowerCase();
        List<Question> questions = questionRepository.findAll().stream()
                .filter(q -> q.getTitle().toLowerCase().contains(searchLower) ||
                        (q.getTopic() != null && q.getTopic().toLowerCase().contains(searchLower)) ||
                        (q.getDescription() != null && q.getDescription().toLowerCase().contains(searchLower)))
                .collect(Collectors.toList());
        
        return questions.stream()
                .map(q -> mapToSummaryDto(q, 0))
                .collect(Collectors.toList());
    }
    
    // ===== Assessment Linking =====
    
    @Override
    @Transactional
    public void linkQuestionsToAssessment(QuestionAttachmentRequestDto request) {
        log.info("Linking questions to assessment: {}", request.getAssessmentId());
        
        Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new RuntimeException("Assessment not found"));
        
        if (request.getExistingQuestionIds() == null || request.getExistingQuestionIds().isEmpty()) {
            throw new IllegalArgumentException("No questions provided to link");
        }
        
        int currentMaxOrder = assessmentQuestionRepository
                .findByAssessmentIdOrderByOrderNumber(request.getAssessmentId())
                .stream()
                .mapToInt(AssessmentQuestion::getOrderNumber)
                .max()
                .orElse(-1);
        
        int nextOrder = currentMaxOrder + 1;
        
        for (UUID questionId : request.getExistingQuestionIds()) {
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question not found: " + questionId));
            
            AssessmentQuestion assessmentQuestion = new AssessmentQuestion();
            assessmentQuestion.setAssessment(assessment);
            assessmentQuestion.setQuestion(question);
            assessmentQuestion.setOrderNumber(nextOrder++);
            
            assessmentQuestionRepository.save(assessmentQuestion);
            log.info("Linked question {} to assessment {}", questionId, assessment.getId());
        }
        
        log.info("Successfully linked {} questions to assessment", request.getExistingQuestionIds().size());
    }
    
    @Override
    @Transactional
    public void removeQuestionFromAssessment(UUID assessmentId, UUID questionId) {
        log.info("Removing question {} from assessment {}", questionId, assessmentId);
        
        List<AssessmentQuestion> assessmentQuestions = assessmentQuestionRepository
                .findByAssessmentIdOrderByOrderNumber(assessmentId);
        
        AssessmentQuestion toRemove = assessmentQuestions.stream()
                .filter(aq -> aq.getQuestion().getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Question not linked to this assessment"));
        
        assessmentQuestionRepository.delete(toRemove);
        log.info("Question removed successfully");
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<QuestionSummaryDto> getQuestionsByAssessmentId(UUID assessmentId) {
        List<AssessmentQuestion> assessmentQuestions = assessmentQuestionRepository
                .findByAssessmentIdOrderByOrderNumber(assessmentId);
        
        return assessmentQuestions.stream()
                .map(aq -> mapToSummaryDto(aq.getQuestion(), aq.getOrderNumber()))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void reorderQuestions(UUID assessmentId, List<UUID> questionIdsInOrder) {
        log.info("Reordering questions for assessment: {}", assessmentId);
        
        List<AssessmentQuestion> assessmentQuestions = assessmentQuestionRepository
                .findByAssessmentIdOrderByOrderNumber(assessmentId);
        Map<UUID, AssessmentQuestion> questionMap = assessmentQuestions.stream()
                .collect(Collectors.toMap(aq -> aq.getQuestion().getId(), aq -> aq));
        
        for (int i = 0; i < questionIdsInOrder.size(); i++) {
            UUID questionId = questionIdsInOrder.get(i);
            AssessmentQuestion aq = questionMap.get(questionId);
            if (aq != null) {
                aq.setOrderNumber(i);
                assessmentQuestionRepository.save(aq);
            }
        }
        
        log.info("Questions reordered successfully");
    }
    
    // ===== BULK CSV UPLOAD =====
    
    @Override
    @Transactional
    public BulkUploadResultDto bulkUploadQuestions(MultipartFile csvFile, UUID assessmentId) {
        log.info("Starting bulk upload for assessment: {}", assessmentId);
        
        // Validate assessment exists
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found with ID: " + assessmentId));
        
        List<BulkUploadResultDto.ErrorDetail> errors = new ArrayList<>();
        int successCount = 0;
        int totalRows = 0;
        
        try {
            // Validate CSV structure
            CsvQuestionParser.validateCsvStructure(csvFile);
            
            // Parse CSV
            List<QuestionBulkUploadDto> questionDtos = CsvQuestionParser.parseQuestionsCsv(csvFile, assessmentId);
            totalRows = questionDtos.size();
            
            // Get current max order
            int currentMaxOrder = assessmentQuestionRepository
                    .findByAssessmentIdOrderByOrderNumber(assessmentId)
                    .stream()
                    .mapToInt(AssessmentQuestion::getOrderNumber)
                    .max()
                    .orElse(-1);
            
            int nextOrder = currentMaxOrder + 1;
            
            // Process each question
            for (QuestionBulkUploadDto dto : questionDtos) {
                try {
                    // Create question entity
                    Question question = createQuestionFromBulkDto(dto);
                    Question savedQuestion = questionRepository.save(question);
                    
                    // Link to assessment
                    AssessmentQuestion assessmentQuestion = new AssessmentQuestion();
                    assessmentQuestion.setAssessment(assessment);
                    assessmentQuestion.setQuestion(savedQuestion);
                    assessmentQuestion.setOrderNumber(nextOrder++);
                    assessmentQuestionRepository.save(assessmentQuestion);
                    
                    successCount++;
                    log.debug("Successfully processed question: {}", dto.getTitle());
                    
                } catch (Exception e) {
                    log.error("Failed to process question: {}", dto.getTitle(), e);
                    errors.add(BulkUploadResultDto.ErrorDetail.builder()
                            .rowNumber(dto.getOrderNumber() + 1)
                            .errorMessage(e.getMessage())
                            .rowData(dto.getTitle())
                            .build());
                }
            }
            
            String message = String.format("Bulk upload completed. %d/%d questions added successfully.", 
                    successCount, totalRows);
            
            log.info(message);
            
            return BulkUploadResultDto.builder()
                    .totalRows(totalRows)
                    .successfulInserts(successCount)
                    .failedInserts(totalRows - successCount)
                    .errors(errors)
                    .message(message)
                    .build();
            
        } catch (Exception e) {
            log.error("Bulk upload failed: {}", e.getMessage());
            throw new RuntimeException("Bulk upload failed: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void deleteQuestion(UUID questionId) {
        log.info("Deleting question: {}", questionId);
        
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        // Check if used in any assessments
        List<AssessmentQuestion> assessmentQuestions = assessmentQuestionRepository
                .findByAssessmentIdOrderByOrderNumber(questionId); // This might need adjustment
        
        if (!assessmentQuestions.isEmpty()) {
            log.warn("Question {} is used in {} assessment(s). Removing links.", 
                    questionId, assessmentQuestions.size());
            assessmentQuestionRepository.deleteAll(assessmentQuestions);
        }
        
        questionRepository.delete(question);
        log.info("Question deleted successfully");
    }
    
    // ===== Helper Methods =====
    
    private void linkQuestionToAssessment(UUID questionId, UUID assessmentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        int nextOrder = assessmentQuestionRepository
                .findByAssessmentIdOrderByOrderNumber(assessmentId)
                .stream()
                .mapToInt(AssessmentQuestion::getOrderNumber)
                .max()
                .orElse(-1) + 1;
        
        AssessmentQuestion assessmentQuestion = new AssessmentQuestion();
        assessmentQuestion.setAssessment(assessment);
        assessmentQuestion.setQuestion(question);
        assessmentQuestion.setOrderNumber(nextOrder);
        
        assessmentQuestionRepository.save(assessmentQuestion);
        log.info("Linked question {} to assessment {}", questionId, assessmentId);
    }
    
    private Question createQuestionFromBulkDto(QuestionBulkUploadDto dto) {
        switch (dto.getType()) {
            case MCQ:
                McqQuestion mcq = McqQuestion.builder()
                        .type(dto.getType())
                        .title(dto.getTitle())
                        .description(dto.getDescription())
                        .topic(dto.getTopic())
                        .score(dto.getScore())
                        .negativeScore(dto.getNegativeScore())
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();
                
                // Set options map
                Map<String, String> options = new HashMap<>();
                if (dto.getOptionA() != null) options.put("A", dto.getOptionA());
                if (dto.getOptionB() != null) options.put("B", dto.getOptionB());
                if (dto.getOptionC() != null) options.put("C", dto.getOptionC());
                if (dto.getOptionD() != null) options.put("D", dto.getOptionD());
                mcq.setOptions(options);
                
                // Set correct option
                if (dto.getCorrectOption() != null) {
                    mcq.setCorrectOption(Arrays.asList(dto.getCorrectOption()));
                }
                
                return mcq;
                
            case CODING:
                CodingQuestion codingQuestion = CodingQuestion.builder()
                        .type(dto.getType())
                        .title(dto.getTitle())
                        .description(dto.getDescription())
                        .topic(dto.getTopic())
                        .score(dto.getScore())
                        .negativeScore(dto.getNegativeScore())
                        .timeLimit(dto.getTimeLimit() != null ? dto.getTimeLimit() : 2000)
                        .memoryLimit(dto.getMemoryLimit() != null ? dto.getMemoryLimit() : 256)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                if (dto.getTestCases() != null) {
                    for (CodingTestCaseDto tcDto : dto.getTestCases()) {
                        CodingTestCase tc = CodingTestCase.builder()
                                .codingQuestion(codingQuestion)
                                .input(tcDto.getInput())
                                .output(tcDto.getOutput())
                                .visibility(tcDto.getVisibility() != null ? tcDto.getVisibility() : TestCaseVisibility.HIDDEN)
                                .orderNumber(tcDto.getOrderNumber() != null ? tcDto.getOrderNumber() : 0)
                                .build();
                        codingQuestion.getTestCases().add(tc);
                    }
                }

                return codingQuestion;
                
            default:
                throw new IllegalArgumentException("Unsupported question type: " + dto.getType());
        }
    }
    
    private QuestionSummaryDto mapToSummaryDto(Question question, Integer orderNumber) {
        QuestionSummaryDto.QuestionSummaryDtoBuilder builder = QuestionSummaryDto.builder()
                .questionId(question.getId())
                .type(question.getType())
                .title(question.getTitle())
                .description(question.getDescription())
                .topic(question.getTopic())
                .score(question.getScore())
                .negativeScore(question.getNegativeScore())
                .orderNumber(orderNumber)
                .tags(question.getTags());

        if (question instanceof com.Lamicons.CurriculumService.Entity.Question.McqQuestion) {
            com.Lamicons.CurriculumService.Entity.Question.McqQuestion mcq =
                (com.Lamicons.CurriculumService.Entity.Question.McqQuestion) question;
            builder.options(mcq.getOptions())
                   .correctOption(mcq.getCorrectOption());
        }

        if (question instanceof com.Lamicons.CurriculumService.Entity.Question.CodingQuestion) {
            com.Lamicons.CurriculumService.Entity.Question.CodingQuestion coding =
                (com.Lamicons.CurriculumService.Entity.Question.CodingQuestion) question;
            builder.timeLimit(coding.getTimeLimit())
                   .memoryLimit(coding.getMemoryLimit());
            if (coding.getTestCases() != null) {
                builder.testCases(coding.getTestCases().stream()
                        .map(tc -> com.Lamicons.CurriculumService.DTO.Question.CodingTestCaseDto.builder()
                                .id(tc.getId())
                                .input(tc.getInput())
                                .output(tc.getOutput())
                                .visibility(tc.getVisibility())
                                .orderNumber(tc.getOrderNumber())
                                .build())
                        .collect(java.util.stream.Collectors.toList()));
            }
            if (coding.getLanguageConfigs() != null) {
                builder.languageConfigs(coding.getLanguageConfigs().stream()
                        .map(lc -> com.Lamicons.CurriculumService.DTO.Question.LanguageConfigDto.builder()
                                .language(lc.getLanguage())
                                .boilerplate(lc.getBoilerplate())
                                .hiddenCode(lc.getHiddenCode())
                                .build())
                        .collect(java.util.stream.Collectors.toList()));
            }
        }

        return builder.build();
    }
}
