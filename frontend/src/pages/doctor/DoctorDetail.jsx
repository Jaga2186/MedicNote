import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { FiArrowLeft, FiEdit3, FiTrash2, FiSave, FiX, FiUser, FiMail, FiPhone, FiBriefcase, FiMapPin, FiAward, FiHash, FiCheckCircle, FiClock } from 'react-icons/fi';
import { getDoctorById, updateDoctor, deleteDoctor } from '../../api/services';
import { useAuth } from '../../context/AuthContext';

const EDITABLE_FIELDS = [
  { key: 'doctorName', label: 'Full Name', icon: FiUser, type: 'text' },
  { key: 'doctorEmail', label: 'Email', icon: FiMail, type: 'email' },
  { key: 'doctorPhone', label: 'Phone', icon: FiPhone, type: 'text' },
  { key: 'specialization', label: 'Specialization', icon: FiBriefcase, type: 'text' },
  { key: 'licenseNumber', label: 'License Number', icon: FiHash, type: 'text' },
  { key: 'experienceYears', label: 'Experience (years)', icon: FiAward, type: 'number' },
  { key: 'hospitalName', label: 'Hospital', icon: FiMapPin, type: 'text' },
];

export default function DoctorDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isDoctor, user } = useAuth();

  const [doctor, setDoctor] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState({});
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(false);

  const isOwner = isDoctor && user?.doctorId?.toString() === id?.toString();

  useEffect(() => {
    fetchDoctor();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const fetchDoctor = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await getDoctorById(id);
      const doc = res.data.data;
      setDoctor(doc);
      setFormData({
        doctorName: doc.doctorName || '',
        doctorEmail: doc.doctorEmail || '',
        doctorPhone: doc.doctorPhone || '',
        specialization: doc.specialization || '',
        licenseNumber: doc.licenseNumber || '',
        experienceYears: doc.experienceYears ?? '',
        hospitalName: doc.hospitalName || '',
      });
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to load doctor details';
      setError(msg);
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (key, value) => {
    setFormData((prev) => ({ ...prev, [key]: value }));
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      const payload = {
        ...formData,
        experienceYears: formData.experienceYears !== '' ? Number(formData.experienceYears) : null,
      };
      const res = await updateDoctor(id, payload);
      const updated = res.data.data;
      setDoctor(updated);
      setEditing(false);
      toast.success(res.data.message || 'Doctor updated successfully');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to update doctor');
    } finally {
      setSaving(false);
    }
  };

  const handleCancelEdit = () => {
    setEditing(false);
    if (doctor) {
      setFormData({
        doctorName: doctor.doctorName || '',
        doctorEmail: doctor.doctorEmail || '',
        doctorPhone: doctor.doctorPhone || '',
        specialization: doctor.specialization || '',
        licenseNumber: doctor.licenseNumber || '',
        experienceYears: doctor.experienceYears ?? '',
        hospitalName: doctor.hospitalName || '',
      });
    }
  };

  const handleDelete = async () => {
    if (!window.confirm(`Are you sure you want to delete Dr. ${doctor?.doctorName}? This action cannot be undone.`)) {
      return;
    }
    setDeleting(true);
    try {
      const res = await deleteDoctor(id);
      toast.success(res.data.message || 'Doctor deleted successfully');
      navigate('/doctors');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to delete doctor');
    } finally {
      setDeleting(false);
    }
  };

  // Loading State
  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  // Error State
  if (error) {
    return (
      <div className="max-w-2xl mx-auto">
        <button
          onClick={() => navigate('/doctors')}
          className="flex items-center gap-2 text-gray-600 hover:text-blue-600 text-sm font-medium mb-6 transition-colors"
        >
          <FiArrowLeft size={16} />
          Back to Doctors
        </button>
        <div className="bg-white rounded-xl shadow-sm border border-red-200 p-8 text-center">
          <div className="text-red-400 mb-3">
            <FiX className="mx-auto" size={48} />
          </div>
          <h3 className="text-lg font-medium text-gray-900 mb-1">Error Loading Doctor</h3>
          <p className="text-gray-500 text-sm mb-4">{error}</p>
          <button
            onClick={fetchDoctor}
            className="text-blue-600 hover:text-blue-800 text-sm font-medium transition-colors"
          >
            Try again
          </button>
        </div>
      </div>
    );
  }

  if (!doctor) return null;

  return (
    <div className="max-w-4xl mx-auto">
      {/* Back Button & Actions */}
      <div className="flex items-center justify-between mb-6">
        <button
          onClick={() => navigate('/doctors')}
          className="flex items-center gap-2 text-gray-600 hover:text-blue-600 text-sm font-medium transition-colors"
        >
          <FiArrowLeft size={16} />
          Back to Doctors
        </button>
        <div className="flex items-center gap-2">
          {isOwner && !editing && (
            <button
              onClick={() => setEditing(true)}
              className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors"
            >
              <FiEdit3 size={15} />
              Edit
            </button>
          )}
          {isDoctor && (
            <button
              onClick={handleDelete}
              disabled={deleting}
              className="flex items-center gap-2 px-4 py-2 bg-red-600 text-white rounded-lg text-sm font-medium hover:bg-red-700 transition-colors disabled:opacity-50"
            >
              {deleting ? (
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
              ) : (
                <FiTrash2 size={15} />
              )}
              Delete
            </button>
          )}
        </div>
      </div>

      {/* Doctor Header Card */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 mb-6">
        <div className="flex items-start gap-4">
          <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center flex-shrink-0">
            <FiUser className="text-blue-600" size={28} />
          </div>
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-3 flex-wrap">
              <h1 className="text-2xl font-bold text-gray-900">{doctor.doctorName}</h1>
              <span
                className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                  doctor.isActive
                    ? 'bg-green-100 text-green-800'
                    : 'bg-gray-100 text-gray-600'
                }`}
              >
                {doctor.isActive ? 'Active' : 'Inactive'}
              </span>
              {isOwner && (
                <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-purple-100 text-purple-800">
                  You
                </span>
              )}
            </div>
            <p className="text-gray-500 mt-1">{doctor.specialization}</p>
            {doctor.hospitalName && (
              <p className="text-gray-400 text-sm mt-0.5">{doctor.hospitalName}</p>
            )}
          </div>
        </div>
      </div>

      {/* Detail / Edit Card */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
        <div className="px-6 py-4 bg-gray-50 border-b border-gray-200 flex items-center justify-between">
          <h2 className="text-sm font-semibold text-gray-700 uppercase tracking-wider">
            {editing ? 'Edit Doctor Information' : 'Doctor Information'}
          </h2>
          {editing && (
            <div className="flex items-center gap-2">
              <button
                onClick={handleCancelEdit}
                disabled={saving}
                className="flex items-center gap-1.5 px-3 py-1.5 border border-gray-300 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-50 transition-colors disabled:opacity-50"
              >
                <FiX size={14} />
                Cancel
              </button>
              <button
                onClick={handleSave}
                disabled={saving}
                className="flex items-center gap-1.5 px-4 py-1.5 bg-blue-600 text-white rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors disabled:opacity-50"
              >
                {saving ? (
                  <div className="animate-spin rounded-full h-3.5 w-3.5 border-b-2 border-white"></div>
                ) : (
                  <FiSave size={14} />
                )}
                Save Changes
              </button>
            </div>
          )}
        </div>

        <div className="p-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {EDITABLE_FIELDS.map(({ key, label, icon: Icon, type }) => (
              <div key={key} className="space-y-1.5">
                <label className="flex items-center gap-2 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  <Icon size={13} />
                  {label}
                </label>
                {editing ? (
                  <input
                    type={type}
                    value={formData[key] ?? ''}
                    onChange={(e) => handleChange(key, e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-900 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                  />
                ) : (
                  <p className="text-gray-900 text-sm font-medium py-2">
                    {key === 'experienceYears'
                      ? doctor[key] != null
                        ? `${doctor[key]} year${doctor[key] !== 1 ? 's' : ''}`
                        : '-'
                      : doctor[key] || '-'}
                  </p>
                )}
              </div>
            ))}

            {/* Read-only fields */}
            <div className="space-y-1.5">
              <label className="flex items-center gap-2 text-xs font-medium text-gray-500 uppercase tracking-wider">
                <FiCheckCircle size={13} />
                Status
              </label>
              <p className="text-sm font-medium py-2">
                <span
                  className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                    doctor.isActive
                      ? 'bg-green-100 text-green-800'
                      : 'bg-gray-100 text-gray-600'
                  }`}
                >
                  {doctor.isActive ? 'Active' : 'Inactive'}
                </span>
              </p>
            </div>

            <div className="space-y-1.5">
              <label className="flex items-center gap-2 text-xs font-medium text-gray-500 uppercase tracking-wider">
                <FiHash size={13} />
                Doctor ID
              </label>
              <p className="text-gray-900 text-sm font-medium py-2">{doctor.doctorId}</p>
            </div>
          </div>

          {/* Timestamps */}
          <div className="mt-8 pt-6 border-t border-gray-100">
            <div className="flex flex-wrap gap-6 text-xs text-gray-400">
              {doctor.createdAt && (
                <div className="flex items-center gap-1.5">
                  <FiClock size={12} />
                  <span>Created: {new Date(doctor.createdAt).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' })}</span>
                </div>
              )}
              {doctor.updatedAt && (
                <div className="flex items-center gap-1.5">
                  <FiClock size={12} />
                  <span>Updated: {new Date(doctor.updatedAt).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' })}</span>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
