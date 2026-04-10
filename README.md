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
- **MySQL** database
- **MapStruct** for object mapping
- **OpenPDF** for PDF generation
- **Spring Mail** for email functionality
- **Resilience4j** for circuit breaking
- **SpringDoc OpenAPI** for API documentation
- **Docker** for containerization

### Frontend
- **React 19** with **Vite 8**
- **Tailwind CSS 4** (v4 with @tailwindcss/vite plugin)
- **React Router DOM 7** for routing
- **Axios** for HTTP requests with JWT interceptor
- **React Hot Toast** for notifications
- **React Icons** (Feather Icons)

## Services

| Service | Port | Description |
|---------|------|-------------|
| Frontend | 5173 | React SPA, proxies /api to Gateway |
| API Gateway | 8080 | Single entry point, routing, load balancing |
| Eureka Server | 8761 | Service discovery and registration |
| Auth Service | 8081 | Centralized authentication (login/register) |
| Doctor Service | 8082 | Doctor CRUD operations |
| Patient Service | 8083 | Patient CRUD operations |
| Prescription Service | 8084 | Prescription management, PDF, Email |

## Features

### Backend
- **CRUD Operations**: Full create, read, update, soft-delete for doctors, patients, and prescriptions
- **JWT Authentication**: Role-based access control (DOCTOR / PATIENT roles)
- **Inter-Service Communication**: OpenFeign clients with Resilience4j circuit breakers
- **PDF Generation**: Download prescriptions as professional PDF documents
- **Email Integration**: Send prescriptions via email with PDF attachments
- **Service Discovery**: Eureka-based service registration and discovery
- **API Gateway**: Centralized routing with Spring Cloud Gateway
- **Pagination & Sorting**: Paginated endpoints with page, size, sortBy, direction params
- **Input Validation**: Comprehensive request validation with Jakarta Validation
- **Swagger/OpenAPI**: Interactive API documentation at `/swagger-ui.html`
- **Soft Deletes**: Data retention with isActive flag

### Frontend
- **Role-Based Auth**: Login as Doctor or Patient, JWT stored in localStorage
- **Protected Routes**: Route guards with role-based access (some routes DOCTOR-only)
- **Responsive Design**: Mobile-first Tailwind CSS with responsive grids and hidden columns
- **Paginated Tables**: Reusable pagination component across list pages
- **12 Pages**: Dashboard, Login, Registration (Doctor/Patient), Doctor List/Detail, Patient List/Detail, Prescription List/Create/Detail, Unauthorized
- **Edit Mode**: Owners can edit their own profiles on detail pages
- **Dynamic Forms**: Multiple medications in prescription create form
- **PDF Download & Email**: Prescription actions from detail page
- **Toast Notifications**: Success/error feedback for all API operations

## Prerequisites

- Java 21+
- Maven 3.9+
- MySQL 8.0+
- Node.js 18+
- npm 9+
- Docker & Docker Compose (optional)

## Getting Started

### Option 1: Run with Docker Compose (Backend Only)

```bash
cd backend
docker-compose up --build
```

This starts all backend services + MySQL.

### Option 2: Run Backend Locally

1. **Start MySQL** and create databases:
```sql
CREATE DATABASE IF NOT EXISTS medicnote_doctor;
CREATE DATABASE IF NOT EXISTS medicnote_patient;
CREATE DATABASE IF NOT EXISTS medicnote_prescription;
```

2. **Set environment variables**:
```bash
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

# JWT
export JWT_SECRET=MedicNoteSecretKey2024SuperSecureTokenForJWT
```

3. **Start services in order** (each in a separate terminal):
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

