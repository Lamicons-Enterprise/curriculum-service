package com.Lamicons.CurriculumService.DTO.Portfolio;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganisationDetailRequestDto {

    @NotBlank(message = "Address is required")
    private String address;

    private Integer totalEmployees;
    private Integer numberOfTrainers;
    private Integer totalStudentsTrained;
    private Integer totalCoursesOffered;
    private Integer totalClients;
    private Integer universityPartners;
    private Integer studentsPlaced;
}
