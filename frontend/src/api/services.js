import api from './axios';

// Auth
export const loginDoctor = (data) => api.post('/auth/doctor/login', data);
export const loginPatient = (data) => api.post('/auth/patient/login', data);
export const verifyOtp = (data) => api.post('/auth/otp/verify', data);
export const registerDoctor = (data) => api.post('/auth/doctor/register', data);
export const registerPatient = (data) => api.post('/auth/patient/register', data);
export const validateToken = () => api.get('/auth/validate');

// Doctors
export const getDoctors = () => api.get('/doctors');
export const getDoctorsPaginated = (page = 0, size = 10, sortBy = 'doctorId', direction = 'asc') =>
  api.get(`/doctors/page?page=${page}&size=${size}&sortBy=${sortBy}&direction=${direction}`);
export const getDoctorById = (id) => api.get(`/doctors/${id}`);
export const getDoctorsBySpecialization = (spec) => api.get(`/doctors/specialization/${spec}`);
export const getDoctorByEmail = (email) => api.get(`/doctors/by-email/${email}`);
export const getDoctorByPhone = (phone) => api.get(`/doctors/by-phone/${phone}`);
export const updateDoctor = (id, data) => api.put(`/doctors/${id}`, data);
export const deleteDoctor = (id) => api.delete(`/doctors/${id}`);

// Patients
export const getPatients = () => api.get('/patients');
export const getPatientsPaginated = (page = 0, size = 10, sortBy = 'patientId', direction = 'asc') =>
  api.get(`/patients/page?page=${page}&size=${size}&sortBy=${sortBy}&direction=${direction}`);
export const getPatientById = (id) => api.get(`/patients/${id}`);
export const getPatientByEmail = (email) => api.get(`/patients/by-email/${email}`);
export const getPatientByPhone = (phone) => api.get(`/patients/by-phone/${phone}`);
export const updatePatient = (id, data) => api.put(`/patients/${id}`, data);
export const deletePatient = (id) => api.delete(`/patients/${id}`);

// Prescriptions
export const getPrescriptions = () => api.get('/prescriptions');
export const getPrescriptionsPaginated = (page = 0, size = 10, sortBy = 'prescriptionId', direction = 'desc') =>
  api.get(`/prescriptions/page?page=${page}&size=${size}&sortBy=${sortBy}&direction=${direction}`);
export const getPrescriptionById = (id) => api.get(`/prescriptions/${id}`);
export const getPrescriptionsByDoctor = (doctorId) => api.get(`/prescriptions/doctor/${doctorId}`);
export const getPrescriptionsByPatient = (patientId) => api.get(`/prescriptions/patient/${patientId}`);
export const getPrescriptionsByStatus = (status) => api.get(`/prescriptions/status/${status}`);
export const createPrescription = (data) => api.post('/prescriptions', data);
export const updatePrescription = (id, data) => api.put(`/prescriptions/${id}`, data);
export const updatePrescriptionStatus = (id, status) => api.patch(`/prescriptions/${id}/status?status=${status}`);
export const deletePrescription = (id) => api.delete(`/prescriptions/${id}`);
export const downloadPrescriptionPdf = (id) => api.get(`/prescriptions/${id}/download`, { responseType: 'blob' });
export const emailPrescription = (id) => api.post(`/prescriptions/${id}/email`);