# 6. API Gateway
cd backend/apiGateway && ../mvnw spring-boot:run
```

### Option 3: Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at http://localhost:5173 and proxies `/api` requests to http://localhost:8080 (API Gateway).

## Access Points

| What | URL |
|------|-----|
| Frontend | http://localhost:5173 |
| API Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |
| Doctor Swagger | http://localhost:8082/swagger-ui.html |
| Patient Swagger | http://localhost:8083/swagger-ui.html |
| Prescription Swagger | http://localhost:8084/swagger-ui.html |
| Auth Swagger | http://localhost:8081/swagger-ui.html |

## API Endpoints (33 total: 8 public, 25 protected)

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
| PUT | `/api/prescriptions/{id}` | Update | DOCTOR |
| PATCH | `/api/prescriptions/{id}/status` | Update status | DOCTOR |
| DELETE | `/api/prescriptions/{id}` | Soft delete | DOCTOR |
| GET | `/api/prescriptions/{id}/download` | Download PDF | DOCTOR, PATIENT |
| POST | `/api/prescriptions/{id}/email` | Email prescription | DOCTOR, PATIENT |

## Frontend Pages & Routes

| Page | Route | Access | Description |
|------|-------|--------|-------------|
| Login | `/login` | Public | Doctor/Patient tab switcher with JWT auth |
| Doctor Register | `/register/doctor` | Public | 8-field registration form |
| Patient Register | `/register/patient` | Public | 4-section form (personal, address, emergency, medical) |
| Dashboard | `/dashboard` | Protected | Stats cards, recent prescriptions |
| Doctor List | `/doctors` | Protected | Paginated table with specialization filter |
| Doctor Detail | `/doctors/:id` | Protected | View/Edit profile (owner only) |
| Patient List | `/patients` | DOCTOR only | Paginated patient table |
| Patient Detail | `/patients/:id` | Protected | 4-card layout, edit own profile |
| Prescription List | `/prescriptions` | Protected | Role-based data, "View All" toggle |
| Prescription Create | `/prescriptions/create` | DOCTOR only | Patient dropdown, dynamic medications |
| Prescription Detail | `/prescriptions/:id` | Protected | PDF download, email, mark completed |
| Unauthorized | `/unauthorized` | Public | Access denied page |

## Project Structure

```
MedicNote/
├── backend/
│   ├── eurekaServer/              # Service Discovery (Port 8761)
│   ├── apiGateway/                # API Gateway (Port 8080)
│   ├── authService/               # Authentication Service (Port 8081)
│   ├── doctorService/             # Doctor Management (Port 8082)
│   ├── patientService/            # Patient Management (Port 8083)
│   ├── prescriptionService/       # Prescription, PDF, Email (Port 8084)
│   ├── docker-compose.yml         # Docker orchestration
│   ├── init-db.sql                # Database initialization
│   ├── .dockerignore
│   └── pom.xml                    # Parent POM
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   │   ├── axios.js           # Axios instance + JWT interceptor
│   │   │   └── services.js        # API service functions
│   │   ├── components/
│   │   │   ├── Navbar.jsx         # Top navigation bar
│   │   │   ├── Pagination.jsx     # Reusable pagination
│   │   │   └── ProtectedRoute.jsx # Auth + role guard
│   │   ├── context/
│   │   │   └── AuthContext.jsx    # Auth state management
│   │   ├── pages/
│   │   │   ├── auth/              # Login, DoctorRegister, PatientRegister
│   │   │   ├── doctor/            # DoctorList, DoctorDetail
│   │   │   ├── patient/           # PatientList, PatientDetail
│   │   │   ├── prescription/      # PrescriptionList, Create, Detail
│   │   │   ├── Dashboard.jsx
│   │   │   └── Unauthorized.jsx
│   │   ├── App.jsx                # Router + layout
│   │   ├── index.css              # Tailwind CSS import
│   │   └── main.jsx               # Entry point
│   ├── vite.config.js             # Vite + proxy config
│   └── package.json
└── README.md
```

## Environment Variables

### Backend

| Variable | Service | Description | Example |
|----------|---------|-------------|---------|
| DOCTOR_DB_URL | Doctor | JDBC URL | jdbc:mysql://localhost:3306/medicnote_doctor |
| DOCTOR_DB_USERNAME | Doctor | DB username | root |
| DOCTOR_DB_PASSWORD | Doctor | DB password | root |
| PATIENT_DB_URL | Patient | JDBC URL | jdbc:mysql://localhost:3306/medicnote_patient |
| PATIENT_DB_USERNAME | Patient | DB username | root |
| PATIENT_DB_PASSWORD | Patient | DB password | root |
| PRESCRIPTION_DB_URL | Prescription | JDBC URL | jdbc:mysql://localhost:3306/medicnote_prescription |
| PRESCRIPTION_DB_USERNAME | Prescription | DB username | root |
| PRESCRIPTION_DB_PASSWORD | Prescription | DB password | root |
| JWT_SECRET | All services | JWT signing secret | MedicNoteSecretKey2024SuperSecureTokenForJWT |
| MAIL_HOST | Prescription | SMTP host | smtp.gmail.com |
| MAIL_PORT | Prescription | SMTP port | 587 |
| MAIL_USERNAME | Prescription | Email address | your-email@gmail.com |
| MAIL_PASSWORD | Prescription | Email app password | your-app-password |

### Frontend

The frontend uses Vite's proxy configuration in `vite.config.js` to route API requests. No environment variables are required for development. The proxy forwards all `/api/*` requests to `http://localhost:8080`.

## Build

```bash
# Build all backend modules
cd backend && ./mvnw clean package -DskipTests

# Build specific backend module
cd backend/doctorService && ../mvnw clean package -DskipTests

# Build frontend for production
cd frontend && npm run build
```

## Documentation

For detailed documentation on each part, see:
- [Backend README](./backend/README.md) - Detailed backend architecture, services, and API docs
- [Frontend README](./frontend/README.md) - Detailed frontend setup, pages, components, and config
