# Portfolio Module — Curriculum Service

## Overview

The Portfolio Module provides APIs to manage and display Lamicons' public-facing portfolio. It showcases organisation details, featured trainers, featured students, achievements, major clients, and university partners.

- **All GET APIs are public** — no authentication required.
- **All write APIs (POST, PUT, DELETE) require ADMIN role** — validated via `X-USER-ID` and `X-USER-ROLE` headers forwarded by the API Gateway.

Base URL: `/api/v1/portfolio`

---

## Database Tables

| Table | Purpose |
|---|---|
| `organisation_detail` | Singleton row storing org-level stats (address, employee count, etc.) |
| `featured_trainer` | List of trainers showcased in the portfolio |
| `featured_student` | List of students showcased in the portfolio |
| `portfolio_highlight` | Achievements, major clients, and university partners (distinguished by `type` column) |

All tables are auto-created by JPA (`ddl-auto: update`).

---

## Project Structure

```
Entity/Portfolio/
├── OrganisationDetail.java
├── FeaturedTrainer.java
├── FeaturedStudent.java
└── PortfolioHighlight.java

DTO/Portfolio/
├── OrganisationDetailRequestDto.java
├── OrganisationDetailResponseDto.java
├── FeaturedTrainerRequestDto.java
├── FeaturedTrainerResponseDto.java
├── FeaturedStudentRequestDto.java
├── FeaturedStudentResponseDto.java
├── PortfolioHighlightRequestDto.java
├── PortfolioHighlightResponseDto.java
├── PortfolioHighlightType.java          (enum: ACHIEVEMENT, MAJOR_CLIENT, UNIVERSITY_PARTNER)
└── PortfolioSummaryResponseDto.java     (aggregated response for full portfolio)

Repository/
├── OrganisationDetailRepository.java
├── FeaturedTrainerRepository.java
├── FeaturedStudentRepository.java
└── PortfolioHighlightRepository.java

Service/
├── PortfolioService.java                (interface)
└── impl/PortfolioServiceImpl.java       (implementation)

Controller/
└── PortfolioController.java
```

---

## API Endpoints

### Full Portfolio (Public)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/portfolio` | Returns the entire portfolio in one response |

Response includes: organisation details, featured trainers, featured students, achievements, major clients, and university partners.

---

### Organisation Details

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/portfolio/organisation` | Public | Get organisation details |
| PUT | `/api/v1/portfolio/organisation` | ADMIN | Create or update organisation details |

Only one organisation record exists (singleton). PUT will create it on first call and update it on subsequent calls.

---

### Featured Trainers

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/portfolio/trainers` | Public | List all featured trainers |
| GET | `/api/v1/portfolio/trainers/{id}` | Public | Get a trainer by ID |
| POST | `/api/v1/portfolio/trainers` | ADMIN | Add a new trainer |
| PUT | `/api/v1/portfolio/trainers/{id}` | ADMIN | Update a trainer |
| DELETE | `/api/v1/portfolio/trainers/{id}` | ADMIN | Delete a trainer |

---

### Featured Students

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/portfolio/students` | Public | List all featured students |
| GET | `/api/v1/portfolio/students/{id}` | Public | Get a student by ID |
| POST | `/api/v1/portfolio/students` | ADMIN | Add a new student |
| PUT | `/api/v1/portfolio/students/{id}` | ADMIN | Update a student |
| DELETE | `/api/v1/portfolio/students/{id}` | ADMIN | Delete a student |

---

### Portfolio Highlights (Achievements, Clients, University Partners)

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/portfolio/highlights?type=ACHIEVEMENT` | Public | List achievements |
| GET | `/api/v1/portfolio/highlights?type=MAJOR_CLIENT` | Public | List major clients |
| GET | `/api/v1/portfolio/highlights?type=UNIVERSITY_PARTNER` | Public | List university partners |
| GET | `/api/v1/portfolio/highlights/{id}` | Public | Get a highlight by ID |
| POST | `/api/v1/portfolio/highlights` | ADMIN | Add a new highlight |
| PUT | `/api/v1/portfolio/highlights/{id}` | ADMIN | Update a highlight |
| DELETE | `/api/v1/portfolio/highlights/{id}` | ADMIN | Delete a highlight |

Highlights use a single table with a `type` column. The `type` field in the request body determines the category:
- `ACHIEVEMENT` — Awards and recognitions
- `MAJOR_CLIENT` — Notable clients
- `UNIVERSITY_PARTNER` — Partner universities

---

## Request/Response Examples

### Create Organisation Details

```
PUT /api/v1/portfolio/organisation
Headers: X-USER-ID: <uuid>, X-USER-ROLE: ADMIN

{
  "address": "123 Tech Park, Bangalore",
  "totalEmployees": 50,
  "numberOfTrainers": 15,
  "totalStudentsTrained": 5000,
  "totalCoursesOffered": 30,
  "totalClients": 20,
  "universityPartners": 10,
  "studentsPlaced": 3500
}
```

### Add a Featured Trainer

```
POST /api/v1/portfolio/trainers
Headers: X-USER-ID: <uuid>, X-USER-ROLE: ADMIN

{
  "name": "John Doe",
  "designation": "Senior Instructor",
  "experience": "10 years",
  "skills": "Java, Spring Boot, Microservices",
  "description": "Expert in backend development and system design.",
  "profileImageUrl": "https://example.com/john.jpg"
}
```

### Add a Featured Student

```
POST /api/v1/portfolio/students
Headers: X-USER-ID: <uuid>, X-USER-ROLE: ADMIN

{
  "name": "Jane Smith",
  "courseCompleted": "Full Stack Development",
  "placementCompany": "Google",
  "testimonial": "The training helped me land my dream job.",
  "profileImageUrl": "https://example.com/jane.jpg"
}
```

### Add an Achievement

```
POST /api/v1/portfolio/highlights
Headers: X-USER-ID: <uuid>, X-USER-ROLE: ADMIN

{
  "type": "ACHIEVEMENT",
  "title": "Best Training Provider 2025",
  "description": "Awarded by National Skill Development Council",
  "imageUrl": "https://example.com/award.png"
}
```

### Get Full Portfolio (Public)

```
GET /api/v1/portfolio

Response:
{
  "success": true,
  "message": "Portfolio retrieved successfully",
  "data": {
    "organisationDetails": { ... },
    "featuredTrainers": [ ... ],
    "featuredStudents": [ ... ],
    "achievements": [ ... ],
    "majorClients": [ ... ],
    "universityPartners": [ ... ]
  }
}
```

---

## Error Handling

Handled by the existing `GlobalExceptionHandler`:

| Scenario | HTTP Status | Exception |
|---|---|---|
| Resource not found | 404 | `ResourceNotFoundException` |
| Validation failure | 400 | `MethodArgumentNotValidException` |
| Non-admin access to write API | 403 | `UnauthorizedException` |
| Unexpected error | 500 | `Exception` |

---

## Swagger

All endpoints are documented and testable at:

```
http://localhost:8082/swagger-ui.html
```

Look for the **"Portfolio Management"** tag.
