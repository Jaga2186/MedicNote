# MedicNote Frontend

A modern, responsive medical records management frontend built with React and Vite. MedicNote provides role-based interfaces for doctors and patients to manage profiles, prescriptions, and medical data.

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| React | 19.2.4 | UI framework |
| Vite | 8.0.4 | Build tool and dev server |
| Tailwind CSS | 4.2.2 | Utility-first CSS (v4 with `@tailwindcss/vite` plugin) |
| React Router DOM | 7.14.0 | Client-side routing |
| Axios | 1.15.0 | HTTP client |
| React Hot Toast | 2.6.0 | Toast notifications |
| React Icons | 5.6.0 | Icon library (Feather Icons - `fi` prefix) |
| ESLint | 9.39.4 | Code linting |

> **Note:** Tailwind CSS v4 uses `@import "tailwindcss"` in `index.css` and the `@tailwindcss/vite` plugin. No `tailwind.config.js` file is needed.

## Features

- **2FA OTP Login** — Two-step login: enter email/phone + password, receive OTP on registered email, verify OTP to get JWT.
- **Email or Phone Login** — Users can log in using either their registered email address or phone number.
- **Role-based authentication** — DOCTOR and PATIENT roles with JWT tokens stored in localStorage.
- **Role-based route protection** — `ProtectedRoute` component restricts routes (some are DOCTOR-only).
- **Responsive design** — Mobile-first layout with responsive grids and hidden columns on small screens.
- **Paginated tables** — Reusable `Pagination` component across list pages.
- **Toast notifications** — Success and error feedback for all API responses.
- **Axios interceptor** — Automatically attaches JWT Bearer token to all outgoing requests. Redirects to `/login` on 401.
- **API proxy** — Vite dev server proxies `/api` requests to `http://localhost:8080` (API Gateway).
- **Edit mode** — Detail pages allow owners to edit their own profiles inline.
- **Dynamic form arrays** — Add/remove multiple medications when creating prescriptions.
- **Doctor/Patient tab switcher** — Login page toggles between doctor and patient authentication.
- **Specialization filter** — Search and filter doctors by specialization on the doctor list page.
- **PDF download and email** — Download prescriptions as PDF or email them directly.
- **Soft-delete with confirmation** — Delete actions require confirmation dialogs before execution.

## Prerequisites

- Node.js 18+
- npm 9+
- Backend services running (API Gateway at port 8080)

## Getting Started

```bash
# Install dependencies
npm install

# Start development server
npm run dev
```

The app runs at **http://localhost:5173**. The Vite dev server proxies all `/api/*` requests to `http://localhost:8080` (API Gateway).

## Available Scripts

| Command | Description |
|---------|-------------|
| `npm run dev` | Start dev server at http://localhost:5173 |
| `npm run build` | Production build to `dist/` folder |
| `npm run preview` | Preview production build locally |
| `npm run lint` | Run ESLint |

## Project Structure

```
frontend/
├── public/
├── src/
│   ├── api/
│   │   ├── axios.js              # Axios instance + JWT interceptor + 401 redirect
│   │   └── services.js           # API service functions for all endpoints
│   ├── components/
│   │   ├── Navbar.jsx            # Top navigation bar
│   │   ├── Pagination.jsx        # Reusable pagination
│   │   └── ProtectedRoute.jsx    # Auth + role guard
│   ├── context/
│   │   └── AuthContext.jsx       # Auth state management
│   ├── pages/
│   │   ├── auth/
│   │   │   ├── Login.jsx         # Step 1 — identifier + password, sends OTP
│   │   │   ├── VerifyOtp.jsx     # Step 2 — 6-digit OTP input, receives JWT
│   │   │   ├── DoctorRegister.jsx  # Doctor registration form
│   │   │   └── PatientRegister.jsx # Patient registration form
│   │   ├── doctor/
│   │   │   ├── DoctorList.jsx    # Paginated doctor table
│   │   │   └── DoctorDetail.jsx  # Doctor profile + edit
│   │   ├── patient/
│   │   │   ├── PatientList.jsx   # Paginated patient table
│   │   │   └── PatientDetail.jsx # Patient profile + edit
│   │   ├── prescription/
│   │   │   ├── PrescriptionList.jsx   # Prescription table
│   │   │   ├── PrescriptionCreate.jsx # Create prescription form
│   │   │   └── PrescriptionDetail.jsx # View + actions
│   │   ├── Dashboard.jsx         # Main dashboard
│   │   └── Unauthorized.jsx      # 403 page
│   ├── App.jsx                   # Router + layout
│   ├── App.css                   # App styles
│   ├── index.css                 # Tailwind CSS import
│   └── main.jsx                  # Entry point
├── index.html
├── vite.config.js                # Vite + proxy config
├── eslint.config.js
└── package.json
```

