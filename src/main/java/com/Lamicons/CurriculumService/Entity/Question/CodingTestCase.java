package com.Lamicons.CurriculumService.Entity.Question;

import com.Lamicons.CurriculumService.DTO.Question.TestCaseVisibility;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "coding_test_case",
    indexes = @Index(name = "idx_testcase_question", columnList = "question_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodingTestCase {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private CodingQuestion codingQuestion;

    @NotNull
    @Column(columnDefinition = "text", nullable = false)
    private String input;

    @NotNull
    @Column(columnDefinition = "text", nullable = false)
    private String output;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestCaseVisibility visibility;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;
}
