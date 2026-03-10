package com.Lamicons.CurriculumService.Entity.Portfolio;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "organisation_detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganisationDetail {

    @Id
    @GeneratedValue
    private UUID id;

    private String address;

    @Builder.Default
    private Integer totalEmployees = 0;

    @Builder.Default
    private Integer numberOfTrainers = 0;

    @Builder.Default
    private Integer totalStudentsTrained = 0;

    @Builder.Default
    private Integer totalCoursesOffered = 0;

    @Builder.Default
    private Integer totalClients = 0;

    @Builder.Default
    private Integer universityPartners = 0;

    @Builder.Default
    private Integer studentsPlaced = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
