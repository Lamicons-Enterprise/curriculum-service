package com.Lamicons.CurriculumService.Service;

import com.Lamicons.CurriculumService.DTO.University.UniversityRequestDto;
import com.Lamicons.CurriculumService.DTO.University.UniversityResponseDto;

import java.util.List;
import java.util.UUID;

public interface UniversityService {

    UniversityResponseDto createUniversity(UniversityRequestDto requestDto, String userId);

    UniversityResponseDto getUniversityById(UUID id);

    List<UniversityResponseDto> getAllUniversities();

    UniversityResponseDto updateUniversity(UUID id, UniversityRequestDto requestDto, String userId);

    void deleteUniversity(UUID id);
}
