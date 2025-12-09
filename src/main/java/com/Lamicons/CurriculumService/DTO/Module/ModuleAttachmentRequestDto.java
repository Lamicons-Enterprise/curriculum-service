package com.Lamicons.CurriculumService.DTO.Module;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for attaching modules to a course (Step 2 of admin flow)
 * Supports both creating new modules and linking existing ones
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleAttachmentRequestDto {
    
    @NotNull(message = "Course ID is required")
    private UUID courseId;
    
    // Option 1: Link existing modules (IDs of pre-created reusable modules)
    private List<UUID> existingModuleIds;
    
    // Option 2: Create and attach new modules
    private List<NewModuleDto> newModules;
    
    /**
     * Inner DTO for creating new modules inline
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewModuleDto {
        @NotBlank(message = "Module title is required")
        @Size(min = 2, max = 255)
        private String title;
        
        @Size(max = 4000)
        private String description;
        
        private String tags;
        
        private Integer order;
        
        @Builder.Default
        private Boolean isReusable = true;
    }
}
