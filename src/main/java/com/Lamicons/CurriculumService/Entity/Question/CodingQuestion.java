package com.Lamicons.CurriculumService.Entity.Question;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder
@Table(name = "coding_question")
public class CodingQuestion extends Question {

    @NotEmpty(message = "At least one test case must be provided")
    @Size(min = 1, message = "At least one test case must be provided")
    @ElementCollection
    @CollectionTable(name = "coding_testcases", joinColumns = @JoinColumn(name = "question_id"))
    private List<String> testcases;
}
