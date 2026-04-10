import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import {
  FiArrowLeft,
  FiEdit3,
  FiSave,
  FiX,
  FiTrash2,
  FiUser,
  FiMapPin,
  FiPhone,
  FiHeart,
  FiAlertCircle,
} from 'react-icons/fi';
import { getPatientById, updatePatient, deletePatient } from '../../api/services';
import { useAuth } from '../../context/AuthContext';

export default function PatientDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isDoctor, isPatient } = useAuth();

  const [patient, setPatient] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editing, setEditing] = useState(false);
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [formData, setFormData] = useState({});

  const isOwnProfile = isPatient && user?.patientId?.toString() === id?.toString();

  useEffect(() => {
    fetchPatient();
  }, [id]);

  const fetchPatient = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await getPatientById(id);
      const data = res.data.data;
      setPatient(data);
      setFormData(buildFormData(data));
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to load patient details.';
      setError(msg);
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  const buildFormData = (p) => ({
    patientName: p.patientName || '',
    patientEmail: p.patientEmail || '',
    patientPhone: p.patientPhone || '',
    dateOfBirth: p.dateOfBirth || '',
    gender: p.gender || '',
    address: {
      street: p.address?.street || '',
      city: p.address?.city || '',
      state: p.address?.state || '',
      zipCode: p.address?.zipCode || '',
    },
    emergencyContact: {
      contactName: p.emergencyContact?.contactName || '',
      contactPhone: p.emergencyContact?.contactPhone || '',
      relationship: p.emergencyContact?.relationship || '',
    },
    medicalInfo: {
      bloodGroup: p.medicalInfo?.bloodGroup || '',
      allergies: p.medicalInfo?.allergies || '',
      chronicConditions: p.medicalInfo?.chronicConditions || '',
    },
  });

  const handleChange = (section, field, value) => {
    if (section) {
      setFormData((prev) => ({
        ...prev,
        [section]: { ...prev[section], [field]: value },
      }));
    } else {
      setFormData((prev) => ({ ...prev, [field]: value }));
    }
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      const res = await updatePatient(id, formData);
      const updated = res.data.data;
      setPatient(updated);
      setFormData(buildFormData(updated));
      setEditing(false);
      toast.success(res.data?.message || 'Patient updated successfully.');
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to update patient.';
      toast.error(msg);
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setFormData(buildFormData(patient));
    setEditing(false);
  };

  const handleDelete = async () => {
    if (!window.confirm(`Are you sure you want to delete "${patient.patientName}"? This action cannot be undone.`)) return;
    setDeleting(true);
    try {
      const res = await deletePatient(id);
      toast.success(res.data?.message || 'Patient deleted successfully.');
      navigate('/patients');
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to delete patient.';
      toast.error(msg);
    } finally {
      setDeleting(false);
    }
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  const formatDateTime = (dateStr) => {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  // Loading state
  if (loading) {
    return (
      <div className="flex items-center justify-center h-[60vh]">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-500 text-sm">Loading patient details...</p>
        </div>
      </div>
    );
  }

  // Error state
  if (error || !patient) {
    return (
      <div className="flex items-center justify-center h-[60vh]">
        <div className="text-center">
          <FiAlertCircle className="mx-auto text-red-400 mb-4" size={48} />
          <h3 className="text-lg font-semibold text-gray-700">Failed to load patient</h3>
          <p className="text-gray-500 text-sm mt-1">{error || 'Patient not found.'}</p>
          <button
            onClick={() => navigate(-1)}
            className="mt-4 inline-flex items-center gap-2 text-blue-600 hover:text-blue-700 font-medium text-sm transition-colors"
          >
            <FiArrowLeft /> Go Back
          </button>
        </div>
      </div>
    );
  }

  const inputClass =
    'w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-shadow text-sm';
  const labelClass = 'block text-xs font-medium text-gray-500 mb-1';
  const valueClass = 'text-sm text-gray-900';

  const renderField = (label, value, section, field, type = 'text', options = null) => (
    <div>
      <span className={labelClass}>{label}</span>
      {editing ? (
        options ? (
          <select
            value={section ? formData[section]?.[field] : formData[field]}
            onChange={(e) => handleChange(section, field, e.target.value)}
            className={inputClass}
          >
            <option value="">Select...</option>
            {options.map((opt) => (
              <option key={opt} value={opt}>
                {opt}
              </option>
            ))}
          </select>
        ) : (
          <input
            type={type}
            value={section ? formData[section]?.[field] : formData[field]}
            onChange={(e) => handleChange(section, field, e.target.value)}
            className={inputClass}
          />
        )
      ) : (
        <p className={valueClass}>{value || '—'}</p>
      )}
    </div>
  );

  return (
    <div>
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <div className="flex items-center gap-4">
          <button
            onClick={() => navigate(-1)}
            className="p-2 rounded-lg border border-gray-300 hover:bg-gray-50 transition-colors"
            title="Go Back"
          >
            <FiArrowLeft className="text-gray-600" />
          </button>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">{patient.patientName}</h1>
            <div className="flex items-center gap-2 mt-1">
              <span className={`text-xs font-medium px-2 py-0.5 rounded-full ${
                patient.isActive ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
              }`}>
                {patient.isActive ? 'Active' : 'Inactive'}
              </span>
              <span className="text-xs text-gray-400">ID: {patient.patientId}</span>
            </div>
          </div>
        </div>

        <div className="flex items-center gap-2">
          {isOwnProfile && !editing && (
            <button
              onClick={() => setEditing(true)}
              className="inline-flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg text-sm transition-colors"
            >
              <FiEdit3 size={14} /> Edit Profile
            </button>
          )}
          {editing && (
            <>
              <button
                onClick={handleCancel}
                className="inline-flex items-center gap-2 border border-gray-300 hover:bg-gray-50 text-gray-700 font-medium py-2 px-4 rounded-lg text-sm transition-colors"
              >
                <FiX size={14} /> Cancel
              </button>
              <button
                onClick={handleSave}
                disabled={saving}
                className="inline-flex items-center gap-2 bg-green-600 hover:bg-green-700 disabled:bg-green-400 disabled:cursor-not-allowed text-white font-medium py-2 px-4 rounded-lg text-sm transition-colors"
              >
                {saving ? (
                  <>
                    <svg className="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                    </svg>
                    Saving...
                  </>
                ) : (
                  <>
                    <FiSave size={14} /> Save Changes
                  </>
                )}
              </button>
            </>
          )}
          {isDoctor && (
            <button
              onClick={handleDelete}
              disabled={deleting}
              className="inline-flex items-center gap-2 bg-red-600 hover:bg-red-700 disabled:bg-red-400 disabled:cursor-not-allowed text-white font-medium py-2 px-4 rounded-lg text-sm transition-colors"
            >
              {deleting ? (
                <>
                  <svg className="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                  </svg>
                  Deleting...
                </>
              ) : (
                <>
                  <FiTrash2 size={14} /> Delete
                </>
              )}
            </button>
          )}
        </div>
      </div>

      {/* Content Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Personal Info */}
        <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6">
          <div className="flex items-center gap-2 mb-5">
            <div className="p-2 rounded-lg bg-blue-100 text-blue-600">
              <FiUser size={18} />
            </div>
            <h2 className="text-lg font-semibold text-gray-900">Personal Information</h2>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {renderField('Full Name', patient.patientName, null, 'patientName')}
            {renderField('Email', patient.patientEmail, null, 'patientEmail', 'email')}
            {renderField('Phone', patient.patientPhone, null, 'patientPhone', 'tel')}
            {renderField('Date of Birth', formatDate(patient.dateOfBirth), null, 'dateOfBirth', 'date')}
            {renderField('Gender', patient.gender, null, 'gender', 'text', ['MALE', 'FEMALE', 'OTHER'])}
            <div>
              <span className={labelClass}>Member Since</span>
              <p className={valueClass}>{formatDateTime(patient.createdAt)}</p>
            </div>
          </div>
        </div>

        {/* Address */}
        <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6">
          <div className="flex items-center gap-2 mb-5">
            <div className="p-2 rounded-lg bg-purple-100 text-purple-600">
              <FiMapPin size={18} />
            </div>
            <h2 className="text-lg font-semibold text-gray-900">Address</h2>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {renderField('Street', patient.address?.street, 'address', 'street')}
            {renderField('City', patient.address?.city, 'address', 'city')}
            {renderField('State', patient.address?.state, 'address', 'state')}
            {renderField('Zip Code', patient.address?.zipCode, 'address', 'zipCode')}
          </div>
        </div>

        {/* Emergency Contact */}
        <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6">
          <div className="flex items-center gap-2 mb-5">
            <div className="p-2 rounded-lg bg-orange-100 text-orange-600">
              <FiPhone size={18} />
            </div>
            <h2 className="text-lg font-semibold text-gray-900">Emergency Contact</h2>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {renderField('Contact Name', patient.emergencyContact?.contactName, 'emergencyContact', 'contactName')}
            {renderField('Contact Phone', patient.emergencyContact?.contactPhone, 'emergencyContact', 'contactPhone', 'tel')}
            {renderField('Relationship', patient.emergencyContact?.relationship, 'emergencyContact', 'relationship')}
          </div>
        </div>

        {/* Medical Info */}
        <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6">
          <div className="flex items-center gap-2 mb-5">
            <div className="p-2 rounded-lg bg-red-100 text-red-600">
              <FiHeart size={18} />
            </div>
            <h2 className="text-lg font-semibold text-gray-900">Medical Information</h2>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {renderField(
              'Blood Group',
              patient.medicalInfo?.bloodGroup,
              'medicalInfo',
              'bloodGroup',
              'text',
              ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-']
            )}
            {renderField('Allergies', patient.medicalInfo?.allergies, 'medicalInfo', 'allergies')}
            {renderField('Chronic Conditions', patient.medicalInfo?.chronicConditions, 'medicalInfo', 'chronicConditions')}
          </div>
        </div>
      </div>

      {/* Last Updated */}
      {patient.updatedAt && (
        <p className="text-xs text-gray-400 text-right mt-4">
          Last updated: {formatDateTime(patient.updatedAt)}
        </p>
      )}
    </div>
  );
}
