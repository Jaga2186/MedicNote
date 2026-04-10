# MedicNote Backend

A Spring Boot microservices backend for managing medical prescriptions, doctors, and patients. Built with a service-oriented architecture using Spring Cloud for service discovery, centralized routing, and inter-service communication.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Microservices](#microservices)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)
- [Environment Variables](#environment-variables)
- [Build](#build)

---

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Programming language |
| Spring Boot | 3.5.6 | Application framework |
| Spring Cloud | 2025.0.0 | Microservices infrastructure (Gateway, Eureka, OpenFeign) |
| Spring Security | - | Authentication and authorization with JWT |
| MySQL | 8.0+ | Relational database |
| MapStruct | - | Object mapping between DTOs and entities |
| OpenPDF | - | PDF generation for prescriptions |
| Spring Mail | - | Email functionality with attachments |
| Resilience4j | - | Circuit breaking and fault tolerance |
| SpringDoc OpenAPI | - | Interactive API documentation (Swagger UI) |
| Docker | - | Containerization and orchestration |

---

## Architecture

```
                    ┌──────────────┐
                    │  API Gateway │
                    │  (Port 8080) │
                    └──────┬───────┘
                           │
                    ┌──────┴───────┐
                    │Eureka Server │
                    │  (Port 8761) │
                    └──────┬───────┘
           ┌───────────────┼───────────────┬──────────────┐
     ┌─────┴──────┐  ┌─────┴──────┐  ┌────┴─────┐  ┌────┴─────────┐
     │  Auth      │  │  Doctor    │  │ Patient  │  │ Prescription │
     │  Service   │  │  Service   │  │ Service  │  │ Service      │
     │ (Port 8081)│  │ (Port 8082)│  │(Port 8083│  │ (Port 8084)  │
     └────────────┘  └────────────┘  └──────────┘  └──────────────┘
```

All services register with **Eureka Server** for discovery. The **API Gateway** routes external requests to the appropriate service. **Auth Service** communicates with Doctor and Patient services via OpenFeign for login and registration. **Prescription Service** fetches doctor and patient details via OpenFeign when generating prescriptions and PDFs.

---

## Microservices

| Service | Module | Port | Description |
|---------|--------|------|-------------|
| Eureka Server | `eurekaServer` | 8761 | Service discovery and registration |
| API Gateway | `apiGateway` | 8080 | Single entry point, routing, load balancing |
| Auth Service | `authService` | 8081 | Centralized authentication (login/register via Feign to Doctor/Patient services) |
| Doctor Service | `doctorService` | 8082 | Doctor CRUD operations |
| Patient Service | `patientService` | 8083 | Patient CRUD operations |
| Prescription Service | `prescriptionService` | 8084 | Prescription management, PDF generation, email sending |

---

## Features

- **CRUD Operations** -- Full create, read, update, and soft-delete for doctors, patients, and prescriptions.
- **JWT Authentication** -- Role-based access control with DOCTOR and PATIENT roles.
- **Inter-Service Communication** -- OpenFeign clients with Resilience4j circuit breakers and FallbackFactory classes for fault tolerance.
- **PDF Generation** -- Download prescriptions as professional PDF documents using OpenPDF.
- **Email Integration** -- Send prescriptions via email with PDF attachments using Spring Mail.
- **Service Discovery** -- Eureka-based service registration and discovery.
- **API Gateway** -- Centralized routing with Spring Cloud Gateway.
- **Pagination and Sorting** -- Paginated endpoints (`/page`) for all list operations with `page`, `size`, `sortBy`, and `direction` parameters.
- **Input Validation** -- Comprehensive request validation with Jakarta Validation.
- **Swagger/OpenAPI** -- Interactive API documentation at `/swagger-ui.html` for each service with `@OpenAPIDefinition`, `@Tag`, `@Operation`, and `@Schema` annotations.
- **Soft Deletes** -- Data retention with `isActive` flag instead of permanent deletion.

---

## Prerequisites

- Java 21+
- Maven 3.9+
- MySQL 8.0+
- Docker and Docker Compose (optional)

---

## Getting Started

### Option 1: Docker Compose

```bash
docker-compose up --build
```

This starts all services including MySQL. No additional configuration required.

### Option 2: Run Locally

**1. Start MySQL and create databases:**

```sql
CREATE DATABASE IF NOT EXISTS medicnote_doctor;
CREATE DATABASE IF NOT EXISTS medicnote_patient;
CREATE DATABASE IF NOT EXISTS medicnote_prescription;
```

**2. Set environment variables:**

```bash
export DOCTOR_DB_URL=jdbc:mysql://localhost:3306/medicnote_doctor?createDatabaseIfNotExist=true
export DOCTOR_DB_USERNAME=root
export DOCTOR_DB_PASSWORD=root
export PATIENT_DB_URL=jdbc:mysql://localhost:3306/medicnote_patient?createDatabaseIfNotExist=true
export PATIENT_DB_USERNAME=root
export PATIENT_DB_PASSWORD=root
export PRESCRIPTION_DB_URL=jdbc:mysql://localhost:3306/medicnote_prescription?createDatabaseIfNotExist=true
export PRESCRIPTION_DB_USERNAME=root
export PRESCRIPTION_DB_PASSWORD=root
export JWT_SECRET=MedicNoteSecretKey2024SuperSecureTokenForJWT
```

**3. Start services in order (each in a separate terminal):**

```bash
# 1. Eureka Server (start first, wait until ready)
cd eurekaServer && ../mvnw spring-boot:run

# 2. Doctor Service
cd doctorService && ../mvnw spring-boot:run

# 3. Patient Service
cd patientService && ../mvnw spring-boot:run

# 4. Auth Service (after Doctor & Patient are up)
cd authService && ../mvnw spring-boot:run

# 5. Prescription Service (after Doctor & Patient are up)
cd prescriptionService && ../mvnw spring-boot:run

# 6. API Gateway
cd apiGateway && ../mvnw spring-boot:run
```

> **Note:** Eureka Server must be started first. Auth Service and Prescription Service depend on Doctor and Patient services being registered with Eureka.

---

## API Documentation

Swagger UI is available for each service:

| Service | URL |
|---------|-----|
| Auth Service | http://localhost:8081/swagger-ui.html |
| Doctor Service | http://localhost:8082/swagger-ui.html |
| Patient Service | http://localhost:8083/swagger-ui.html |
| Prescription Service | http://localhost:8084/swagger-ui.html |

---

## API Endpoints

**33 total endpoints: 8 public, 25 protected.**

All endpoints are accessed through the API Gateway at `http://localhost:8080`.

### Authentication (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/doctor/register` | Register a doctor |
| POST | `/api/auth/doctor/login` | Doctor login |
| POST | `/api/auth/patient/register` | Register a patient |
| POST | `/api/auth/patient/login` | Patient login |
| GET | `/api/auth/validate` | Validate JWT token |

### Doctors (Protected)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/doctors` | Get all doctors |
| GET | `/api/doctors/page?page=0&size=10&sortBy=doctorId&direction=asc` | Get doctors (paginated) |
| GET | `/api/doctors/{id}` | Get doctor by ID |
| GET | `/api/doctors/specialization/{spec}` | Filter by specialization |
| PUT | `/api/doctors/{id}` | Update doctor |
| DELETE | `/api/doctors/{id}` | Soft delete doctor |

### Patients (Protected)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/patients` | Get all patients |
| GET | `/api/patients/page?page=0&size=10&sortBy=patientId&direction=asc` | Get patients (paginated) |
| GET | `/api/patients/{id}` | Get patient by ID |
| PUT | `/api/patients/{id}` | Update patient |
| DELETE | `/api/patients/{id}` | Soft delete patient |

### Prescriptions (Protected)

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| POST | `/api/prescriptions` | Create prescription | DOCTOR |
| GET | `/api/prescriptions` | Get all prescriptions | DOCTOR, PATIENT |
| GET | `/api/prescriptions/page?page=0&size=10` | Paginated list | DOCTOR, PATIENT |
| GET | `/api/prescriptions/{id}` | Get by ID | DOCTOR, PATIENT |
| GET | `/api/prescriptions/doctor/{id}` | Get by doctor | DOCTOR, PATIENT |
| GET | `/api/prescriptions/patient/{id}` | Get by patient | DOCTOR, PATIENT |
| PUT | `/api/prescriptions/{id}` | Update prescription | DOCTOR |
| PATCH | `/api/prescriptions/{id}/status` | Update status | DOCTOR |
| DELETE | `/api/prescriptions/{id}` | Soft delete | DOCTOR |
| GET | `/api/prescriptions/{id}/download` | Download PDF | DOCTOR, PATIENT |
| POST | `/api/prescriptions/{id}/email` | Email prescription | DOCTOR, PATIENT |

---

## Project Structure

```
backend/
├── eurekaServer/                    # Service Discovery
├── apiGateway/                      # API Gateway
├── authService/                     # Authentication Service
│   └── src/main/java/com/MedicNote/authService/
│       ├── controller/              # Auth endpoints
│       ├── dto/                     # Request/Response DTOs
│       ├── feign/                   # Feign clients + FallbackFactory
│       ├── security/                # JWT util, filter, config
│       └── service/                 # Business logic
├── doctorService/                   # Doctor Management
│   └── src/main/java/com/MedicNote/doctorService/
│       ├── controller/              # REST endpoints
│       ├── dto/                     # Request/Response DTOs
│       ├── entity/                  # JPA entities
│       ├── exception/               # Custom exceptions + GlobalExceptionHandler
│       ├── mapper/                  # MapStruct mappers
│       ├── repository/              # Spring Data JPA repositories
│       ├── security/                # JWT filter, config
│       └── service/                 # Business logic (interface + impl)
├── patientService/                  # Patient Management (same structure as Doctor)
├── prescriptionService/             # Prescription, PDF, Email
│   └── src/main/java/com/MedicNote/prescriptionService/
│       ├── controller/              # REST endpoints
│       ├── dto/                     # Request/Response DTOs
│       ├── entity/                  # Prescription + Medication entities
│       ├── feign/                   # Feign clients + FallbackFactory
│       ├── mapper/                  # MapStruct mappers
│       ├── repository/              # JPA repositories
│       ├── security/                # JWT filter, config
│       └── service/                 # Business logic + PDF + Email
├── docker-compose.yml               # Docker orchestration
├── init-db.sql                      # Database initialization
├── .dockerignore
└── pom.xml                          # Parent POM
```

---

## Environment Variables

| Variable | Service | Description | Example |
|----------|---------|-------------|---------|
| `DOCTOR_DB_URL` | Doctor | JDBC URL | `jdbc:mysql://localhost:3306/medicnote_doctor` |
| `DOCTOR_DB_USERNAME` | Doctor | DB username | `root` |
| `DOCTOR_DB_PASSWORD` | Doctor | DB password | `root` |
| `PATIENT_DB_URL` | Patient | JDBC URL | `jdbc:mysql://localhost:3306/medicnote_patient` |
| `PATIENT_DB_USERNAME` | Patient | DB username | `root` |
| `PATIENT_DB_PASSWORD` | Patient | DB password | `root` |
| `PRESCRIPTION_DB_URL` | Prescription | JDBC URL | `jdbc:mysql://localhost:3306/medicnote_prescription` |
| `PRESCRIPTION_DB_USERNAME` | Prescription | DB username | `root` |
| `PRESCRIPTION_DB_PASSWORD` | Prescription | DB password | `root` |
| `JWT_SECRET` | All services | JWT signing secret | `MedicNoteSecretKey2024SuperSecureTokenForJWT` |
| `MAIL_HOST` | Prescription | SMTP host | `smtp.gmail.com` |
| `MAIL_PORT` | Prescription | SMTP port | `587` |
| `MAIL_USERNAME` | Prescription | Email address | `your-email@gmail.com` |
| `MAIL_PASSWORD` | Prescription | Email app password | `your-app-password` |

---

## Build

```bash
# Build all modules
./mvnw clean package -DskipTests

# Build specific module
cd doctorService && ../mvnw clean package -DskipTests

# Run tests
./mvnw test
```
