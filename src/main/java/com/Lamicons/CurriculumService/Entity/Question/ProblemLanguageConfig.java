package com.Lamicons.CurriculumService.Entity.Question;

import com.Lamicons.CurriculumService.DTO.Question.SupportedLanguage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "problem_language_config",
    uniqueConstraints = @UniqueConstraint(columnNames = {"question_id", "language"}),
    indexes = @Index(name = "idx_lang_config_question", columnList = "question_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemLanguageConfig {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private CodingQuestion codingQuestion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupportedLanguage language;

    @NotNull
    @Column(columnDefinition = "text", nullable = false)
    private String boilerplate;

    @NotNull
    @Column(name = "hidden_code", columnDefinition = "text", nullable = false)
    private String hiddenCode;
}
