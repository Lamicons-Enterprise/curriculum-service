package com.Lamicons.CurriculumService.DTO.Question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Result DTO for bulk question upload operations
 * Provides summary of upload results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkUploadResultDto {
    
    private Integer totalRows;
    
    private Integer successfulInserts;
    
    private Integer failedInserts;
    
    private List<ErrorDetail> errors;
    
    private String message;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetail {
        private Integer rowNumber;
        private String errorMessage;
        private String rowData;
    }
}
