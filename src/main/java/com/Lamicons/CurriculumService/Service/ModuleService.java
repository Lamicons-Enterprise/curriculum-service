package com.Lamicons.CurriculumService.Service;

import com.Lamicons.CurriculumService.DTO.Course.CourseResponseDto;
import com.Lamicons.CurriculumService.DTO.Module.*;

import java.util.List;
import java.util.UUID;

public interface ModuleService {

    ModuleSummaryDto createStandaloneModule(ModuleAttachmentRequestDto.NewModuleDto request);

    ModuleSummaryDto updateModule(UUID moduleId, ModuleAttachmentRequestDto.NewModuleDto request);

    ModuleSummaryDto getModuleById(UUID moduleId);

    List<ModuleSummaryDto> getAllReusableModules();

    List<ModuleSummaryDto> searchModules(String searchTerm);

    List<ModuleSummaryDto> getModulesByCourseId(UUID courseId);

    void deleteModule(UUID moduleId);

    CourseResponseDto attachModulesToCourse(ModuleAttachmentRequestDto request);

    void removeModuleFromCourse(UUID courseId, UUID moduleId);

    CourseResponseDto reorderModules(UUID courseId, List<UUID> moduleIdsInOrder);
}
