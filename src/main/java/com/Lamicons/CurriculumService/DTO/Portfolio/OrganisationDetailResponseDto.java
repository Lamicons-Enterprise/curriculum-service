package com.Lamicons.CurriculumService.DTO.Portfolio;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganisationDetailResponseDto {

    private UUID id;
    private String address;
    private Integer totalEmployees;
    private Integer numberOfTrainers;
    private Integer totalStudentsTrained;
    private Integer totalCoursesOffered;
    private Integer totalClients;
    private Integer universityPartners;
    private Integer studentsPlaced;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
