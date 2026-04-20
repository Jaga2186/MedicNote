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
- [Login Flow](#login-flow)
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
| Spring Mail | - | Email functionality for OTP delivery and prescription attachments |
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

All services register with **Eureka Server** for discovery. The **API Gateway** routes external requests to the appropriate service and validates JWT tokens. **Auth Service** communicates with Doctor and Patient services via OpenFeign for credential validation, OTP generation, and registration. **Prescription Service** fetches doctor and patient details via OpenFeign when generating prescriptions and PDFs.

---

## Microservices

| Service | Module | Port | Description |
|---------|--------|------|-------------|
| Eureka Server | `eurekaServer` | 8761 | Service discovery and registration |
| API Gateway | `apiGateway` | 8080 | Single entry point, routing, load balancing, JWT validation |
| Auth Service | `authService` | 8081 | Centralized 2FA authentication — credential validation, OTP via email, JWT issuance |
| Doctor Service | `doctorService` | 8082 | Doctor CRUD operations |
| Patient Service | `patientService` | 8083 | Patient CRUD operations |
| Prescription Service | `prescriptionService` | 8084 | Prescription management, PDF generation, email sending |

---

## Features

- **2FA OTP Login** — Two-factor authentication: credentials are validated first, then a 6-digit OTP is sent to the registered email. JWT is issued only after OTP verification.
- **Email or Phone Login** — Users can log in using either their registered email address or phone number along with their password.
- **OTP Session Management** — Short-lived OTP sessions stored in Auth Service DB with automatic hourly cleanup via scheduled job.
- **CRUD Operations** — Full create, read, update, and soft-delete for doctors, patients, and prescriptions.
- **JWT Authentication** — Role-based access control with DOCTOR and PATIENT roles.
- **Inter-Service Communication** — OpenFeign clients with Resilience4j circuit breakers and FallbackFactory classes for fault tolerance.
- **PDF Generation** — Download prescriptions as professional PDF documents using OpenPDF.
- **Email Integration** — Send OTP codes and prescriptions via email with PDF attachments using Spring Mail (Gmail SMTP).
- **Service Discovery** — Eureka-based service registration and discovery.
- **API Gateway** — Centralized routing with Spring Cloud Gateway, JWT validation filter, and role-based authorization filter.
- **Pagination and Sorting** — Paginated endpoints (`/page`) for all list operations with `page`, `size`, `sortBy`, and `direction` parameters.
- **Input Validation** — Comprehensive request validation with Jakarta Validation.
- **Swagger/OpenAPI** — Interactive API documentation at `/swagger-ui.html` for each service.
- **Soft Deletes** — Data retention with `isActive` flag instead of permanent deletion.

---

## Prerequisites

- Java 21+
- Maven 3.9+
- MySQL 8.0+
- Docker and Docker Compose (optional)
- Gmail account with App Password enabled (for OTP emails)

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
CREATE DATABASE IF NOT EXISTS medicnote_auth;
CREATE DATABASE IF NOT EXISTS medicnote_doctor;
CREATE DATABASE IF NOT EXISTS medicnote_patient;
CREATE DATABASE IF NOT EXISTS medicnote_prescription;
```

**2. Set environment variables:**

```bash
export AUTH_DB_URL=jdbc:mysql://localhost:3306/medicnote_auth?createDatabaseIfNotExist=true
export AUTH_DB_USERNAME=root
export AUTH_DB_PASSWORD=root
export DOCTOR_DB_URL=jdbc:mysql://localhost:3306/medicnote_doctor?createDatabaseIfNotExist=true
export DOCTOR_DB_USERNAME=root
export DOCTOR_DB_PASSWORD=root
export PATIENT_DB_URL=jdbc:mysql://localhost:3306/medicnote_patient?createDatabaseIfNotExist=true
export PATIENT_DB_USERNAME=root
export PATIENT_DB_PASSWORD=root
export PRESCRIPTION_DB_URL=jdbc:mysql://localhost:3306/medicnote_prescription?createDatabaseIfNotExist=true
export PRESCRIPTION_DB_USERNAME=root
export PRESCRIPTION_DB_PASSWORD=root
export JWT_SECRET=k2x7ieCfxLcw5us4+mP+7tspXHhVfGn+kzVVheSrsT0=
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-gmail-app-password
```

> **Gmail App Password setup:** Google Account → Security → 2-Step Verification → App Passwords → Generate → use the 16-character password as `MAIL_PASSWORD`.

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

# 6. API Gateway (start last)
cd apiGateway && ../mvnw spring-boot:run
```

> **Note:** Eureka Server must be started first. Auth Service and Prescription Service depend on Doctor and Patient services being registered with Eureka before they can communicate via OpenFeign.

---

## Login Flow

MedicNote uses a **2-step 2FA login flow**. There is no direct JWT issuance on password alone — an OTP must be verified first.

### Step 1 — Submit credentials

```
POST /api/auth/doctor/login
     or
POST /api/auth/patient/login

Body:
{
  "identifier": "john@hospital.com",   ← email OR phone number
  "password": "Pass@123"
}

Response:
{
  "message": "OTP sent to your registered email j***n@hospital.com",
  "sessionToken": "cf98d908-04c1-4295-89a6-78a3b7aedc47"
}
```

### Step 2 — Verify OTP

```
POST /api/auth/otp/verify

Body:
{
  "sessionToken": "cf98d908-04c1-4295-89a6-78a3b7aedc47",
  "otpCode": "483921"
}

Response:
{
  "message": "Doctor login successful",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "DOCTOR",
  "data": { ...user details }
}
```

> - OTP is valid for **5 minutes**
> - Session expires after **10 minutes**
> - Use the JWT `token` in the `Authorization: Bearer <token>` header for all protected endpoints

---

## API Documentation

Swagger UI is available for each service directly (bypassing the gateway):

| Service | URL |
|---------|-----|
| Auth Service | http://localhost:8081/swagger-ui.html |
| Doctor Service | http://localhost:8082/swagger-ui.html |
| Patient Service | http://localhost:8083/swagger-ui.html |
| Prescription Service | http://localhost:8084/swagger-ui.html |

---

## API Endpoints

All endpoints are accessed through the API Gateway at `http://localhost:8080`.

### Authentication (Public — no JWT required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/doctor/register` | Register a new doctor |
| POST | `/api/auth/doctor/login` | Step 1 — validate credentials, send OTP to registered email |
| POST | `/api/auth/patient/register` | Register a new patient |
| POST | `/api/auth/patient/login` | Step 1 — validate credentials, send OTP to registered email |
| POST | `/api/auth/otp/verify` | Step 2 — verify OTP, receive JWT token |
| GET | `/api/auth/validate` | Validate an existing JWT token |

### Doctors (Protected — JWT required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/doctors` | Get all doctors |
| GET | `/api/doctors/page?page=0&size=10&sortBy=doctorId&direction=asc` | Get doctors (paginated) |
| GET | `/api/doctors/{id}` | Get doctor by ID |
| GET | `/api/doctors/specialization/{spec}` | Filter doctors by specialization |
| GET | `/api/doctors/by-email/{email}` | Get doctor by email |
| GET | `/api/doctors/by-phone/{phone}` | Get doctor by phone number |
| PUT | `/api/doctors/{id}` | Update doctor details |
| DELETE | `/api/doctors/{id}` | Soft delete doctor |

### Patients (Protected — JWT required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/patients` | Get all patients |
| GET | `/api/patients/page?page=0&size=10&sortBy=patientId&direction=asc` | Get patients (paginated) |
| GET | `/api/patients/{id}` | Get patient by ID |
| GET | `/api/patients/by-email/{email}` | Get patient by email |
| GET | `/api/patients/by-phone/{phone}` | Get patient by phone number |
| PUT | `/api/patients/{id}` | Update patient details |
| DELETE | `/api/patients/{id}` | Soft delete patient |

### Prescriptions (Protected — JWT required)

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| POST | `/api/prescriptions` | Create a new prescription | DOCTOR |
| GET | `/api/prescriptions` | Get all prescriptions | DOCTOR, PATIENT |
| GET | `/api/prescriptions/page?page=0&size=10` | Get prescriptions (paginated) | DOCTOR, PATIENT |
| GET | `/api/prescriptions/{id}` | Get prescription by ID | DOCTOR, PATIENT |
| GET | `/api/prescriptions/doctor/{doctorId}` | Get prescriptions by doctor | DOCTOR, PATIENT |
| GET | `/api/prescriptions/patient/{patientId}` | Get prescriptions by patient | DOCTOR, PATIENT |
| GET | `/api/prescriptions/status/{status}` | Get prescriptions by status | DOCTOR, PATIENT |
| PUT | `/api/prescriptions/{id}` | Update prescription | DOCTOR |
| PATCH | `/api/prescriptions/{id}/status` | Update prescription status | DOCTOR |
| DELETE | `/api/prescriptions/{id}` | Soft delete prescription | DOCTOR |
| GET | `/api/prescriptions/{id}/download` | Download prescription as PDF | DOCTOR, PATIENT |
| POST | `/api/prescriptions/{id}/email` | Email prescription PDF | DOCTOR |

---

## Project Structure

```
backend/
├── eurekaServer/                        # Service Discovery
├── apiGateway/                          # API Gateway
│   └── src/main/java/com/MedicNote/apiGateway/
│       ├── config/                      # CorsConfig, SecurityConfig
│       └── security/                    # JwtAuthenticationFilter, RoleAuthorizationFilter, JwtUtility
├── authService/                         # Authentication Service
│   └── src/main/java/com/MedicNote/authService/
│       ├── controller/                  # AuthController (register, login, otp/verify)
│       ├── dto/                         # LoginRequestDTO, OtpVerifyRequestDTO, AuthResponseDTO
│       ├── entity/                      # OtpRecord (session + OTP storage)
│       ├── exception/                   # DownstreamServiceException, GlobalExceptionHandler
│       ├── feign/                       # DoctorServiceClient, PatientServiceClient + Fallbacks
│       ├── repository/                  # OtpRepository
│       ├── config/                      # FeignConfig, FeignErrorDecoder, SecurityConfig
│       ├── security/                    # JwtAuthenticationFilter, JwtUtility
│       └── service/                     # OtpService, EmailService
├── doctorService/                       # Doctor Management
│   └── src/main/java/com/MedicNote/doctorService/
│       ├── controller/                  # DoctorController (CRUD + by-email + by-phone)
│       ├── dto/                         # DoctorRequestDTO, DoctorResponseDTO, LoginRequestDTO
│       ├── entity/                      # Doctor entity
│       ├── exception/                   # Custom exceptions + GlobalExceptionHandler
│       ├── mapper/                      # DTOMapper (MapStruct)
│       ├── repository/                  # DoctorRepository
│       ├── security/                    # JwtAuthenticationFilter, SecurityConfig
│       └── service/                     # DoctorService interface + DoctorServiceImplementation
├── patientService/                      # Patient Management (same structure as Doctor)
│   └── src/main/java/com/MedicNote/patientService/
│       ├── controller/                  # PatientController (CRUD + by-email + by-phone)
│       ├── dto/                         # PatientRequestDTO, PatientResponseDTO, LoginRequestDTO
│       ├── entity/                      # Patient, Address, EmergencyContact, MedicalInfo, Gender
│       ├── exception/                   # Custom exceptions + GlobalExceptionHandler
│       ├── mapper/                      # DTOMapper (MapStruct)
│       ├── repository/                  # PatientRepository
│       ├── security/                    # JwtAuthenticationFilter, SecurityConfig
│       └── service/                     # PatientService interface + PatientServiceImplementation
├── prescriptionService/                 # Prescription, PDF, Email
│   └── src/main/java/com/MedicNote/prescriptionService/
│       ├── controller/                  # PrescriptionController
│       ├── dto/                         # Request/Response DTOs
│       ├── entity/                      # Prescription + Medication entities
│       ├── feign/                       # DoctorServiceClient, PatientServiceClient + Fallbacks
│       ├── mapper/                      # DTOMapper (MapStruct)
│       ├── repository/                  # PrescriptionRepository
│       ├── security/                    # JwtAuthenticationFilter, SecurityConfig
│       └── service/                     # PrescriptionService + PDF generation + Email
├── docker-compose.yml                   # Docker orchestration
├── init-db.sql                          # Database initialization
├── .dockerignore
└── pom.xml                              # Parent POM
```

---

## Environment Variables

| Variable | Service | Description | Default / Example |
|----------|---------|-------------|-------------------|
| `AUTH_DB_URL` | Auth | JDBC URL for auth DB | `jdbc:mysql://localhost:3306/medicnote_auth` |
| `AUTH_DB_USERNAME` | Auth | DB username | `root` |
| `AUTH_DB_PASSWORD` | Auth | DB password | `root` |
| `DOCTOR_DB_URL` | Doctor | JDBC URL for doctor DB | `jdbc:mysql://localhost:3306/medicnote_doctor` |
| `DOCTOR_DB_USERNAME` | Doctor | DB username | `root` |
| `DOCTOR_DB_PASSWORD` | Doctor | DB password | `root` |
| `PATIENT_DB_URL` | Patient | JDBC URL for patient DB | `jdbc:mysql://localhost:3306/medicnote_patient` |
| `PATIENT_DB_USERNAME` | Patient | DB username | `root` |
| `PATIENT_DB_PASSWORD` | Patient | DB password | `root` |
| `PRESCRIPTION_DB_URL` | Prescription | JDBC URL for prescription DB | `jdbc:mysql://localhost:3306/medicnote_prescription` |
| `PRESCRIPTION_DB_USERNAME` | Prescription | DB username | `root` |
| `PRESCRIPTION_DB_PASSWORD` | Prescription | DB password | `root` |
| `JWT_SECRET` | All services | Base64-encoded JWT signing secret | `k2x7ieCfxLcw5us4+mP+7tspXHhVfGn+kzVVheSrsT0=` |
| `JWT_EXPIRATION` | Auth | JWT expiry in milliseconds | `86400000` (24 hours) |
| `MAIL_USERNAME` | Auth | Gmail address for sending OTP emails | `your-email@gmail.com` |
| `MAIL_PASSWORD` | Auth | Gmail App Password (16 characters) | `xxxx xxxx xxxx xxxx` |
| `OTP_EXPIRY_MINUTES` | Auth | OTP code validity duration | `5` |
| `OTP_SESSION_EXPIRY_MINUTES` | Auth | Login session validity duration | `10` |
| `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE` | All | Eureka server URL | `http://localhost:8761/eureka/` |
| `EUREKA_INSTANCE_HOSTNAME` | All | Instance hostname | `localhost` |
| `EUREKA_PREFER_IP_ADDRESS` | All | Use IP instead of hostname | `false` |

---

## Build

```bash
# Build all modules
./mvnw clean package -DskipTests

# Build a specific module
cd doctorService && ../mvnw clean package -DskipTests

# Run tests
./mvnw test
```