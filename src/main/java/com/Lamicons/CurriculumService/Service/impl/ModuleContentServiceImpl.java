package com.Lamicons.CurriculumService.Service.impl;

import com.Lamicons.CurriculumService.DTO.Module.ModuleContentRequestDto;
import com.Lamicons.CurriculumService.DTO.Module.ModuleContentResponseDto;
import com.Lamicons.CurriculumService.Entity.Module;
import com.Lamicons.CurriculumService.Entity.ModuleContent;
import com.Lamicons.CurriculumService.Repository.ModuleContentRepository;
import com.Lamicons.CurriculumService.Repository.ModuleRepository;
import com.Lamicons.CurriculumService.Service.ModuleContentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModuleContentServiceImpl implements ModuleContentService {
    private final ModuleContentRepository moduleContentRepository;
    private final ModuleRepository moduleRepository;

    @Override
    @Transactional
    public ModuleContentResponseDto createModuleContent(UUID moduleId, ModuleContentRequestDto request) {
        log.info("ModuleContentServiceImpl : createModuleContent : Creating new content for module ID: {}", moduleId);
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> {
                    log.error("ModuleContentServiceImpl : createModuleContent : Module not found with ID: {}", moduleId);
                    return new IllegalArgumentException("Module not found");
                });

        ModuleContent content = new ModuleContent();
        content.setId(UUID.randomUUID());
        content.setModule(module);
        content.setContentType(request.getContentType());
        content.setTitle(request.getTitle());
        content.setDescription(request.getDescription());
        content.setUrl(request.getUrl());
        content.setThumbnailUrl(request.getThumbnailUrl());
        content.setEstimatedTimeMinutes(request.getEstimatedTimeMinutes() != null ? request.getEstimatedTimeMinutes() : 0);
        content.setOrder(request.getOrder() != null ? request.getOrder() : 0);
        content.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        content.setDifficulty(request.getDifficulty() != null ? request.getDifficulty() : ModuleContent.Difficulty.BEGINNER);

        ModuleContent savedContent = moduleContentRepository.save(content);
        log.info("ModuleContentServiceImpl : createModuleContent : Content created with ID: {}", savedContent.getId());
        return convertToDto(savedContent);
    }

    @Override
    @Transactional
    public ModuleContentResponseDto updateModuleContent(UUID contentId, ModuleContentRequestDto request) {
        log.info("ModuleContentServiceImpl : updateModuleContent : Updating content with ID: {}", contentId);
        ModuleContent content = moduleContentRepository.findById(contentId)
                .orElseThrow(() -> {
                    log.error("ModuleContentServiceImpl : updateModuleContent : Content not found with ID: {}", contentId);
                    return new IllegalArgumentException("Module content not found");
                });

        if (request.getTitle() != null) {
            content.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            content.setDescription(request.getDescription());
        }
        if (request.getContentType() != null) {
            content.setContentType(request.getContentType());
        }
        if (request.getUrl() != null) {
            content.setUrl(request.getUrl());
        }
        if (request.getThumbnailUrl() != null) {
            content.setThumbnailUrl(request.getThumbnailUrl());
        }

        if (request.getEstimatedTimeMinutes() != null) {
            content.setEstimatedTimeMinutes(request.getEstimatedTimeMinutes());
        }
        if (request.getOrder() != null) {
            content.setOrder(request.getOrder());
        }
        if (request.getIsActive() != null) {
            content.setIsActive(request.getIsActive());
        }
        if (request.getDifficulty() != null) {
            content.setDifficulty(request.getDifficulty());
        }

        content.setUpdatedAt(Instant.now());
        ModuleContent updatedContent = moduleContentRepository.save(content);
        log.info("ModuleContentServiceImpl : updateModuleContent : Content updated successfully");
        return convertToDto(updatedContent);
    }

    @Override
    @Transactional
    public void deleteModuleContent(UUID contentId) {
        log.info("ModuleContentServiceImpl : deleteModuleContent : Deleting content with ID: {}", contentId);
        if (!moduleContentRepository.existsById(contentId)) {
            log.error("ModuleContentServiceImpl : deleteModuleContent : Content not found with ID: {}", contentId);
            throw new IllegalArgumentException("Module content not found");
        }
        moduleContentRepository.deleteById(contentId);
        log.info("ModuleContentServiceImpl : deleteModuleContent : Content deleted successfully");
    }

    @Override
    public ModuleContentResponseDto getModuleContent(UUID contentId) {
        log.info("ModuleContentServiceImpl : getModuleContent : Retrieving content with ID: {}", contentId);
        ModuleContent   moduleContent= moduleContentRepository.findById(contentId)
                .orElseThrow(() -> {
                    log.error("ModuleContentServiceImpl : getModuleContent : Content not found with ID: {}", contentId);
                    return new IllegalArgumentException("Module content not found");
                });
        log.info("ModuleContentServiceImpl : getModuleContent : Content retrieved successfully");
        return convertToDto(moduleContent);
    }

    @Override
    public List<ModuleContentResponseDto> listContentsByModule(UUID moduleId) {
        log.info("ModuleContentServiceImpl : listContentsByModule : Retrieving contents for module ID: {}", moduleId);
        List<ModuleContent> contents = moduleContentRepository.findByModuleIdOrderByOrder(moduleId);

        List<ModuleContentResponseDto> responseList = contents.stream()
                .map(this::convertToDto)
                .toList();
                
        log.info("ModuleContentServiceImpl : listContentsByModule : Found {} contents for module", responseList.size());
        return responseList;
    }

    @Override
    @Transactional
    public ModuleContentResponseDto updateContentUrl(UUID contentId, String contentUrl) {
        log.info("ModuleContentServiceImpl : updateContentUrl : Updating content URL for content ID: {}", contentId);
        ModuleContent content = moduleContentRepository.findById(contentId)
                .orElseThrow(() -> {
                    log.error("ModuleContentServiceImpl : updateContentUrl : Content not found with ID: {}", contentId);
                    return new IllegalArgumentException("Module content not found");
                });

        content.setUrl(contentUrl);
        content.setUpdatedAt(Instant.now());
        ModuleContent updatedContent = moduleContentRepository.save(content);
        log.info("ModuleContentServiceImpl : updateContentUrl : Content URL updated successfully");
        return convertToDto(updatedContent);
    }

    private ModuleContentResponseDto convertToDto(ModuleContent content) {
        return ModuleContentResponseDto.builder()
                .id(content.getId())
                .contentType(content.getContentType())
                .title(content.getTitle())
                .description(content.getDescription())
                .url(content.getUrl())
                .thumbnailUrl(content.getThumbnailUrl())
                .estimatedTimeMinutes(content.getEstimatedTimeMinutes())
                .order(content.getOrder())
                .isActive(content.getIsActive())
                .difficulty(content.getDifficulty())
                .completionStatus(content.getCompletionStatus())
                .moduleId(content.getModule().getId())
                .build();
    }
}