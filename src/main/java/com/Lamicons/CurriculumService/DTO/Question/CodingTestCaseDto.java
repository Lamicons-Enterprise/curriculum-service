package com.Lamicons.CurriculumService.DTO.Question;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodingTestCaseDto {
    private UUID id;
    private String input;
    private String output;
    private TestCaseVisibility visibility;
    private Integer orderNumber;
}
