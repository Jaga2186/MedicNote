import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { FiLogOut, FiMenu, FiX } from 'react-icons/fi';
import { useState } from 'react';

export default function Navbar() {
  const { isAuthenticated, role, user, logout } = useAuth();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const links = isAuthenticated
    ? role === 'DOCTOR'
      ? [
          { to: '/dashboard', label: 'Dashboard' },
          { to: '/doctors', label: 'Doctors' },
          { to: '/patients', label: 'Patients' },
          { to: '/prescriptions', label: 'Prescriptions' },
        ]
      : [
          { to: '/dashboard', label: 'Dashboard' },
          { to: '/doctors', label: 'Doctors' },
          { to: '/prescriptions', label: 'My Prescriptions' },
        ]
    : [];

  return (
    <nav className="bg-white border-b border-gray-200 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex items-center">
            <Link to="/" className="text-xl font-bold text-blue-600">MedicNote</Link>
            <div className="hidden md:flex ml-10 space-x-4">
              {links.map((l) => (
                <Link key={l.to} to={l.to} className="text-gray-600 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium transition-colors">
                  {l.label}
                </Link>
              ))}
            </div>
          </div>
          <div className="hidden md:flex items-center space-x-4">
            {isAuthenticated ? (
              <>
                <span className="text-sm text-gray-500">
                  {user?.doctorName || user?.patientName || 'User'}{' '}
                  <span className="bg-blue-100 text-blue-800 text-xs px-2 py-0.5 rounded-full">{role}</span>
                </span>
                <button onClick={handleLogout} className="flex items-center gap-1 text-red-600 hover:text-red-800 text-sm font-medium transition-colors">
                  <FiLogOut /> Logout
                </button>
              </>
            ) : (
              <Link to="/login" className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors">
                Login
              </Link>
            )}
          </div>
          <div className="md:hidden flex items-center">
            <button onClick={() => setOpen(!open)} className="text-gray-600">
              {open ? <FiX size={24} /> : <FiMenu size={24} />}
            </button>
          </div>
        </div>
      </div>
      {open && (
        <div className="md:hidden border-t border-gray-200 bg-white">
          <div className="px-2 pt-2 pb-3 space-y-1">
            {links.map((l) => (
              <Link key={l.to} to={l.to} onClick={() => setOpen(false)} className="block text-gray-600 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium">
                {l.label}
              </Link>
            ))}
            {isAuthenticated && (
              <button onClick={handleLogout} className="w-full text-left text-red-600 px-3 py-2 text-sm font-medium">
                Logout
              </button>
            )}
          </div>
        </div>
      )}
    </nav>
  );
}
