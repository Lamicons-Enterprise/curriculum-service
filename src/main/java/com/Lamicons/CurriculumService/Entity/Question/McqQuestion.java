package com.Lamicons.CurriculumService.Entity.Question;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mcq_question")
public class McqQuestion extends Question {

    @NotEmpty(message = "MCQ options cannot be empty")
    @Size(min = 2, message = "At least two options must be provided for an MCQ")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mcq_options", joinColumns = @JoinColumn(name = "question_id"))
    @MapKeyColumn(name = "option_key")
    @Column(name = "option_value", nullable = false)
    private Map<String, String> options;

    @NotEmpty(message = "Correct option cannot be empty")
    @Column(nullable = false)
    private List<String> correctOption;
}
