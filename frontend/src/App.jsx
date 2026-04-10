import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Navbar from './components/Navbar';
import Login from './pages/auth/Login';
import DoctorRegister from './pages/auth/DoctorRegister';
import PatientRegister from './pages/auth/PatientRegister';
import Dashboard from './pages/Dashboard';
import DoctorList from './pages/doctor/DoctorList';
import DoctorDetail from './pages/doctor/DoctorDetail';
import PatientList from './pages/patient/PatientList';
import PatientDetail from './pages/patient/PatientDetail';
import PrescriptionList from './pages/prescription/PrescriptionList';
import PrescriptionCreate from './pages/prescription/PrescriptionCreate';
import PrescriptionDetail from './pages/prescription/PrescriptionDetail';
import Unauthorized from './pages/Unauthorized';

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <div className="min-h-screen bg-gray-50">
          <Navbar />
          <Toaster position="top-right" toastOptions={{ duration: 3000 }} />
          <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/register/doctor" element={<DoctorRegister />} />
              <Route path="/register/patient" element={<PatientRegister />} />
              <Route path="/unauthorized" element={<Unauthorized />} />

              <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
              <Route path="/doctors" element={<ProtectedRoute><DoctorList /></ProtectedRoute>} />
              <Route path="/doctors/:id" element={<ProtectedRoute><DoctorDetail /></ProtectedRoute>} />
              <Route path="/patients" element={<ProtectedRoute allowedRoles={['DOCTOR']}><PatientList /></ProtectedRoute>} />
              <Route path="/patients/:id" element={<ProtectedRoute><PatientDetail /></ProtectedRoute>} />
              <Route path="/prescriptions" element={<ProtectedRoute><PrescriptionList /></ProtectedRoute>} />
              <Route path="/prescriptions/create" element={<ProtectedRoute allowedRoles={['DOCTOR']}><PrescriptionCreate /></ProtectedRoute>} />
              <Route path="/prescriptions/:id" element={<ProtectedRoute><PrescriptionDetail /></ProtectedRoute>} />

              <Route path="/" element={<Navigate to="/dashboard" replace />} />
              <Route path="*" element={<Navigate to="/dashboard" replace />} />
            </Routes>
          </main>
        </div>
      </AuthProvider>
    </BrowserRouter>
  );
}