## Login Flow

MedicNote uses a **2-step 2FA login flow**. Password alone does not grant access — an OTP must be verified first.

### Step 1 — Login page (`/login`)

User enters their email or phone number and password. On success, the backend sends a 6-digit OTP to the registered email and returns a `sessionToken`. The `sessionToken` is saved to `sessionStorage` and the user is redirected to `/verify-otp`.

```
POST /api/auth/doctor/login  or  POST /api/auth/patient/login
Body: { "identifier": "john@hospital.com", "password": "Pass@123" }

Response: { "sessionToken": "uuid-here", "message": "OTP sent to j***n@hospital.com" }
```

### Step 2 — OTP verification page (`/verify-otp`)

User enters the 6-digit OTP received in their email. The `sessionToken` is read from `sessionStorage` and sent along with the OTP. On success, the JWT token is saved to `localStorage`, `sessionToken` is cleared from `sessionStorage`, and the user is redirected to `/dashboard`.

```
POST /api/auth/otp/verify
Body: { "sessionToken": "uuid-here", "otpCode": "483921" }

Response: { "token": "JWT...", "role": "DOCTOR", "data": { ...user details } }
```

### Token Storage

| Item | Storage | Lifetime |
|------|---------|---------|
| `sessionToken` | `sessionStorage` | Until OTP verified or tab closed |
| `token` (JWT) | `localStorage` | Until logout |
| `role` | `localStorage` | Until logout |
| `user` | `localStorage` | Until logout |

---

## Pages

The application contains **13 pages** organized by domain:

### Public Pages

| Page | File | Route | Description |
|------|------|-------|-------------|
| Login | `pages/auth/Login.jsx` | `/login` | Step 1 of 2FA — Doctor/Patient tab switcher, accepts email or phone + password, sends OTP |
| Verify OTP | `pages/auth/VerifyOtp.jsx` | `/verify-otp` | Step 2 of 2FA — 6-digit OTP input with auto-focus, paste support, and back-to-login option |
| Doctor Register | `pages/auth/DoctorRegister.jsx` | `/register/doctor` | 8-field registration form in a 2-column grid layout |
| Patient Register | `pages/auth/PatientRegister.jsx` | `/register/patient` | Multi-section form: personal, address, emergency contact, medical history |
| Unauthorized | `pages/Unauthorized.jsx` | `/unauthorized` | Access denied page shown when role requirements are not met |

### Protected Pages (Authenticated Users)

| Page | File | Route | Description |
|------|------|-------|-------------|
| Dashboard | `pages/Dashboard.jsx` | `/dashboard` | Stats cards and recent prescriptions table |
| Doctor List | `pages/doctor/DoctorList.jsx` | `/doctors` | Paginated table with specialization filter/search |
| Doctor Detail | `pages/doctor/DoctorDetail.jsx` | `/doctors/:id` | View doctor profile; owners can edit and delete |
| Patient Detail | `pages/patient/PatientDetail.jsx` | `/patients/:id` | 4-card layout displaying patient data; owners can edit their own profile |
| Prescription List | `pages/prescription/PrescriptionList.jsx` | `/prescriptions` | Role-based data display with "View All" toggle |
| Prescription Detail | `pages/prescription/PrescriptionDetail.jsx` | `/prescriptions/:id` | PDF download, email prescription, mark as completed |

### Doctor-Only Pages

| Page | File | Route | Description |
|------|------|-------|-------------|
| Patient List | `pages/patient/PatientList.jsx` | `/patients` | Paginated, responsive patient table |
| Prescription Create | `pages/prescription/PrescriptionCreate.jsx` | `/prescriptions/create` | Patient dropdown with dynamic medication form arrays |

---

## Core Components

### Navbar.jsx

