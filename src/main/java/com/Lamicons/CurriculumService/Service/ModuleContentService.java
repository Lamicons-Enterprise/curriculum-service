package com.Lamicons.CurriculumService.Service;

import com.Lamicons.CurriculumService.DTO.Module.ModuleContentRequestDto;
import com.Lamicons.CurriculumService.DTO.Module.ModuleContentResponseDto;
import com.Lamicons.CurriculumService.Entity.ModuleContent;

import java.util.List;
import java.util.UUID;

public interface ModuleContentService {
    ModuleContentResponseDto createModuleContent(UUID moduleId, ModuleContentRequestDto request);
    ModuleContentResponseDto updateModuleContent(UUID contentId, ModuleContentRequestDto request);
    void deleteModuleContent(UUID contentId);
    ModuleContentResponseDto getModuleContent(UUID contentId);
    List<ModuleContentResponseDto> listContentsByModule(UUID moduleId);
    ModuleContentResponseDto updateContentUrl(UUID contentId, String contentUrl);
}