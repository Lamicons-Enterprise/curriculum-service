package com.Lamicons.CurriculumService.Service;

import com.Lamicons.CurriculumService.DTO.Batch.BatchRequestDto;
import com.Lamicons.CurriculumService.DTO.Batch.BatchResponseDto;

import java.util.List;
import java.util.UUID;

public interface BatchService {

    BatchResponseDto createBatch(BatchRequestDto requestDto, UUID userId);

    BatchResponseDto getBatchById(UUID id);

    List<BatchResponseDto> getAllBatches();

    List<BatchResponseDto> getBatchesByCourseId(UUID courseId);

    List<BatchResponseDto> getBatchesByUniversityId(UUID universityId);

    BatchResponseDto updateBatch(UUID id, BatchRequestDto requestDto, UUID userId);

    void deleteBatch(UUID id);
}