Top navigation bar with role-aware links. Displays different navigation items based on the user's role (DOCTOR or PATIENT) and includes a logout button.

### Pagination.jsx

Reusable pagination component with page numbers, previous/next buttons. Used across all list pages (doctors, patients, prescriptions).

### ProtectedRoute.jsx

Route guard component that handles authentication and role-based access control. Redirects unauthenticated users to `/login` and unauthorized users to `/unauthorized`.

---

## Core Modules

### api/axios.js

Configures an Axios instance with `baseURL: '/api'` and two interceptors:
- **Request interceptor** — automatically attaches the JWT Bearer token from `localStorage` to all outgoing requests.
- **Response interceptor** — on 401 Unauthorized, clears `localStorage` and redirects to `/login`.

### api/services.js

Contains all API service functions organized by domain:

**Auth Service (port 8081 via gateway)**
```js
loginDoctor(data)        // POST /api/auth/doctor/login
loginPatient(data)       // POST /api/auth/patient/login
verifyOtp(data)          // POST /api/auth/otp/verify
registerDoctor(data)     // POST /api/auth/doctor/register
registerPatient(data)    // POST /api/auth/patient/register
validateToken()          // GET  /api/auth/validate
```

**Doctor Service (port 8082 via gateway)**
```js
getDoctors()                              // GET /api/doctors
getDoctorsPaginated(page, size, ...)      // GET /api/doctors/page
getDoctorById(id)                         // GET /api/doctors/{id}
getDoctorsBySpecialization(spec)          // GET /api/doctors/specialization/{spec}
getDoctorByEmail(email)                   // GET /api/doctors/by-email/{email}
getDoctorByPhone(phone)                   // GET /api/doctors/by-phone/{phone}
updateDoctor(id, data)                    // PUT /api/doctors/{id}
deleteDoctor(id)                          // DELETE /api/doctors/{id}
```

**Patient Service (port 8083 via gateway)**
```js
getPatients()                             // GET /api/patients
getPatientsPaginated(page, size, ...)     // GET /api/patients/page
getPatientById(id)                        // GET /api/patients/{id}
getPatientByEmail(email)                  // GET /api/patients/by-email/{email}
getPatientByPhone(phone)                  // GET /api/patients/by-phone/{phone}
updatePatient(id, data)                   // PUT /api/patients/{id}
deletePatient(id)                         // DELETE /api/patients/{id}
```

**Prescription Service (port 8084 via gateway)**
```js
getPrescriptions()                                    // GET /api/prescriptions
getPrescriptionsPaginated(page, size, ...)            // GET /api/prescriptions/page
getPrescriptionById(id)                               // GET /api/prescriptions/{id}
getPrescriptionsByDoctor(doctorId)                    // GET /api/prescriptions/doctor/{doctorId}
getPrescriptionsByPatient(patientId)                  // GET /api/prescriptions/patient/{patientId}
getPrescriptionsByStatus(status)                      // GET /api/prescriptions/status/{status}
createPrescription(data)                              // POST /api/prescriptions
updatePrescription(id, data)                          // PUT /api/prescriptions/{id}
updatePrescriptionStatus(id, status)                  // PATCH /api/prescriptions/{id}/status
deletePrescription(id)                                // DELETE /api/prescriptions/{id}
downloadPrescriptionPdf(id)                           // GET /api/prescriptions/{id}/download
emailPrescription(id)                                 // POST /api/prescriptions/{id}/email
```

### context/AuthContext.jsx

React context provider that manages authentication state. Exposes `login(token, role, userData)`, `logout()`, `isAuthenticated`, `isDoctor`, `isPatient`, `user`, `token`, and `role`. Persists all auth state to `localStorage` and rehydrates on page refresh.

---

## Proxy Configuration

The Vite dev server proxies all `/api/*` requests to the backend API Gateway, defined in `vite.config.js`:

```js
server: {
  port: 5173,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    },
  },
}
```

This eliminates CORS issues during development by routing API calls through the same origin.

---

## Build

```bash
# Create production build
npm run build
```

The build outputs optimized, minified assets to the `dist/` directory:

```
dist/
├── index.html          (0.45 kB)
├── assets/index.css    (27.77 kB, 5.87 kB gzip)
└── assets/index.js     (386.18 kB, 109.94 kB gzip)
```

To preview the production build locally:

```bash
npm run preview
```
