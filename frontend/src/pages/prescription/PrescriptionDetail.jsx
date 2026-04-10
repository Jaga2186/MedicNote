import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import {
  FiArrowLeft,
  FiDownload,
  FiMail,
  FiCheckCircle,
  FiTrash2,
} from 'react-icons/fi';
import {
  getPrescriptionById,
  downloadPrescriptionPdf,
  emailPrescription,
  updatePrescriptionStatus,
  deletePrescription,
} from '../../api/services';
import { useAuth } from '../../context/AuthContext';

const STATUS_COLORS = {
  ACTIVE: 'bg-green-100 text-green-800',
  COMPLETED: 'bg-blue-100 text-blue-800',
  INACTIVE: 'bg-gray-100 text-gray-600',
  CANCELLED: 'bg-red-100 text-red-800',
};

export default function PrescriptionDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isDoctor } = useAuth();

  const [prescription, setPrescription] = useState(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState('');

  useEffect(() => {
    const fetchPrescription = async () => {
      setLoading(true);
      try {
        const res = await getPrescriptionById(id);
        setPrescription(res.data.data);
      } catch (err) {
        const msg =
          err.response?.data?.message ||
          err.response?.data?.error ||
          'Failed to load prescription';
        toast.error(msg);
      } finally {
        setLoading(false);
      }
    };
    fetchPrescription();
  }, [id]);

  const formatDate = (dateStr) => {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const handleDownload = async () => {
    setActionLoading('download');
    try {
      const response = await downloadPrescriptionPdf(id);
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `Prescription_${id}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
      toast.success('PDF downloaded');
    } catch (err) {
      toast.error('Failed to download PDF');
    } finally {
      setActionLoading('');
    }
  };

  const handleEmail = async () => {
    setActionLoading('email');
    try {
      const res = await emailPrescription(id);
      toast.success(res.data.message || 'Prescription emailed to patient');
    } catch (err) {
      const msg =
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Failed to email prescription';
      toast.error(msg);
    } finally {
      setActionLoading('');
    }
  };

  const handleMarkCompleted = async () => {
    setActionLoading('complete');
    try {
      const res = await updatePrescriptionStatus(id, 'COMPLETED');
      setPrescription(res.data.data);
      toast.success(res.data.message || 'Prescription marked as completed');
    } catch (err) {
      const msg =
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Failed to update status';
      toast.error(msg);
    } finally {
      setActionLoading('');
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('Are you sure you want to delete this prescription? This action cannot be undone.')) {
      return;
    }
    setActionLoading('delete');
    try {
      await deletePrescription(id);
      toast.success('Prescription deleted');
      navigate('/prescriptions');
    } catch (err) {
      const msg =
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Failed to delete prescription';
      toast.error(msg);
    } finally {
      setActionLoading('');
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <svg
          className="animate-spin h-8 w-8 text-blue-600"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
        >
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
        </svg>
      </div>
    );
  }

  if (!prescription) {
    return (
      <div className="text-center py-20">
        <p className="text-gray-500 text-lg">Prescription not found.</p>
        <button
          onClick={() => navigate('/prescriptions')}
          className="mt-4 inline-flex items-center gap-2 text-blue-600 hover:text-blue-800 font-medium"
        >
          <FiArrowLeft /> Back to Prescriptions
        </button>
      </div>
    );
  }

  const rx = prescription;

  return (
    <div className="max-w-4xl mx-auto">
      {/* Top Navigation */}
      <button
        onClick={() => navigate('/prescriptions')}
        className="inline-flex items-center gap-2 text-sm text-gray-500 hover:text-gray-800 font-medium mb-4 transition-colors"
      >
        <FiArrowLeft /> Back to Prescriptions
      </button>

      {/* Header Card */}
      <div className="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden mb-6">
        <div className="px-6 py-5 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 border-b border-gray-100">
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-2xl font-bold text-gray-900">Prescription #{rx.prescriptionId}</h1>
              <span
                className={`inline-block px-3 py-1 rounded-full text-xs font-semibold ${STATUS_COLORS[rx.status] || 'bg-gray-100 text-gray-600'}`}
              >
                {rx.status}
              </span>
            </div>
            <p className="text-sm text-gray-500 mt-1">Created: {formatDate(rx.createdAt)}</p>
            {rx.updatedAt && rx.updatedAt !== rx.createdAt && (
              <p className="text-xs text-gray-400">Updated: {formatDate(rx.updatedAt)}</p>
            )}
          </div>

          {/* Action Buttons */}
          <div className="flex flex-wrap items-center gap-2">
            <button
              onClick={handleDownload}
              disabled={!!actionLoading}
              className="inline-flex items-center gap-1.5 px-3 py-2 text-xs font-semibold rounded-lg border border-gray-300 bg-white hover:bg-gray-50 text-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              {actionLoading === 'download' ? (
                <svg className="animate-spin h-3.5 w-3.5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                </svg>
              ) : (
                <FiDownload className="text-sm" />
              )}
              Download PDF
            </button>

            {isDoctor && (
              <>
                <button
                  onClick={handleEmail}
                  disabled={!!actionLoading}
                  className="inline-flex items-center gap-1.5 px-3 py-2 text-xs font-semibold rounded-lg border border-blue-200 bg-blue-50 hover:bg-blue-100 text-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  {actionLoading === 'email' ? (
                    <svg className="animate-spin h-3.5 w-3.5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                    </svg>
                  ) : (
                    <FiMail className="text-sm" />
                  )}
                  Email to Patient
                </button>

                {rx.status === 'ACTIVE' && (
                  <button
                    onClick={handleMarkCompleted}
                    disabled={!!actionLoading}
                    className="inline-flex items-center gap-1.5 px-3 py-2 text-xs font-semibold rounded-lg border border-green-200 bg-green-50 hover:bg-green-100 text-green-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                  >
                    {actionLoading === 'complete' ? (
                      <svg className="animate-spin h-3.5 w-3.5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                      </svg>
                    ) : (
                      <FiCheckCircle className="text-sm" />
                    )}
                    Mark Completed
                  </button>
                )}

                <button
                  onClick={handleDelete}
                  disabled={!!actionLoading}
                  className="inline-flex items-center gap-1.5 px-3 py-2 text-xs font-semibold rounded-lg border border-red-200 bg-red-50 hover:bg-red-100 text-red-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  {actionLoading === 'delete' ? (
                    <svg className="animate-spin h-3.5 w-3.5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                    </svg>
                  ) : (
                    <FiTrash2 className="text-sm" />
                  )}
                  Delete
                </button>
              </>
            )}
          </div>
        </div>

        {/* Doctor & Patient Info */}
        <div className="grid grid-cols-1 sm:grid-cols-2 divide-y sm:divide-y-0 sm:divide-x divide-gray-100">
          <div className="px-6 py-4">
            <h3 className="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-2">Doctor</h3>
            <p className="text-sm font-medium text-gray-900">{rx.doctorName || '—'}</p>
            <p className="text-xs text-gray-500">ID: {rx.doctorId}</p>
          </div>
          <div className="px-6 py-4">
            <h3 className="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-2">Patient</h3>
            <p className="text-sm font-medium text-gray-900">{rx.patientName || '—'}</p>
            <p className="text-xs text-gray-500">ID: {rx.patientId}</p>
          </div>
        </div>
      </div>

      {/* Diagnosis & Notes */}
      <div className="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden mb-6">
        <div className="px-6 py-4 border-b border-gray-100">
          <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider">Diagnosis</h2>
          <p className="text-sm text-gray-800 mt-2 whitespace-pre-wrap">{rx.diagnosis || '—'}</p>
        </div>
        <div className="px-6 py-4">
          <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider">Notes</h2>
          <p className="text-sm text-gray-800 mt-2 whitespace-pre-wrap">{rx.notes || 'No additional notes.'}</p>
        </div>
      </div>

      {/* Medications Table */}
      <div className="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden mb-6">
        <div className="px-6 py-4 border-b border-gray-100">
          <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider">
            Medications ({rx.medications?.length || 0})
          </h2>
        </div>

        {rx.medications && rx.medications.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full text-sm text-left">
              <thead>
                <tr className="border-b border-gray-200 bg-gray-50">
                  <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">#</th>
                  <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Name</th>
                  <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Dosage</th>
                  <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Frequency</th>
                  <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Duration</th>
                  <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Instructions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {rx.medications.map((med, index) => (
                  <tr key={index} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 text-gray-500">{index + 1}</td>
                    <td className="px-6 py-4 font-medium text-gray-900">{med.medicationName || '—'}</td>
                    <td className="px-6 py-4 text-gray-700">{med.dosage || '—'}</td>
                    <td className="px-6 py-4 text-gray-700">{med.frequency || '—'}</td>
                    <td className="px-6 py-4 text-gray-700">{med.duration || '—'}</td>
                    <td className="px-6 py-4 text-gray-700 max-w-xs">{med.instructions || '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="text-center py-10 text-gray-400 text-sm">No medications listed.</div>
        )}
      </div>
    </div>
  );
}
