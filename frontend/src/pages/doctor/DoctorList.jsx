import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { FiSearch, FiEye, FiTrash2, FiX, FiUsers } from 'react-icons/fi';
import { getDoctorsPaginated, getDoctorsBySpecialization, deleteDoctor } from '../../api/services';
import { useAuth } from '../../context/AuthContext';
import Pagination from '../../components/Pagination';

export default function DoctorList() {
  const navigate = useNavigate();
  const { isDoctor } = useAuth();

  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalItems, setTotalItems] = useState(0);

  const [specFilter, setSpecFilter] = useState('');
  const [activeFilter, setActiveFilter] = useState('');
  const [deleting, setDeleting] = useState(null);

  const fetchDoctors = useCallback(async (page = 0) => {
    setLoading(true);
    try {
      const res = await getDoctorsPaginated(page, 10, 'doctorId', 'asc');
      const payload = res.data.data;
      setDoctors(Array.isArray(payload) ? payload : payload.content || []);
      setCurrentPage(res.data.currentPage ?? page);
      setTotalPages(res.data.totalPages ?? 1);
      setTotalItems(res.data.totalItems ?? 0);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to fetch doctors');
      setDoctors([]);
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchBySpecialization = useCallback(async (spec) => {
    setLoading(true);
    try {
      const res = await getDoctorsBySpecialization(spec);
      const list = res.data.data || [];
      setDoctors(list);
      setCurrentPage(0);
      setTotalPages(1);
      setTotalItems(list.length);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to search by specialization');
      setDoctors([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (!activeFilter) {
      fetchDoctors(currentPage);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage, activeFilter]);

  const handleSearch = () => {
    const trimmed = specFilter.trim();
    if (!trimmed) {
      handleClearFilter();
      return;
    }
    setActiveFilter(trimmed);
    fetchBySpecialization(trimmed);
  };

  const handleClearFilter = () => {
    setSpecFilter('');
    setActiveFilter('');
    setCurrentPage(0);
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
    if (activeFilter) {
      // specialization search isn't paginated server-side, so we just keep the full list
      return;
    }
  };

  const handleDelete = async (id, name) => {
    if (!window.confirm(`Are you sure you want to delete Dr. ${name}?`)) return;
    setDeleting(id);
    try {
      const res = await deleteDoctor(id);
      toast.success(res.data.message || 'Doctor deleted successfully');
      if (activeFilter) {
        fetchBySpecialization(activeFilter);
      } else {
        fetchDoctors(currentPage);
      }
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to delete doctor');
    } finally {
      setDeleting(null);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') handleSearch();
  };

  return (
    <div>
      {/* Header */}
      <div className="mb-6">
        <div className="flex items-center gap-3 mb-1">
          <FiUsers className="text-blue-600" size={28} />
          <h1 className="text-2xl font-bold text-gray-900">Doctors</h1>
        </div>
        <p className="text-gray-500 text-sm">
          {activeFilter
            ? `Showing results for "${activeFilter}"`
            : `${totalItems} doctor${totalItems !== 1 ? 's' : ''} registered`}
        </p>
      </div>

      {/* Search / Filter Bar */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4 mb-6">
        <div className="flex flex-col sm:flex-row gap-3">
          <div className="relative flex-1">
            <input
              type="text"
              value={specFilter}
              onChange={(e) => setSpecFilter(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="Filter by specialization (e.g. Cardiology)"
              className="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
            />
            <FiSearch className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
          </div>
          <div className="flex gap-2">
            <button
              onClick={handleSearch}
              className="px-5 py-2.5 bg-blue-600 text-white rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors flex items-center gap-2"
            >
              <FiSearch size={16} />
              Search
            </button>
            {activeFilter && (
              <button
                onClick={handleClearFilter}
                className="px-4 py-2.5 bg-gray-100 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-200 transition-colors flex items-center gap-2"
              >
                <FiX size={16} />
                Clear
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Loading */}
      {loading && (
        <div className="flex items-center justify-center py-20">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      )}

      {/* Empty State */}
      {!loading && doctors.length === 0 && (
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-12 text-center">
          <FiUsers className="mx-auto text-gray-300 mb-4" size={48} />
          <h3 className="text-lg font-medium text-gray-900 mb-1">No doctors found</h3>
          <p className="text-gray-500 text-sm">
            {activeFilter
              ? `No doctors match the specialization "${activeFilter}". Try a different search term.`
              : 'There are no doctors registered in the system yet.'}
          </p>
          {activeFilter && (
            <button
              onClick={handleClearFilter}
              className="mt-4 text-blue-600 hover:text-blue-800 text-sm font-medium transition-colors"
            >
              Clear filter and show all
            </button>
          )}
        </div>
      )}

      {/* Table */}
      {!loading && doctors.length > 0 && (
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm text-left">
              <thead>
                <tr className="bg-gray-50 border-b border-gray-200">
                  <th className="px-6 py-3.5 text-xs font-semibold text-gray-500 uppercase tracking-wider">Name</th>
                  <th className="px-6 py-3.5 text-xs font-semibold text-gray-500 uppercase tracking-wider">Email</th>
                  <th className="px-6 py-3.5 text-xs font-semibold text-gray-500 uppercase tracking-wider hidden md:table-cell">Phone</th>
                  <th className="px-6 py-3.5 text-xs font-semibold text-gray-500 uppercase tracking-wider">Specialization</th>
                  <th className="px-6 py-3.5 text-xs font-semibold text-gray-500 uppercase tracking-wider hidden lg:table-cell">Hospital</th>
                  <th className="px-6 py-3.5 text-xs font-semibold text-gray-500 uppercase tracking-wider hidden lg:table-cell">Experience</th>
                  <th className="px-6 py-3.5 text-xs font-semibold text-gray-500 uppercase tracking-wider text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {doctors.map((doc) => (
                  <tr
                    key={doc.doctorId}
                    className="hover:bg-blue-50/40 transition-colors"
                  >
                    <td className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap">
                      {doc.doctorName}
                    </td>
                    <td className="px-6 py-4 text-gray-600 whitespace-nowrap">{doc.doctorEmail}</td>
                    <td className="px-6 py-4 text-gray-600 whitespace-nowrap hidden md:table-cell">{doc.doctorPhone}</td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                        {doc.specialization}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-gray-600 whitespace-nowrap hidden lg:table-cell">{doc.hospitalName || '-'}</td>
                    <td className="px-6 py-4 text-gray-600 whitespace-nowrap hidden lg:table-cell">
                      {doc.experienceYears != null ? `${doc.experienceYears} yr${doc.experienceYears !== 1 ? 's' : ''}` : '-'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right">
                      <div className="flex items-center justify-end gap-2">
                        <button
                          onClick={() => navigate(`/doctors/${doc.doctorId}`)}
                          className="p-2 text-blue-600 hover:bg-blue-100 rounded-lg transition-colors"
                          title="View details"
                        >
                          <FiEye size={16} />
                        </button>
                        {isDoctor && (
                          <button
                            onClick={() => handleDelete(doc.doctorId, doc.doctorName)}
                            disabled={deleting === doc.doctorId}
                            className="p-2 text-red-600 hover:bg-red-100 rounded-lg transition-colors disabled:opacity-50"
                            title="Delete doctor"
                          >
                            {deleting === doc.doctorId ? (
                              <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-red-600"></div>
                            ) : (
                              <FiTrash2 size={16} />
                            )}
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          {!activeFilter && (
            <div className="border-t border-gray-200 px-6 py-3">
              <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={handlePageChange}
              />
            </div>
          )}
        </div>
      )}
    </div>
  );
}
