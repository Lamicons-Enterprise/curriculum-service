package com.Lamicons.CurriculumService.Entity.JunctionTable;


import com.Lamicons.CurriculumService.Entity.Assessment;
import com.Lamicons.CurriculumService.Entity.Question.Question;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "assessment_question")
public class AssessmentQuestion {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private Integer orderNumber;
}



