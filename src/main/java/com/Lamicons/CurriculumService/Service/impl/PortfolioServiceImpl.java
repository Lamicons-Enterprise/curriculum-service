package com.Lamicons.CurriculumService.Service.impl;

import com.Lamicons.CurriculumService.DTO.Portfolio.*;
import com.Lamicons.CurriculumService.Entity.Portfolio.*;
import com.Lamicons.CurriculumService.Exception.ResourceNotFoundException;
import com.Lamicons.CurriculumService.Repository.*;
import com.Lamicons.CurriculumService.Service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioServiceImpl implements PortfolioService {

    private final OrganisationDetailRepository organisationDetailRepository;
    private final FeaturedTrainerRepository featuredTrainerRepository;
    private final FeaturedStudentRepository featuredStudentRepository;
    private final PortfolioHighlightRepository portfolioHighlightRepository;

    @Override
    @Transactional(readOnly = true)
    public PortfolioSummaryResponseDto getFullPortfolio() {
        log.info("PortfolioServiceImpl : getFullPortfolio : Fetching full portfolio");

        OrganisationDetailResponseDto orgDetails = null;
        List<OrganisationDetail> orgList = organisationDetailRepository.findAll();
        if (!orgList.isEmpty()) {
            orgDetails = mapToOrgResponse(orgList.get(0));
        }

        List<FeaturedTrainerResponseDto> trainers = featuredTrainerRepository.findAll()
                .stream().map(this::mapToTrainerResponse).collect(Collectors.toList());

        List<FeaturedStudentResponseDto> students = featuredStudentRepository.findAll()
                .stream().map(this::mapToStudentResponse).collect(Collectors.toList());

        List<PortfolioHighlightResponseDto> achievements = portfolioHighlightRepository
                .findByType(PortfolioHighlightType.ACHIEVEMENT)
                .stream().map(this::mapToHighlightResponse).collect(Collectors.toList());

        List<PortfolioHighlightResponseDto> majorClients = portfolioHighlightRepository
                .findByType(PortfolioHighlightType.MAJOR_CLIENT)
                .stream().map(this::mapToHighlightResponse).collect(Collectors.toList());

        List<PortfolioHighlightResponseDto> universityPartners = portfolioHighlightRepository
                .findByType(PortfolioHighlightType.UNIVERSITY_PARTNER)
                .stream().map(this::mapToHighlightResponse).collect(Collectors.toList());

        log.info("PortfolioServiceImpl : getFullPortfolio : Portfolio fetched successfully");
        return PortfolioSummaryResponseDto.builder()
                .organisationDetails(orgDetails)
                .featuredTrainers(trainers)
                .featuredStudents(students)
                .achievements(achievements)
                .majorClients(majorClients)
                .universityPartners(universityPartners)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public OrganisationDetailResponseDto getOrganisationDetails() {
        log.info("PortfolioServiceImpl : getOrganisationDetails : Fetching organisation details");
        List<OrganisationDetail> orgList = organisationDetailRepository.findAll();
        if (orgList.isEmpty()) {
            throw new ResourceNotFoundException("Organisation details not found");
        }
        log.info("PortfolioServiceImpl : getOrganisationDetails : Organisation details fetched");
        return mapToOrgResponse(orgList.get(0));
    }

    @Override
    @Transactional
    public OrganisationDetailResponseDto createOrUpdateOrganisationDetails(OrganisationDetailRequestDto requestDto) {
        log.info("PortfolioServiceImpl : createOrUpdateOrganisationDetails : Upserting organisation details");
        List<OrganisationDetail> orgList = organisationDetailRepository.findAll();
        OrganisationDetail org;

        if (!orgList.isEmpty()) {
            org = orgList.get(0);
        } else {
            org = new OrganisationDetail();
        }

        org.setAddress(requestDto.getAddress());
        org.setTotalEmployees(requestDto.getTotalEmployees() != null ? requestDto.getTotalEmployees() : 0);
        org.setNumberOfTrainers(requestDto.getNumberOfTrainers() != null ? requestDto.getNumberOfTrainers() : 0);
        org.setTotalStudentsTrained(requestDto.getTotalStudentsTrained() != null ? requestDto.getTotalStudentsTrained() : 0);
        org.setTotalCoursesOffered(requestDto.getTotalCoursesOffered() != null ? requestDto.getTotalCoursesOffered() : 0);
        org.setTotalClients(requestDto.getTotalClients() != null ? requestDto.getTotalClients() : 0);
        org.setUniversityPartners(requestDto.getUniversityPartners() != null ? requestDto.getUniversityPartners() : 0);
        org.setStudentsPlaced(requestDto.getStudentsPlaced() != null ? requestDto.getStudentsPlaced() : 0);

        OrganisationDetail saved = organisationDetailRepository.save(org);
        log.info("PortfolioServiceImpl : createOrUpdateOrganisationDetails : Organisation details saved with ID: {}", saved.getId());
        return mapToOrgResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeaturedTrainerResponseDto> getAllTrainers() {
        log.info("PortfolioServiceImpl : getAllTrainers : Fetching all featured trainers");
        List<FeaturedTrainerResponseDto> trainers = featuredTrainerRepository.findAll()
                .stream().map(this::mapToTrainerResponse).collect(Collectors.toList());
        log.info("PortfolioServiceImpl : getAllTrainers : Found {} trainers", trainers.size());
        return trainers;
    }

    @Override
    @Transactional(readOnly = true)
    public FeaturedTrainerResponseDto getTrainerById(UUID id) {
        log.info("PortfolioServiceImpl : getTrainerById : Fetching trainer with ID: {}", id);
        FeaturedTrainer trainer = featuredTrainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Featured trainer not found with ID: " + id));
        log.info("PortfolioServiceImpl : getTrainerById : Trainer found: {}", trainer.getName());
        return mapToTrainerResponse(trainer);
    }

    @Override
    @Transactional
    public FeaturedTrainerResponseDto createTrainer(FeaturedTrainerRequestDto requestDto) {
        log.info("PortfolioServiceImpl : createTrainer : Adding featured trainer: {}", requestDto.getName());
        FeaturedTrainer trainer = FeaturedTrainer.builder()
                .name(requestDto.getName())
                .designation(requestDto.getDesignation())
                .experience(requestDto.getExperience())
                .skills(requestDto.getSkills())
                .description(requestDto.getDescription())
                .profileImageUrl(requestDto.getProfileImageUrl())
                .build();
        FeaturedTrainer saved = featuredTrainerRepository.save(trainer);
        log.info("PortfolioServiceImpl : createTrainer : Trainer added with ID: {}", saved.getId());
        return mapToTrainerResponse(saved);
    }

    @Override
    @Transactional
    public FeaturedTrainerResponseDto updateTrainer(UUID id, FeaturedTrainerRequestDto requestDto) {
        log.info("PortfolioServiceImpl : updateTrainer : Updating trainer with ID: {}", id);
        FeaturedTrainer trainer = featuredTrainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Featured trainer not found with ID: " + id));

        trainer.setName(requestDto.getName());
        trainer.setDesignation(requestDto.getDesignation());
        trainer.setExperience(requestDto.getExperience());
        trainer.setSkills(requestDto.getSkills());
        trainer.setDescription(requestDto.getDescription());
        trainer.setProfileImageUrl(requestDto.getProfileImageUrl());

        FeaturedTrainer updated = featuredTrainerRepository.save(trainer);
        log.info("PortfolioServiceImpl : updateTrainer : Trainer updated: {}", updated.getName());
        return mapToTrainerResponse(updated);
    }

    @Override
    @Transactional
    public void deleteTrainer(UUID id) {
        log.info("PortfolioServiceImpl : deleteTrainer : Deleting trainer with ID: {}", id);
        FeaturedTrainer trainer = featuredTrainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Featured trainer not found with ID: " + id));
        featuredTrainerRepository.delete(trainer);
        log.info("PortfolioServiceImpl : deleteTrainer : Trainer deleted successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeaturedStudentResponseDto> getAllStudents() {
        log.info("PortfolioServiceImpl : getAllStudents : Fetching all featured students");
        List<FeaturedStudentResponseDto> students = featuredStudentRepository.findAll()
                .stream().map(this::mapToStudentResponse).collect(Collectors.toList());
        log.info("PortfolioServiceImpl : getAllStudents : Found {} students", students.size());
        return students;
    }

    @Override
    @Transactional(readOnly = true)
    public FeaturedStudentResponseDto getStudentById(UUID id) {
        log.info("PortfolioServiceImpl : getStudentById : Fetching student with ID: {}", id);
        FeaturedStudent student = featuredStudentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Featured student not found with ID: " + id));
        log.info("PortfolioServiceImpl : getStudentById : Student found: {}", student.getName());
        return mapToStudentResponse(student);
    }

    @Override
    @Transactional
    public FeaturedStudentResponseDto createStudent(FeaturedStudentRequestDto requestDto) {
        log.info("PortfolioServiceImpl : createStudent : Adding featured student: {}", requestDto.getName());
        FeaturedStudent student = FeaturedStudent.builder()
                .name(requestDto.getName())
                .courseCompleted(requestDto.getCourseCompleted())
                .placementCompany(requestDto.getPlacementCompany())
                .testimonial(requestDto.getTestimonial())
                .profileImageUrl(requestDto.getProfileImageUrl())
                .build();
        FeaturedStudent saved = featuredStudentRepository.save(student);
        log.info("PortfolioServiceImpl : createStudent : Student added with ID: {}", saved.getId());
        return mapToStudentResponse(saved);
    }

    @Override
    @Transactional
    public FeaturedStudentResponseDto updateStudent(UUID id, FeaturedStudentRequestDto requestDto) {
        log.info("PortfolioServiceImpl : updateStudent : Updating student with ID: {}", id);
        FeaturedStudent student = featuredStudentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Featured student not found with ID: " + id));

        student.setName(requestDto.getName());
        student.setCourseCompleted(requestDto.getCourseCompleted());
        student.setPlacementCompany(requestDto.getPlacementCompany());
        student.setTestimonial(requestDto.getTestimonial());
        student.setProfileImageUrl(requestDto.getProfileImageUrl());

        FeaturedStudent updated = featuredStudentRepository.save(student);
        log.info("PortfolioServiceImpl : updateStudent : Student updated: {}", updated.getName());
        return mapToStudentResponse(updated);
    }

    @Override
    @Transactional
    public void deleteStudent(UUID id) {
        log.info("PortfolioServiceImpl : deleteStudent : Deleting student with ID: {}", id);
        FeaturedStudent student = featuredStudentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Featured student not found with ID: " + id));
        featuredStudentRepository.delete(student);
        log.info("PortfolioServiceImpl : deleteStudent : Student deleted successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioHighlightResponseDto> getHighlightsByType(PortfolioHighlightType type) {
        log.info("PortfolioServiceImpl : getHighlightsByType : Fetching highlights of type: {}", type);
        List<PortfolioHighlightResponseDto> highlights = portfolioHighlightRepository.findByType(type)
                .stream().map(this::mapToHighlightResponse).collect(Collectors.toList());
        log.info("PortfolioServiceImpl : getHighlightsByType : Found {} highlights", highlights.size());
        return highlights;
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioHighlightResponseDto getHighlightById(UUID id) {
        log.info("PortfolioServiceImpl : getHighlightById : Fetching highlight with ID: {}", id);
        PortfolioHighlight highlight = portfolioHighlightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio highlight not found with ID: " + id));
        log.info("PortfolioServiceImpl : getHighlightById : Highlight found: {}", highlight.getTitle());
        return mapToHighlightResponse(highlight);
    }

    @Override
    @Transactional
    public PortfolioHighlightResponseDto createHighlight(PortfolioHighlightRequestDto requestDto) {
        log.info("PortfolioServiceImpl : createHighlight : Adding highlight: {}", requestDto.getTitle());
        PortfolioHighlight highlight = PortfolioHighlight.builder()
                .type(requestDto.getType())
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .imageUrl(requestDto.getImageUrl())
                .build();
        PortfolioHighlight saved = portfolioHighlightRepository.save(highlight);
        log.info("PortfolioServiceImpl : createHighlight : Highlight added with ID: {}", saved.getId());
        return mapToHighlightResponse(saved);
    }

    @Override
    @Transactional
    public PortfolioHighlightResponseDto updateHighlight(UUID id, PortfolioHighlightRequestDto requestDto) {
        log.info("PortfolioServiceImpl : updateHighlight : Updating highlight with ID: {}", id);
        PortfolioHighlight highlight = portfolioHighlightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio highlight not found with ID: " + id));

        highlight.setType(requestDto.getType());
        highlight.setTitle(requestDto.getTitle());
        highlight.setDescription(requestDto.getDescription());
        highlight.setImageUrl(requestDto.getImageUrl());

        PortfolioHighlight updated = portfolioHighlightRepository.save(highlight);
        log.info("PortfolioServiceImpl : updateHighlight : Highlight updated: {}", updated.getTitle());
        return mapToHighlightResponse(updated);
    }

    @Override
    @Transactional
    public void deleteHighlight(UUID id) {
        log.info("PortfolioServiceImpl : deleteHighlight : Deleting highlight with ID: {}", id);
        PortfolioHighlight highlight = portfolioHighlightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio highlight not found with ID: " + id));
        portfolioHighlightRepository.delete(highlight);
        log.info("PortfolioServiceImpl : deleteHighlight : Highlight deleted successfully");
    }

    private OrganisationDetailResponseDto mapToOrgResponse(OrganisationDetail org) {
        return OrganisationDetailResponseDto.builder()
                .id(org.getId())
                .address(org.getAddress())
                .totalEmployees(org.getTotalEmployees())
                .numberOfTrainers(org.getNumberOfTrainers())
                .totalStudentsTrained(org.getTotalStudentsTrained())
                .totalCoursesOffered(org.getTotalCoursesOffered())
                .totalClients(org.getTotalClients())
                .universityPartners(org.getUniversityPartners())
                .studentsPlaced(org.getStudentsPlaced())
                .createdAt(org.getCreatedAt())
                .updatedAt(org.getUpdatedAt())
                .build();
    }

    private FeaturedTrainerResponseDto mapToTrainerResponse(FeaturedTrainer trainer) {
        return FeaturedTrainerResponseDto.builder()
                .id(trainer.getId())
                .name(trainer.getName())
                .designation(trainer.getDesignation())
                .experience(trainer.getExperience())
                .skills(trainer.getSkills())
                .description(trainer.getDescription())
                .profileImageUrl(trainer.getProfileImageUrl())
                .createdAt(trainer.getCreatedAt())
                .updatedAt(trainer.getUpdatedAt())
                .build();
    }

    private FeaturedStudentResponseDto mapToStudentResponse(FeaturedStudent student) {
        return FeaturedStudentResponseDto.builder()
                .id(student.getId())
                .name(student.getName())
                .courseCompleted(student.getCourseCompleted())
                .placementCompany(student.getPlacementCompany())
                .testimonial(student.getTestimonial())
                .profileImageUrl(student.getProfileImageUrl())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }

    private PortfolioHighlightResponseDto mapToHighlightResponse(PortfolioHighlight highlight) {
        return PortfolioHighlightResponseDto.builder()
                .id(highlight.getId())
                .type(highlight.getType())
                .title(highlight.getTitle())
                .description(highlight.getDescription())
                .imageUrl(highlight.getImageUrl())
                .createdAt(highlight.getCreatedAt())
                .updatedAt(highlight.getUpdatedAt())
                .build();
    }
}
