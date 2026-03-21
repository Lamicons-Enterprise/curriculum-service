package com.Lamicons.CurriculumService.DTO.Question;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageConfigDto {
    private SupportedLanguage language;
    private String boilerplate;
    private String hiddenCode;
}
