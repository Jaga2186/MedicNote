# MedicNote - Healthcare Microservices Platform

A comprehensive healthcare management system with a Spring Boot microservices backend and a React frontend for managing doctors, patients, and prescriptions.

## Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         FRONTEND                                    │
│                   React + Vite + Tailwind CSS                       │
│                     (Port 5173)                                     │
│          /api/* requests proxied to API Gateway                     │
└────────────────────────────┬────────────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────────────┐
│                        API Gateway                                  │
│              JWT Validation + Role Authorization                    │
│                        (Port 8080)                                  │
└────────────────────────────┬────────────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────────────┐
│                       Eureka Server                                 │
│                       (Port 8761)                                   │
└──────┬─────────────┬───────────────┬────────────────┬───────────────┘
       │             │               │                │
┌──────▼─────┐ ┌─────▼──────┐ ┌─────▼──────┐ ┌──────▼─────────┐
│   Auth     │ │  Doctor    │ │  Patient   │ │  Prescription  │
│  Service   │ │  Service   │ │  Service   │ │  Service       │
│ (Port 8081)│ │ (Port 8082)│ │ (Port 8083)│ │  (Port 8084)   │
└────────────┘ └──────┬─────┘ └──────┬─────┘ └───────┬────────┘
                      │              │                │
                      └──────────────┼────────────────┘
                                     │
                              ┌──────▼──────┐
                              │    MySQL    │
                              │  (Port 3306)│
                              └─────────────┘
```

## Tech Stack

### Backend
- **Java 21** with **Spring Boot 3.5.6**
- **Spring Cloud 2025.0.0** (Gateway, Eureka, OpenFeign)
- **Spring Security** with **JWT** authentication
- **MySQL 8.0+** database
- **MapStruct** for object mapping
- **OpenPDF** for PDF generation
- **Spring Mail** for OTP and prescription email delivery
- **Resilience4j** for circuit breaking and fault tolerance
- **SpringDoc OpenAPI** for Swagger UI documentation
- **Docker** for containerization

### Frontend
- **React 19** with **Vite 8**
- **Tailwind CSS 4** (v4 with `@tailwindcss/vite` plugin)
- **React Router DOM 7** for client-side routing
- **Axios** for HTTP requests with JWT interceptor
- **React Hot Toast** for toast notifications
- **React Icons** (Feather Icons - `fi` prefix)

---

## Services

| Service | Port | Description |
|---------|------|-------------|
| Frontend | 5173 | React SPA, proxies `/api` to Gateway |
| API Gateway | 8080 | Single entry point, JWT validation, role authorization, load balancing |
| Eureka Server | 8761 | Service discovery and registration |
| Auth Service | 8081 | 2FA authentication — credential validation, OTP via email, JWT issuance |
| Doctor Service | 8082 | Doctor CRUD operations |
| Patient Service | 8083 | Patient CRUD operations |
| Prescription Service | 8084 | Prescription management, PDF generation, email sending |

---

## Features

### Backend
- **2FA OTP Login** — Credentials validated first, then a 6-digit OTP is sent to the registered email. JWT issued only after OTP verification.
- **Email or Phone Login** — Users can log in using their registered email address or phone number.
- **OTP Session Management** — Short-lived OTP sessions stored in Auth Service DB with automatic hourly cleanup.
- **CRUD Operations** — Full create, read, update, soft-delete for doctors, patients, and prescriptions.
- **JWT Authentication** — Role-based access control with DOCTOR and PATIENT roles.
- **Inter-Service Communication** — OpenFeign clients with Resilience4j circuit breakers and FallbackFactory for fault tolerance.
- **PDF Generation** — Download prescriptions as professional PDF documents using OpenPDF.
- **Email Integration** — Send OTP codes and prescriptions via email with PDF attachments using Spring Mail (Gmail SMTP).
- **Service Discovery** — Eureka-based service registration and discovery.
- **API Gateway** — Centralized routing with JWT validation filter and role-based authorization filter.
- **Pagination & Sorting** — Paginated endpoints (`/page`) with `page`, `size`, `sortBy`, `direction` parameters.
- **Input Validation** — Comprehensive request validation with Jakarta Validation.
- **Swagger/OpenAPI** — Interactive API documentation at `/swagger-ui.html` for each service.
- **Soft Deletes** — Data retention with `isActive` flag instead of permanent deletion.

### Frontend
- **2FA OTP Login** — Step 1: enter email/phone + password. Step 2: enter 6-digit OTP received on email.
- **Email or Phone Login** — Identifier field accepts both email addresses and phone numbers.
- **Role-Based Auth** — DOCTOR and PATIENT roles with JWT stored in localStorage.
- **Protected Routes** — Route guards with role-based access (some routes are DOCTOR-only).
- **Responsive Design** — Mobile-first Tailwind CSS with responsive grids.
- **Paginated Tables** — Reusable `Pagination` component across all list pages.
- **13 Pages** — Dashboard, Login, OTP Verify, Registration (Doctor/Patient), Doctor List/Detail, Patient List/Detail, Prescription List/Create/Detail, Unauthorized.
- **Edit Mode** — Owners can edit their own profiles on detail pages.
- **Dynamic Forms** — Add/remove multiple medications in the prescription create form.
- **PDF Download & Email** — Prescription actions from the detail page.
- **Toast Notifications** — Success/error feedback for all API operations.

---

## Prerequisites

- Java 21+
- Maven 3.9+
- MySQL 8.0+
- Node.js 18+
- npm 9+
- Docker & Docker Compose (optional)
- Gmail account with App Password enabled (for OTP emails)

---

## Getting Started

### Option 1: Docker Compose (Backend)

```bash
cd backend
docker-compose up --build
```

This starts all backend services including MySQL.

### Option 2: Run Backend Locally

**1. Start MySQL and create databases:**

```sql
CREATE DATABASE IF NOT EXISTS medicnote_auth;
CREATE DATABASE IF NOT EXISTS medicnote_doctor;
CREATE DATABASE IF NOT EXISTS medicnote_patient;
CREATE DATABASE IF NOT EXISTS medicnote_prescription;
```

**2. Set environment variables:**

```bash
# Auth Service DB
export AUTH_DB_URL=jdbc:mysql://localhost:3306/medicnote_auth?createDatabaseIfNotExist=true
export AUTH_DB_USERNAME=root
export AUTH_DB_PASSWORD=root

# Doctor Service DB
export DOCTOR_DB_URL=jdbc:mysql://localhost:3306/medicnote_doctor?createDatabaseIfNotExist=true
export DOCTOR_DB_USERNAME=root
export DOCTOR_DB_PASSWORD=root

# Patient Service DB
export PATIENT_DB_URL=jdbc:mysql://localhost:3306/medicnote_patient?createDatabaseIfNotExist=true
export PATIENT_DB_USERNAME=root
export PATIENT_DB_PASSWORD=root

# Prescription Service DB
export PRESCRIPTION_DB_URL=jdbc:mysql://localhost:3306/medicnote_prescription?createDatabaseIfNotExist=true
export PRESCRIPTION_DB_USERNAME=root
export PRESCRIPTION_DB_PASSWORD=root

# JWT (Base64 encoded)
export JWT_SECRET=k2x7ieCfxLcw5us4+mP+7tspXHhVfGn+kzVVheSrsT0=

# Mail (Gmail SMTP — use App Password)
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-gmail-app-password
```

> **Gmail App Password:** Google Account → Security → 2-Step Verification → App Passwords → Generate → use the 16-character password.

**3. Start services in order (each in a separate terminal):**

```bash
# 1. Eureka Server (start first, wait until ready)
cd backend/eurekaServer && ../mvnw spring-boot:run

# 2. Doctor Service
cd backend/doctorService && ../mvnw spring-boot:run

# 3. Patient Service
cd backend/patientService && ../mvnw spring-boot:run

# 4. Auth Service (after Doctor & Patient are up)
cd backend/authService && ../mvnw spring-boot:run

# 5. Prescription Service (after Doctor & Patient are up)
cd backend/prescriptionService && ../mvnw spring-boot:run

# 6. API Gateway (start last)
cd backend/apiGateway && ../mvnw spring-boot:run
```

> **Note:** Eureka Server must be started first. Auth Service and Prescription Service depend on Doctor and Patient services being registered with Eureka.

### Option 3: Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at **http://localhost:5173** and proxies all `/api/*` requests to `http://localhost:8080` (API Gateway).

---

## Login Flow

MedicNote uses a **2-step 2FA login**. Password alone does not grant access — an OTP must be verified first.

### Step 1 — Submit credentials

```
POST /api/auth/doctor/login   or   POST /api/auth/patient/login

Body:
{
  "identifier": "john@hospital.com",   ← email OR phone number
  "password": "Pass@123"
}

Response:
{
  "sessionToken": "cf98d908-04c1-4295-89a6-78a3b7aedc47",
  "message": "OTP sent to your registered email j***n@hospital.com"
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
> - Use the JWT `token` as `Authorization: Bearer <token>` for all protected endpoints

---

## Access Points

| What | URL |
|------|-----|
| Frontend | http://localhost:5173 |
| API Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |
| Auth Swagger | http://localhost:8081/swagger-ui.html |
| Doctor Swagger | http://localhost:8082/swagger-ui.html |
| Patient Swagger | http://localhost:8083/swagger-ui.html |
| Prescription Swagger | http://localhost:8084/swagger-ui.html |

---

## API Endpoints

All endpoints are accessed through the API Gateway at `http://localhost:8080`.

### Authentication (Public — no JWT required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/doctor/register` | Register a new doctor |
| POST | `/api/auth/doctor/login` | Step 1 — validate credentials, send OTP to email |
| POST | `/api/auth/patient/register` | Register a new patient |
| POST | `/api/auth/patient/login` | Step 1 — validate credentials, send OTP to email |
| POST | `/api/auth/otp/verify` | Step 2 — verify OTP, receive JWT token |
| GET | `/api/auth/validate` | Validate an existing JWT token |

### Doctors (Protected — JWT required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/doctors` | Get all doctors |
| GET | `/api/doctors/page?page=0&size=10&sortBy=doctorId&direction=asc` | Get doctors (paginated) |
| GET | `/api/doctors/{id}` | Get doctor by ID |
| GET | `/api/doctors/specialization/{spec}` | Filter by specialization |
| GET | `/api/doctors/by-email/{email}` | Get doctor by email |
| GET | `/api/doctors/by-phone/{phone}` | Get doctor by phone number |
| PUT | `/api/doctors/{id}` | Update doctor |
| DELETE | `/api/doctors/{id}` | Soft delete doctor |

### Patients (Protected — JWT required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/patients` | Get all patients |
| GET | `/api/patients/page?page=0&size=10&sortBy=patientId&direction=asc` | Get patients (paginated) |
| GET | `/api/patients/{id}` | Get patient by ID |
| GET | `/api/patients/by-email/{email}` | Get patient by email |
| GET | `/api/patients/by-phone/{phone}` | Get patient by phone number |
| PUT | `/api/patients/{id}` | Update patient |
| DELETE | `/api/patients/{id}` | Soft delete patient |

### Prescriptions (Protected — JWT required)

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| POST | `/api/prescriptions` | Create prescription | DOCTOR |
| GET | `/api/prescriptions` | Get all prescriptions | DOCTOR, PATIENT |
| GET | `/api/prescriptions/page?page=0&size=10` | Paginated list | DOCTOR, PATIENT |
| GET | `/api/prescriptions/{id}` | Get by ID | DOCTOR, PATIENT |
| GET | `/api/prescriptions/doctor/{doctorId}` | Get by doctor | DOCTOR, PATIENT |
| GET | `/api/prescriptions/patient/{patientId}` | Get by patient | DOCTOR, PATIENT |
| GET | `/api/prescriptions/status/{status}` | Get by status | DOCTOR, PATIENT |
| PUT | `/api/prescriptions/{id}` | Update prescription | DOCTOR |
| PATCH | `/api/prescriptions/{id}/status` | Update status | DOCTOR |
| DELETE | `/api/prescriptions/{id}` | Soft delete | DOCTOR |
| GET | `/api/prescriptions/{id}/download` | Download PDF | DOCTOR, PATIENT |
| POST | `/api/prescriptions/{id}/email` | Email prescription PDF | DOCTOR |

---

## Frontend Pages & Routes

| Page | Route | Access | Description |
|------|-------|--------|-------------|
| Login | `/login` | Public | Step 1 of 2FA — Doctor/Patient tabs, email or phone + password |
| Verify OTP | `/verify-otp` | Public | Step 2 of 2FA — 6-digit OTP input with auto-focus and paste support |
| Doctor Register | `/register/doctor` | Public | 8-field registration form |
| Patient Register | `/register/patient` | Public | 4-section form (personal, address, emergency contact, medical) |
| Unauthorized | `/unauthorized` | Public | Access denied page |
| Dashboard | `/dashboard` | Protected | Stats cards and recent prescriptions |
| Doctor List | `/doctors` | Protected | Paginated table with specialization filter |
| Doctor Detail | `/doctors/:id` | Protected | View/edit doctor profile (owner only) |
| Patient List | `/patients` | DOCTOR only | Paginated patient table |
| Patient Detail | `/patients/:id` | Protected | 4-card layout, owners can edit own profile |
| Prescription List | `/prescriptions` | Protected | Role-based data with "View All" toggle |
| Prescription Create | `/prescriptions/create` | DOCTOR only | Dynamic medication form with patient dropdown |
| Prescription Detail | `/prescriptions/:id` | Protected | PDF download, email, mark as completed |

---

## Project Structure

```
MedicNote/
├── backend/
│   ├── eurekaServer/                    # Service Discovery (Port 8761)
│   ├── apiGateway/                      # API Gateway (Port 8080)
│   │   └── src/main/java/com/MedicNote/apiGateway/
│   │       ├── config/                  # CorsConfig, SecurityConfig
│   │       └── security/               # JwtAuthenticationFilter, RoleAuthorizationFilter, JwtUtility
│   ├── authService/                     # Auth Service (Port 8081)
│   │   └── src/main/java/com/MedicNote/authService/
│   │       ├── controller/              # AuthController (register, login, otp/verify)
│   │       ├── dto/                     # LoginRequestDTO, OtpVerifyRequestDTO, AuthResponseDTO
│   │       ├── entity/                  # OtpRecord
│   │       ├── exception/               # DownstreamServiceException, GlobalExceptionHandler
│   │       ├── feign/                   # DoctorServiceClient, PatientServiceClient + Fallbacks
│   │       ├── repository/              # OtpRepository
│   │       ├── config/                  # FeignConfig, FeignErrorDecoder, SecurityConfig
│   │       ├── security/               # JwtAuthenticationFilter, JwtUtility
│   │       └── service/                # OtpService, EmailService
│   ├── doctorService/                   # Doctor Service (Port 8082)
│   │   └── src/main/java/com/MedicNote/doctorService/
│   │       ├── controller/              # DoctorController (CRUD + by-email + by-phone)
│   │       ├── dto/                     # DoctorRequestDTO, DoctorResponseDTO, LoginRequestDTO
│   │       ├── entity/                  # Doctor entity
│   │       ├── exception/               # Custom exceptions + GlobalExceptionHandler
│   │       ├── mapper/                  # DTOMapper (MapStruct)
│   │       ├── repository/              # DoctorRepository
│   │       ├── security/               # JwtAuthenticationFilter, SecurityConfig
│   │       └── service/                # DoctorService interface + implementation
│   ├── patientService/                  # Patient Service (Port 8083)
│   │   └── src/main/java/com/MedicNote/patientService/
│   │       ├── controller/              # PatientController (CRUD + by-email + by-phone)
│   │       ├── dto/                     # PatientRequestDTO, PatientResponseDTO, LoginRequestDTO
│   │       ├── entity/                  # Patient, Address, EmergencyContact, MedicalInfo, Gender
│   │       ├── exception/               # Custom exceptions + GlobalExceptionHandler
│   │       ├── mapper/                  # DTOMapper (MapStruct)
│   │       ├── repository/              # PatientRepository
│   │       ├── security/               # JwtAuthenticationFilter, SecurityConfig
│   │       └── service/                # PatientService interface + implementation
│   ├── prescriptionService/             # Prescription Service (Port 8084)
│   │   └── src/main/java/com/MedicNote/prescriptionService/
│   │       ├── controller/              # PrescriptionController
│   │       ├── dto/                     # Request/Response DTOs
│   │       ├── entity/                  # Prescription + Medication entities
│   │       ├── feign/                   # DoctorServiceClient, PatientServiceClient + Fallbacks
│   │       ├── mapper/                  # DTOMapper (MapStruct)
│   │       ├── repository/              # PrescriptionRepository
│   │       ├── security/               # JwtAuthenticationFilter, SecurityConfig
│   │       └── service/                # PrescriptionService + PDF + Email
│   ├── docker-compose.yml               # Docker orchestration
│   ├── init-db.sql                      # Database initialization
│   ├── .dockerignore
│   └── pom.xml                          # Parent POM
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   │   ├── axios.js                 # Axios instance + JWT interceptor + 401 redirect
│   │   │   └── services.js              # All API service functions
│   │   ├── components/
│   │   │   ├── Navbar.jsx               # Role-aware top navigation
│   │   │   ├── Pagination.jsx           # Reusable pagination component
│   │   │   └── ProtectedRoute.jsx       # Auth + role guard
│   │   ├── context/
│   │   │   └── AuthContext.jsx          # Auth state, login(), logout()
│   │   ├── pages/
│   │   │   ├── auth/
│   │   │   │   ├── Login.jsx            # Step 1 — identifier + password
│   │   │   │   ├── VerifyOtp.jsx        # Step 2 — 6-digit OTP input
│   │   │   │   ├── DoctorRegister.jsx   # Doctor registration form
│   │   │   │   └── PatientRegister.jsx  # Patient registration form
│   │   │   ├── doctor/
│   │   │   │   ├── DoctorList.jsx       # Paginated doctor table
│   │   │   │   └── DoctorDetail.jsx     # Doctor profile + edit
│   │   │   ├── patient/
│   │   │   │   ├── PatientList.jsx      # Paginated patient table
│   │   │   │   └── PatientDetail.jsx    # Patient profile + edit
│   │   │   ├── prescription/
│   │   │   │   ├── PrescriptionList.jsx   # Prescription table
│   │   │   │   ├── PrescriptionCreate.jsx # Create prescription
│   │   │   │   └── PrescriptionDetail.jsx # View + actions
│   │   │   ├── Dashboard.jsx            # Main dashboard
│   │   │   └── Unauthorized.jsx         # 403 page
│   │   ├── App.jsx                      # Router + layout
│   │   ├── index.css                    # Tailwind CSS import
│   │   └── main.jsx                     # Entry point
│   ├── vite.config.js                   # Vite + proxy to gateway
│   └── package.json
└── README.md
```

---

## Environment Variables

### Backend

| Variable | Service | Description | Example |
|----------|---------|-------------|---------|
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
| `MAIL_USERNAME` | Auth | Gmail address for OTP emails | `your-email@gmail.com` |
| `MAIL_PASSWORD` | Auth | Gmail App Password (16 chars) | `xxxx xxxx xxxx xxxx` |
| `OTP_EXPIRY_MINUTES` | Auth | OTP code validity | `5` |
| `OTP_SESSION_EXPIRY_MINUTES` | Auth | Login session validity | `10` |
| `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE` | All | Eureka server URL | `http://localhost:8761/eureka/` |

### Frontend

No environment variables required for development. The Vite proxy in `vite.config.js` forwards all `/api/*` requests to `http://localhost:8080` automatically.

---

## Build

```bash
# Build all backend modules
cd backend && ./mvnw clean package -DskipTests

# Build a specific backend module
cd backend/doctorService && ../mvnw clean package -DskipTests

# Build frontend for production
cd frontend && npm run build
```

---

## Documentation

For detailed documentation on each part, see:
- [Backend README](./backend/README.md) — Detailed backend architecture, services, and API docs
- [Frontend README](./frontend/README.md) — Detailed frontend setup, pages, components, and config