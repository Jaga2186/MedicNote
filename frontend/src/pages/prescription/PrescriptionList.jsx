import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { FiPlus, FiEye, FiList, FiUser } from 'react-icons/fi';
import {
  getPrescriptionsByDoctor,
  getPrescriptionsByPatient,
  getPrescriptionsPaginated,
} from '../../api/services';
import { useAuth } from '../../context/AuthContext';
import Pagination from '../../components/Pagination';

const STATUS_COLORS = {
  ACTIVE: 'bg-green-100 text-green-800',
  COMPLETED: 'bg-blue-100 text-blue-800',
  INACTIVE: 'bg-gray-100 text-gray-600',
  CANCELLED: 'bg-red-100 text-red-800',
};

export default function PrescriptionList() {
  const { user, isDoctor, isPatient } = useAuth();
  const navigate = useNavigate();

  const [prescriptions, setPrescriptions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [viewAll, setViewAll] = useState(false);

  // Pagination state (used only in "View All" mode)
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalItems, setTotalItems] = useState(0);
  const pageSize = 10;

  const fetchMyPrescriptions = useCallback(async () => {
    setLoading(true);
    try {
      let res;
      if (isDoctor) {
        res = await getPrescriptionsByDoctor(user.doctorId);
      } else if (isPatient) {
        res = await getPrescriptionsByPatient(user.patientId);
      }
      setPrescriptions(res.data.data || []);
    } catch (err) {
      const msg =
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Failed to load prescriptions';
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  }, [isDoctor, isPatient, user]);

  const fetchAllPrescriptions = useCallback(
    async (page = 0) => {
      setLoading(true);
      try {
        const res = await getPrescriptionsPaginated(page, pageSize, 'prescriptionId', 'desc');
        const payload = res.data;
        setPrescriptions(payload.data || []);
        setCurrentPage(payload.currentPage ?? page);
        setTotalPages(payload.totalPages ?? 1);
        setTotalItems(payload.totalItems ?? 0);
      } catch (err) {
        const msg =
          err.response?.data?.message ||
          err.response?.data?.error ||
          'Failed to load prescriptions';
        toast.error(msg);
      } finally {
        setLoading(false);
      }
    },
    [pageSize],
  );

  useEffect(() => {
    if (viewAll && isDoctor) {
      fetchAllPrescriptions(currentPage);
    } else {
      fetchMyPrescriptions();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [viewAll]);

  const handlePageChange = (page) => {
    setCurrentPage(page);
    fetchAllPrescriptions(page);
  };

  const handleToggleView = () => {
    setCurrentPage(0);
    setViewAll((prev) => !prev);
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  return (
    <div>
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Prescriptions</h1>
          <p className="text-gray-500 text-sm mt-1">
            {viewAll
              ? `All prescriptions (${totalItems} total)`
              : isDoctor
                ? 'Prescriptions you have issued'
                : 'Your prescriptions'}
          </p>
        </div>
        <div className="flex items-center gap-3">
          {isDoctor && (
            <>
              <button
                onClick={handleToggleView}
                className="inline-flex items-center gap-2 px-4 py-2 text-sm font-medium rounded-lg border border-gray-300 bg-white hover:bg-gray-50 text-gray-700 transition-colors"
              >
                {viewAll ? <FiUser className="text-base" /> : <FiList className="text-base" />}
                {viewAll ? 'My Prescriptions' : 'View All'}
              </button>
              <button
                onClick={() => navigate('/prescriptions/create')}
                className="inline-flex items-center gap-2 px-4 py-2 text-sm font-semibold rounded-lg bg-blue-600 hover:bg-blue-700 text-white transition-colors"
              >
                <FiPlus className="text-base" />
                Create Prescription
              </button>
            </>
          )}
        </div>
      </div>

      {/* Table Card */}
      <div className="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden">
        {loading ? (
          <div className="flex items-center justify-center py-20">
            <svg
              className="animate-spin h-8 w-8 text-blue-600"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
            >
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
              <path
                className="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
              />
            </svg>
          </div>
        ) : prescriptions.length === 0 ? (
          <div className="text-center py-20 text-gray-500">
            <FiList className="mx-auto text-4xl mb-3 text-gray-300" />
            <p className="text-lg font-medium">No prescriptions found</p>
            <p className="text-sm mt-1">
              {isDoctor
                ? 'Create your first prescription to get started.'
                : 'No prescriptions have been issued to you yet.'}
            </p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm text-left">
              <thead>
                <tr className="border-b border-gray-200 bg-gray-50">
                  <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">ID</th>
                  <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Doctor</th>
                  <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Patient</th>
                  <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Diagnosis</th>
                  <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Status</th>
                  <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Date</th>
                  <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {prescriptions.map((rx) => (
                  <tr key={rx.prescriptionId} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 font-medium text-gray-900">#{rx.prescriptionId}</td>
                    <td className="px-6 py-4 text-gray-700">{rx.doctorName || `Doctor #${rx.doctorId}`}</td>
                    <td className="px-6 py-4 text-gray-700">{rx.patientName || `Patient #${rx.patientId}`}</td>
                    <td className="px-6 py-4 text-gray-700 max-w-xs truncate">{rx.diagnosis || '—'}</td>
                    <td className="px-6 py-4">
                      <span
                        className={`inline-block px-2.5 py-0.5 rounded-full text-xs font-semibold ${STATUS_COLORS[rx.status] || 'bg-gray-100 text-gray-600'}`}
                      >
                        {rx.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-gray-500 whitespace-nowrap">{formatDate(rx.createdAt)}</td>
                    <td className="px-6 py-4">
                      <button
                        onClick={() => navigate(`/prescriptions/${rx.prescriptionId}`)}
                        className="inline-flex items-center gap-1.5 text-blue-600 hover:text-blue-800 font-medium transition-colors"
                      >
                        <FiEye className="text-base" />
                        View
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Pagination (View All mode only) */}
      {viewAll && !loading && prescriptions.length > 0 && (
        <Pagination currentPage={currentPage} totalPages={totalPages} onPageChange={handlePageChange} />
      )}
    </div>
  );
}
