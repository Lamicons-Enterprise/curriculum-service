package com.Lamicons.CurriculumService.DTO.File;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadDirectRequestDto {
    private String fileKey;
    private String fileUrl;
}