package com.Lamicons.CurriculumService.DTO.File;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadResponseDto {
    private String fileName;
    private String fileType;
    private long fileSize;
    private String fileUrl;
    private String message;
    private boolean success;
}