package com.Lamicons.CurriculumService.Entity.Question;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder
@Table(name = "coding_question")
public class CodingQuestion extends Question {

    @Column(name = "time_limit")
    @Builder.Default
    private Integer timeLimit = 2000;

    @Column(name = "memory_limit")
    @Builder.Default
    private Integer memoryLimit = 256;

    @OneToMany(mappedBy = "codingQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNumber ASC")
    @Builder.Default
    private List<CodingTestCase> testCases = new ArrayList<>();

    @OneToMany(mappedBy = "codingQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProblemLanguageConfig> languageConfigs = new ArrayList<>();
}
