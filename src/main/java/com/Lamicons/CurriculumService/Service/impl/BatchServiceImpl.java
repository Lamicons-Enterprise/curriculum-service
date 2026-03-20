package com.Lamicons.CurriculumService.Service.impl;

import com.Lamicons.CurriculumService.DTO.Batch.BatchRequestDto;
import com.Lamicons.CurriculumService.DTO.Batch.BatchResponseDto;
import com.Lamicons.CurriculumService.Entity.Course;
import com.Lamicons.CurriculumService.Entity.PhysicalEntity.Batch;
import com.Lamicons.CurriculumService.Entity.PhysicalEntity.University;
import com.Lamicons.CurriculumService.Exception.ResourceNotFoundException;
import com.Lamicons.CurriculumService.Repository.BatchRepository;
import com.Lamicons.CurriculumService.Repository.CourseRepository;
import com.Lamicons.CurriculumService.Repository.UniversityRepository;
import com.Lamicons.CurriculumService.Service.BatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BatchServiceImpl implements BatchService {
    
    private final BatchRepository batchRepository;
    private final CourseRepository courseRepository;
    private final UniversityRepository universityRepository;
    
    @Override
    @Transactional
    public BatchResponseDto createBatch(BatchRequestDto requestDto, UUID userId) {
        log.info("BatchServiceImpl : createBatch : Creating batch with code: {}", requestDto.getBatchCode());
        
        if (requestDto.getEndDate().isBefore(requestDto.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        
        if (batchRepository.existsByBatchCode(requestDto.getBatchCode())) {
            throw new IllegalArgumentException("Batch with code " + requestDto.getBatchCode() + " already exists");
        }
        
        Course course = courseRepository.findById(requestDto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + requestDto.getCourseId()));
        

        University university = universityRepository.findById(requestDto.getUniversityId())
                    .orElseThrow(() -> new ResourceNotFoundException("University not found with ID: " + requestDto.getUniversityId()));

        
        Batch batch = new Batch();
        batch.setBatchCode(requestDto.getBatchCode());
        batch.setBatchName(requestDto.getBatchName());
        batch.setStartDate(requestDto.getStartDate());
        batch.setEndDate(requestDto.getEndDate());
        batch.setCapacity(requestDto.getCapacity());
        batch.setActive(true);
        batch.setCourse(course);
        batch.setUniversity(university);
        batch.setCreatedBy(userId);
        batch.setUpdatedBy(userId);
        
        Batch savedBatch = batchRepository.save(batch);
        log.info("BatchServiceImpl : createBatch : Batch created successfully with ID: {}", savedBatch.getId());
        
        return mapToResponseDto(savedBatch);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BatchResponseDto getBatchById(UUID id) {
        log.info("BatchServiceImpl : getBatchById : Fetching batch with ID: {}", id);
        
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with ID: " + id));
        
        log.info("BatchServiceImpl : getBatchById : Batch found: {}", batch.getBatchName());
        return mapToResponseDto(batch);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BatchResponseDto> getAllBatches() {
        log.info("BatchServiceImpl : getAllBatches : Fetching all batches");
        
        List<Batch> batches = batchRepository.findAll();
        log.info("BatchServiceImpl : getAllBatches : Found {} batches", batches.size());
        
        return batches.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BatchResponseDto> getBatchesByCourseId(UUID courseId) {
        log.info("BatchServiceImpl : getBatchesByCourseId : Fetching batches for course ID: {}", courseId);

        List<Batch> batches = batchRepository.findByCourseId(courseId);
        if (batches.isEmpty()) {
            log.info("BatchServiceImpl : getBatchesByCourseId : No batches found for course ID: {}", courseId);
            return List.of();
        }
        log.info("BatchServiceImpl : getBatchesByCourseId : Found {} batches", batches.size());
        
        return batches.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BatchResponseDto> getBatchesByUniversityId(UUID universityId) {
        log.info("BatchServiceImpl : getBatchesByUniversityId : Fetching batches for university ID: {}", universityId);
        
        List<Batch> batches = batchRepository.findByUniversityId(universityId);
        if (batches.isEmpty()) {
            log.info("BatchServiceImpl : getBatchesByUniversityId : No batches found for university ID: {}", universityId);
            return List.of();
        }
        log.info("BatchServiceImpl : getBatchesByUniversityId : Found {} batches", batches.size());
        
        return batches.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public BatchResponseDto updateBatch(UUID id, BatchRequestDto requestDto, UUID userId) {
        log.info("BatchServiceImpl : updateBatch : Updating batch with ID: {}", id);
        
        // Validate dates
        if (requestDto.getEndDate().isBefore(requestDto.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with ID: " + id));
        

        if (!batch.getBatchCode().equals(requestDto.getBatchCode()) &&
            batchRepository.existsByBatchCode(requestDto.getBatchCode())) {
            throw new IllegalArgumentException("Batch with code " + requestDto.getBatchCode() + " already exists");
        }

        Course course = courseRepository.findById(requestDto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + requestDto.getCourseId()));

        University university= universityRepository.findById(requestDto.getUniversityId())
                    .orElseThrow(() -> new ResourceNotFoundException("University not found with ID: " + requestDto.getUniversityId()));

        if(requestDto.getBatchCode()!=null)
            batch.setBatchCode(requestDto.getBatchCode());
        if (requestDto.getBatchName()!=null)
            batch.setBatchName(requestDto.getBatchName());
        if(requestDto.getStartDate()!=null)
            batch.setStartDate(requestDto.getStartDate());
        if(requestDto.getEndDate()!=null)
            batch.setEndDate(requestDto.getEndDate());
        if(requestDto.getCapacity()!=null)
            batch.setCapacity(requestDto.getCapacity());
        batch.setCourse(course);
        batch.setUniversity(university);
        batch.setUpdatedBy(userId);
        
        Batch updatedBatch = batchRepository.save(batch);
        log.info("BatchServiceImpl : updateBatch : Batch updated successfully");
        
        return mapToResponseDto(updatedBatch);
    }
    
    @Override
    @Transactional
    public void deleteBatch(UUID id) {
        log.info("BatchServiceImpl : deleteBatch : Deleting batch with ID: {}", id);
        
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with ID: " + id));
        
        batchRepository.delete(batch);
        log.info("BatchServiceImpl : deleteBatch : Batch deleted successfully");
    }

    private BatchResponseDto mapToResponseDto(Batch batch) {
        return BatchResponseDto.builder()
                .id(batch.getId())
                .batchCode(batch.getBatchCode())
                .batchName(batch.getBatchName())
                .startDate(batch.getStartDate())
                .endDate(batch.getEndDate())
                .capacity(batch.getCapacity())
                .active(batch.getActive())
                .courseId(batch.getCourse() != null ? batch.getCourse().getId() : null)
                .courseName(batch.getCourse() != null ? batch.getCourse().getName() : null)
                .universityId(batch.getUniversity() != null ? batch.getUniversity().getId() : null)
                .universityName(batch.getUniversity() != null ? batch.getUniversity().getName() : null)
                .createdAt(batch.getCreatedAt())
                .updatedAt(batch.getUpdatedAt())
                .createdBy(batch.getCreatedBy())
                .updatedBy(batch.getUpdatedBy())
                .build();
    }
}
