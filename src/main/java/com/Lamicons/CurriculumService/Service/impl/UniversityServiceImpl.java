package com.Lamicons.CurriculumService.Service.impl;

import com.Lamicons.CurriculumService.DTO.University.UniversityRequestDto;
import com.Lamicons.CurriculumService.DTO.University.UniversityResponseDto;
import com.Lamicons.CurriculumService.Entity.PhysicalEntity.University;
import com.Lamicons.CurriculumService.Exception.ResourceNotFoundException;
import com.Lamicons.CurriculumService.Repository.UniversityRepository;
import com.Lamicons.CurriculumService.Service.UniversityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UniversityServiceImpl implements UniversityService {
    
    private final UniversityRepository universityRepository;
    
    @Override
    public UniversityResponseDto createUniversity(UniversityRequestDto requestDto, String userId) {
        log.info("UniversityServiceImpl : createUniversity : Creating university with code: {}", requestDto.getUniversityCode());

        if (universityRepository.existsByUniversityCode(requestDto.getUniversityCode())) {
            throw new IllegalArgumentException("University with code " + requestDto.getUniversityCode() + " already exists");
        }

        UUID adminID = UUID.fromString(userId);
        
        University university = University.builder()
                .universityCode(requestDto.getUniversityCode())
                .name(requestDto.getName())
                .city(requestDto.getCity())
                .state(requestDto.getState())
                .country(requestDto.getCountry())
                .active(requestDto.getActive() != null ? requestDto.getActive() : true)
                .createdBy(adminID)
                .updatedBy(adminID)
                .build();
        
        University savedUniversity = universityRepository.save(university);
        log.info("UniversityServiceImpl : createUniversity : University created successfully with ID: {}", savedUniversity.getId());
        
        return mapToResponseDto(savedUniversity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UniversityResponseDto getUniversityById(UUID id) {
        log.info("UniversityServiceImpl : getUniversityById : Fetching university with ID: {}", id);
        
        University university = universityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("University not found with ID: " + id));
        
        log.info("UniversityServiceImpl : getUniversityById : University found: {}", university.getName());
        return mapToResponseDto(university);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UniversityResponseDto> getAllUniversities() {
        log.info("UniversityServiceImpl : getAllUniversities : Fetching all universities");
        
        List<University> universities = universityRepository.findAll();
        log.info("UniversityServiceImpl : getAllUniversities : Found {} universities", universities.size());
        
        return universities.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public UniversityResponseDto updateUniversity(UUID id, UniversityRequestDto requestDto, String userId) {
        log.info("UniversityServiceImpl : updateUniversity : Updating university with ID: {}", id);
        
        University university = universityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("University not found with ID: " + id));
        
        // Check if university code is being changed and if it already exists
        if (!university.getUniversityCode().equals(requestDto.getUniversityCode()) &&
            universityRepository.existsByUniversityCode(requestDto.getUniversityCode())) {
            throw new IllegalArgumentException("University with code " + requestDto.getUniversityCode() + " already exists");
        }
        UUID adminID = UUID.fromString(userId);
        // Update fields
        university.setUniversityCode(requestDto.getUniversityCode());
        university.setName(requestDto.getName());
        university.setCity(requestDto.getCity());
        university.setState(requestDto.getState());
        university.setCountry(requestDto.getCountry());
        university.setActive(requestDto.getActive() != null ? requestDto.getActive() : university.getActive());
        university.setUpdatedBy(adminID);
        
        University updatedUniversity = universityRepository.save(university);
        log.info("UniversityServiceImpl : updateUniversity : University updated successfully");
        
        return mapToResponseDto(updatedUniversity);
    }
    
    @Override
    @Transactional
    public void deleteUniversity(UUID id) {
        log.info("UniversityServiceImpl : deleteUniversity : Deleting university with ID: {}", id);
        
        University university = universityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("University not found with ID: " + id));
        
        universityRepository.delete(university);
        log.info("UniversityServiceImpl : deleteUniversity : University deleted successfully");
    }
    

    private UniversityResponseDto mapToResponseDto(University university) {
        return UniversityResponseDto.builder()
                .id(university.getId())
                .universityCode(university.getUniversityCode())
                .name(university.getName())
                .city(university.getCity())
                .state(university.getState())
                .country(university.getCountry())
                .active(university.getActive())
                .createdAt(university.getCreatedAt())
                .updatedAt(university.getUpdatedAt())
                .createdBy(university.getCreatedBy())
                .updatedBy(university.getUpdatedBy())
                .build();
    }
}
