import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import { FiUser, FiMail, FiLock, FiPhone, FiBriefcase, FiAward, FiClock, FiHome } from 'react-icons/fi';
import { registerDoctor } from '../../api/services';

export default function DoctorRegister() {
  const [formData, setFormData] = useState({
    doctorName: '',
    doctorEmail: '',
    doctorPassword: '',
    doctorPhone: '',
    specialization: '',
    licenseNumber: '',
    experienceYears: '',
    hospitalName: '',
  });
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const payload = {
      ...formData,
      experienceYears: Number(formData.experienceYears),
    };

    setLoading(true);
    try {
      const res = await registerDoctor(payload);
      toast.success(res.data?.message || 'Registration successful! Please login.');
      navigate('/login');
    } catch (err) {
      const msg =
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Registration failed. Please try again.';
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  const inputClass =
    'w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-shadow text-sm';

  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4 py-8">
      <div className="w-full max-w-2xl">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Doctor Registration</h1>
          <p className="text-gray-500 mt-2">Create your MedicNote doctor account</p>
        </div>

        {/* Card */}
        <div className="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden">
          <form onSubmit={handleSubmit} className="p-6 sm:p-8 space-y-6">
            {/* Personal Information */}
            <div>
              <h2 className="text-lg font-semibold text-gray-800 mb-4 pb-2 border-b border-gray-200">
                Personal Information
              </h2>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {/* Name */}
                <div>
                  <label htmlFor="doctorName" className="block text-sm font-medium text-gray-700 mb-1">
                    Full Name
                  </label>
                  <div className="relative">
                    <FiUser className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input id="doctorName" name="doctorName" type="text" required value={formData.doctorName} onChange={handleChange} placeholder="Dr. John Doe" className={inputClass} />
                  </div>
                </div>

                {/* Email */}
                <div>
                  <label htmlFor="doctorEmail" className="block text-sm font-medium text-gray-700 mb-1">
                    Email Address
                  </label>
                  <div className="relative">
                    <FiMail className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input id="doctorEmail" name="doctorEmail" type="email" required value={formData.doctorEmail} onChange={handleChange} placeholder="doctor@example.com" className={inputClass} />
                  </div>
                </div>

                {/* Password */}
                <div>
                  <label htmlFor="doctorPassword" className="block text-sm font-medium text-gray-700 mb-1">
                    Password
                  </label>
                  <div className="relative">
                    <FiLock className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input id="doctorPassword" name="doctorPassword" type="password" required value={formData.doctorPassword} onChange={handleChange} placeholder="Create a strong password" className={inputClass} />
                  </div>
                </div>

                {/* Phone */}
                <div>
                  <label htmlFor="doctorPhone" className="block text-sm font-medium text-gray-700 mb-1">
                    Phone Number
                  </label>
                  <div className="relative">
                    <FiPhone className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input id="doctorPhone" name="doctorPhone" type="text" required value={formData.doctorPhone} onChange={handleChange} placeholder="+1 234 567 8900" className={inputClass} />
                  </div>
                </div>
              </div>
            </div>

            {/* Professional Information */}
            <div>
              <h2 className="text-lg font-semibold text-gray-800 mb-4 pb-2 border-b border-gray-200">
                Professional Information
              </h2>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {/* Specialization */}
                <div>
                  <label htmlFor="specialization" className="block text-sm font-medium text-gray-700 mb-1">
                    Specialization
                  </label>
                  <div className="relative">
                    <FiBriefcase className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input id="specialization" name="specialization" type="text" required value={formData.specialization} onChange={handleChange} placeholder="e.g. Cardiology" className={inputClass} />
                  </div>
                </div>

                {/* License Number */}
                <div>
                  <label htmlFor="licenseNumber" className="block text-sm font-medium text-gray-700 mb-1">
                    License Number
                  </label>
                  <div className="relative">
                    <FiAward className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input id="licenseNumber" name="licenseNumber" type="text" required value={formData.licenseNumber} onChange={handleChange} placeholder="MED-XXXXXX" className={inputClass} />
                  </div>
                </div>

                {/* Experience Years */}
                <div>
                  <label htmlFor="experienceYears" className="block text-sm font-medium text-gray-700 mb-1">
                    Years of Experience
                  </label>
                  <div className="relative">
                    <FiClock className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input id="experienceYears" name="experienceYears" type="number" min="0" required value={formData.experienceYears} onChange={handleChange} placeholder="5" className={inputClass} />
                  </div>
                </div>

                {/* Hospital Name */}
                <div>
                  <label htmlFor="hospitalName" className="block text-sm font-medium text-gray-700 mb-1">
                    Hospital / Clinic Name
                  </label>
                  <div className="relative">
                    <FiHome className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input id="hospitalName" name="hospitalName" type="text" required value={formData.hospitalName} onChange={handleChange} placeholder="City General Hospital" className={inputClass} />
                  </div>
                </div>
              </div>
            </div>

            {/* Submit */}
            <button
              type="submit"
              disabled={loading}
              className="w-full flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 disabled:cursor-not-allowed text-white font-semibold py-2.5 px-4 rounded-lg transition-colors duration-200"
            >
              {loading ? (
                <>
                  <svg className="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                  </svg>
                  Creating Account...
                </>
              ) : (
                'Create Doctor Account'
              )}
            </button>
          </form>

          {/* Footer */}
          <div className="px-6 pb-6 text-center text-sm text-gray-500">
            Already have an account?{' '}
            <Link to="/login" className="text-blue-600 hover:text-blue-700 font-medium">
              Login
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
