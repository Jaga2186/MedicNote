import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { FiEye, FiTrash2, FiUsers, FiSearch } from 'react-icons/fi';
import { getPatientsPaginated, deletePatient } from '../../api/services';
import Pagination from '../../components/Pagination';

export default function PatientList() {
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalItems, setTotalItems] = useState(0);
  const [deletingId, setDeletingId] = useState(null);
  const navigate = useNavigate();

  const pageSize = 10;

  const fetchPatients = useCallback(async (page) => {
    setLoading(true);
    try {
      const res = await getPatientsPaginated(page, pageSize, 'patientId', 'asc');
      const { data, currentPage: cp, totalPages: tp, totalItems: ti } = res.data.data;
      setPatients(data);
      setCurrentPage(cp);
      setTotalPages(tp);
      setTotalItems(ti);
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to load patients.';
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchPatients(0);
  }, [fetchPatients]);

  const handlePageChange = (page) => {
    fetchPatients(page);
  };

  const handleDelete = async (id, name) => {
    if (!window.confirm(`Are you sure you want to delete patient "${name}"? This action cannot be undone.`)) return;
    setDeletingId(id);
    try {
      const res = await deletePatient(id);
      toast.success(res.data?.message || 'Patient deleted successfully.');
      fetchPatients(currentPage);
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to delete patient.';
      toast.error(msg);
    } finally {
      setDeletingId(null);
    }
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  // Loading state
  if (loading && patients.length === 0) {
    return (
      <div className="flex items-center justify-center h-[60vh]">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-500 text-sm">Loading patients...</p>
        </div>
      </div>
    );
  }

  return (
    <div>
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
            <FiUsers className="text-blue-600" />
            Patients
          </h1>
          <p className="text-gray-500 text-sm mt-1">
            {totalItems} patient{totalItems !== 1 ? 's' : ''} registered
          </p>
        </div>
      </div>

      {/* Table */}
      {patients.length === 0 ? (
        <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-12 text-center">
          <FiSearch className="mx-auto text-gray-300 mb-4" size={48} />
          <h3 className="text-lg font-semibold text-gray-700">No patients found</h3>
          <p className="text-gray-500 text-sm mt-1">There are no registered patients yet.</p>
        </div>
      ) : (
        <div className="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-gray-50 border-b border-gray-200">
                  <th className="text-left py-3.5 px-4 font-semibold text-gray-600 whitespace-nowrap">Name</th>
                  <th className="text-left py-3.5 px-4 font-semibold text-gray-600 whitespace-nowrap">Email</th>
                  <th className="text-left py-3.5 px-4 font-semibold text-gray-600 whitespace-nowrap hidden md:table-cell">Phone</th>
                  <th className="text-left py-3.5 px-4 font-semibold text-gray-600 whitespace-nowrap hidden lg:table-cell">DOB</th>
                  <th className="text-left py-3.5 px-4 font-semibold text-gray-600 whitespace-nowrap hidden lg:table-cell">Gender</th>
                  <th className="text-left py-3.5 px-4 font-semibold text-gray-600 whitespace-nowrap hidden xl:table-cell">Blood Group</th>
                  <th className="text-right py-3.5 px-4 font-semibold text-gray-600 whitespace-nowrap">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {patients.map((p) => (
                  <tr key={p.patientId} className="hover:bg-gray-50/50 transition-colors">
                    <td className="py-3 px-4">
                      <div className="flex items-center gap-3">
                        <div className="h-8 w-8 rounded-full bg-blue-100 text-blue-700 flex items-center justify-center text-xs font-bold shrink-0">
                          {p.patientName?.charAt(0)?.toUpperCase() || '?'}
                        </div>
                        <div>
                          <span className="font-medium text-gray-900 whitespace-nowrap">{p.patientName}</span>
                          {!p.isActive && (
                            <span className="ml-2 bg-red-100 text-red-700 text-xs px-1.5 py-0.5 rounded-full">Inactive</span>
                          )}
                        </div>
                      </div>
                    </td>
                    <td className="py-3 px-4 text-gray-600 max-w-[200px] truncate">{p.patientEmail}</td>
                    <td className="py-3 px-4 text-gray-600 hidden md:table-cell whitespace-nowrap">{p.patientPhone || '—'}</td>
                    <td className="py-3 px-4 text-gray-600 hidden lg:table-cell whitespace-nowrap">{formatDate(p.dateOfBirth)}</td>
                    <td className="py-3 px-4 hidden lg:table-cell">
                      {p.gender ? (
                        <span className={`inline-block text-xs font-medium px-2 py-0.5 rounded-full ${
                          p.gender === 'MALE' ? 'bg-sky-100 text-sky-700' :
                          p.gender === 'FEMALE' ? 'bg-pink-100 text-pink-700' :
                          'bg-gray-100 text-gray-700'
                        }`}>
                          {p.gender}
                        </span>
                      ) : '—'}
                    </td>
                    <td className="py-3 px-4 hidden xl:table-cell">
                      {p.medicalInfo?.bloodGroup ? (
                        <span className="inline-block text-xs font-semibold bg-red-50 text-red-700 px-2 py-0.5 rounded-full">
                          {p.medicalInfo.bloodGroup}
                        </span>
                      ) : '—'}
                    </td>
                    <td className="py-3 px-4">
                      <div className="flex items-center justify-end gap-2">
                        <button
                          onClick={() => navigate(`/patients/${p.patientId}`)}
                          className="p-2 rounded-lg text-blue-600 hover:bg-blue-50 transition-colors"
                          title="View Details"
                        >
                          <FiEye size={16} />
                        </button>
                        <button
                          onClick={() => handleDelete(p.patientId, p.patientName)}
                          disabled={deletingId === p.patientId}
                          className="p-2 rounded-lg text-red-600 hover:bg-red-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                          title="Delete Patient"
                        >
                          {deletingId === p.patientId ? (
                            <svg className="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                            </svg>
                          ) : (
                            <FiTrash2 size={16} />
                          )}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Loading overlay for page transitions */}
          {loading && patients.length > 0 && (
            <div className="flex justify-center py-4 border-t border-gray-100">
              <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
            </div>
          )}

          {/* Pagination */}
          <div className="border-t border-gray-100 px-4 py-3">
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={handlePageChange}
            />
          </div>
        </div>
      )}
    </div>
  );
}
