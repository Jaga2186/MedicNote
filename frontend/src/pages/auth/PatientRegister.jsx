import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import { FiUser, FiMail, FiLock, FiPhone, FiCalendar, FiMapPin, FiHeart, FiAlertCircle } from 'react-icons/fi';
import { registerPatient } from '../../api/services';

const BLOOD_GROUPS = [
  { value: '', label: 'Select blood group' },
  { value: 'A_POSITIVE', label: 'A+' },
  { value: 'A_NEGATIVE', label: 'A-' },
  { value: 'B_POSITIVE', label: 'B+' },
  { value: 'B_NEGATIVE', label: 'B-' },
  { value: 'O_POSITIVE', label: 'O+' },
  { value: 'O_NEGATIVE', label: 'O-' },
  { value: 'AB_POSITIVE', label: 'AB+' },
  { value: 'AB_NEGATIVE', label: 'AB-' },
];

const GENDERS = [
  { value: '', label: 'Select gender' },
  { value: 'MALE', label: 'Male' },
  { value: 'FEMALE', label: 'Female' },
  { value: 'OTHER', label: 'Other' },
];

export default function PatientRegister() {
  const [formData, setFormData] = useState({
    patientName: '',
    patientEmail: '',
    patientPassword: '',
    patientPhone: '',
    dateOfBirth: '',
    gender: '',
    address: { street: '', city: '', state: '', zipCode: '' },
    emergencyContact: { contactName: '', contactPhone: '', relationship: '' },
    medicalInfo: { bloodGroup: '', allergies: '', chronicConditions: '' },
  });
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleNestedChange = (section, field, value) => {
    setFormData((prev) => ({
      ...prev,
      [section]: { ...prev[section], [field]: value },
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await registerPatient(formData);
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
  const plainInputClass =
    'w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-shadow text-sm';
  const selectClass =
    'w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-shadow text-sm bg-white';

  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4 py-8">
      <div className="w-full max-w-3xl">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Patient Registration</h1>
          <p className="text-gray-500 mt-2">Create your MedicNote patient account</p>
        </div>

        {/* Card */}
        <div className="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden">
          <form onSubmit={handleSubmit} className="p-6 sm:p-8 space-y-6">

            {/* ─── Personal Information ─── */}
            <div>
              <h2 className="flex items-center gap-2 text-lg font-semibold text-gray-800 mb-4 pb-2 border-b border-gray-200">
                <FiUser className="text-blue-600" />
                Personal Information
              </h2>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label htmlFor="patientName" className="block text-sm font-medium text-gray-700 mb-1">Full Name</label>
                  <div className="relative">
                    <FiUser className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input id="patientName" name="patientName" type="text" required value={formData.patientName} onChange={handleChange} placeholder="Jane Doe" className={inputClass} />
                  </div>
                </div>

                <div>
                  <label htmlFor="patientEmail" className="block text-sm font-medium text-gray-700 mb-1">Email Address</label>
                  <div className="relative">
                    <FiMail className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input id="patientEmail" name="patientEmail" type="email" required value={formData.patientEmail} onChange={handleChange} placeholder="patient@example.com" className={inputClass} />
                  </div>
                </div>

                <div>
                  <label htmlFor="patientPassword" className="block text-sm font-medium text-gray-700 mb-1">Password</label>
                  <div className="relative">
                    <FiLock className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input id="patientPassword" name="patientPassword" type="password" required value={formData.patientPassword} onChange={handleChange} placeholder="Create a strong password" className={inputClass} />
                  </div>
                </div>

                <div>
                  <label htmlFor="patientPhone" className="block text-sm font-medium text-gray-700 mb-1">Phone Number</label>
                  <div className="relative">
                    <FiPhone className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input id="patientPhone" name="patientPhone" type="text" required value={formData.patientPhone} onChange={handleChange} placeholder="+1 234 567 8900" className={inputClass} />
                  </div>
                </div>

                <div>
                  <label htmlFor="dateOfBirth" className="block text-sm font-medium text-gray-700 mb-1">Date of Birth</label>
                  <div className="relative">
                    <FiCalendar className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input id="dateOfBirth" name="dateOfBirth" type="date" required value={formData.dateOfBirth} onChange={handleChange} className={inputClass} />
                  </div>
                </div>

                <div>
                  <label htmlFor="gender" className="block text-sm font-medium text-gray-700 mb-1">Gender</label>
                  <select id="gender" name="gender" required value={formData.gender} onChange={handleChange} className={selectClass}>
                    {GENDERS.map((g) => (
                      <option key={g.value} value={g.value} disabled={g.value === ''}>
                        {g.label}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
            </div>

            {/* ─── Address ─── */}
            <div>
              <h2 className="flex items-center gap-2 text-lg font-semibold text-gray-800 mb-4 pb-2 border-b border-gray-200">
                <FiMapPin className="text-blue-600" />
                Address
              </h2>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="sm:col-span-2">
                  <label htmlFor="street" className="block text-sm font-medium text-gray-700 mb-1">Street Address</label>
                  <input id="street" type="text" required value={formData.address.street} onChange={(e) => handleNestedChange('address', 'street', e.target.value)} placeholder="123 Main Street" className={plainInputClass} />
                </div>
                <div>
                  <label htmlFor="city" className="block text-sm font-medium text-gray-700 mb-1">City</label>
                  <input id="city" type="text" required value={formData.address.city} onChange={(e) => handleNestedChange('address', 'city', e.target.value)} placeholder="New York" className={plainInputClass} />
                </div>
                <div>
                  <label htmlFor="state" className="block text-sm font-medium text-gray-700 mb-1">State</label>
                  <input id="state" type="text" required value={formData.address.state} onChange={(e) => handleNestedChange('address', 'state', e.target.value)} placeholder="NY" className={plainInputClass} />
                </div>
                <div>
                  <label htmlFor="zipCode" className="block text-sm font-medium text-gray-700 mb-1">Zip Code</label>
                  <input id="zipCode" type="text" required value={formData.address.zipCode} onChange={(e) => handleNestedChange('address', 'zipCode', e.target.value)} placeholder="10001" className={plainInputClass} />
                </div>
              </div>
            </div>

            {/* ─── Emergency Contact ─── */}
            <div>
              <h2 className="flex items-center gap-2 text-lg font-semibold text-gray-800 mb-4 pb-2 border-b border-gray-200">
                <FiAlertCircle className="text-blue-600" />
                Emergency Contact
              </h2>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div>
                  <label htmlFor="contactName" className="block text-sm font-medium text-gray-700 mb-1">Contact Name</label>
                  <input id="contactName" type="text" required value={formData.emergencyContact.contactName} onChange={(e) => handleNestedChange('emergencyContact', 'contactName', e.target.value)} placeholder="John Doe" className={plainInputClass} />
                </div>
                <div>
                  <label htmlFor="contactPhone" className="block text-sm font-medium text-gray-700 mb-1">Contact Phone</label>
                  <input id="contactPhone" type="text" required value={formData.emergencyContact.contactPhone} onChange={(e) => handleNestedChange('emergencyContact', 'contactPhone', e.target.value)} placeholder="+1 234 567 8900" className={plainInputClass} />
                </div>
                <div>
                  <label htmlFor="relationship" className="block text-sm font-medium text-gray-700 mb-1">Relationship</label>
                  <input id="relationship" type="text" required value={formData.emergencyContact.relationship} onChange={(e) => handleNestedChange('emergencyContact', 'relationship', e.target.value)} placeholder="Spouse, Parent, etc." className={plainInputClass} />
                </div>
              </div>
            </div>

            {/* ─── Medical Information ─── */}
            <div>
              <h2 className="flex items-center gap-2 text-lg font-semibold text-gray-800 mb-4 pb-2 border-b border-gray-200">
                <FiHeart className="text-blue-600" />
                Medical Information
              </h2>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label htmlFor="bloodGroup" className="block text-sm font-medium text-gray-700 mb-1">Blood Group</label>
                  <select id="bloodGroup" required value={formData.medicalInfo.bloodGroup} onChange={(e) => handleNestedChange('medicalInfo', 'bloodGroup', e.target.value)} className={selectClass}>
                    {BLOOD_GROUPS.map((bg) => (
                      <option key={bg.value} value={bg.value} disabled={bg.value === ''}>
                        {bg.label}
                      </option>
                    ))}
                  </select>
                </div>
                <div>
                  <label htmlFor="allergies" className="block text-sm font-medium text-gray-700 mb-1">Allergies</label>
                  <input id="allergies" type="text" value={formData.medicalInfo.allergies} onChange={(e) => handleNestedChange('medicalInfo', 'allergies', e.target.value)} placeholder="e.g. Penicillin, Peanuts (or leave blank)" className={plainInputClass} />
                </div>
                <div className="sm:col-span-2">
                  <label htmlFor="chronicConditions" className="block text-sm font-medium text-gray-700 mb-1">Chronic Conditions</label>
                  <input id="chronicConditions" type="text" value={formData.medicalInfo.chronicConditions} onChange={(e) => handleNestedChange('medicalInfo', 'chronicConditions', e.target.value)} placeholder="e.g. Diabetes, Hypertension (or leave blank)" className={plainInputClass} />
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
                'Create Patient Account'
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
