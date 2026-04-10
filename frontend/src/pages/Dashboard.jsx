import { useAuth } from '../context/AuthContext';
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { FiUsers, FiFileText, FiUserPlus, FiActivity } from 'react-icons/fi';
import { getDoctors, getPatients, getPrescriptionsByDoctor, getPrescriptionsByPatient } from '../api/services';

export default function Dashboard() {
  const { user, role, isDoctor, isPatient } = useAuth();
  const [stats, setStats] = useState({ doctors: 0, patients: 0, prescriptions: 0 });
  const [recentPrescriptions, setRecentPrescriptions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const promises = [getDoctors()];
        if (isDoctor) {
          promises.push(getPatients());
          promises.push(getPrescriptionsByDoctor(user.doctorId));
        } else {
          promises.push(getPrescriptionsByPatient(user.patientId));
        }
        const results = await Promise.allSettled(promises);
        const doctorCount = results[0].status === 'fulfilled' ? results[0].value.data.count || results[0].value.data.data?.length || 0 : 0;
        if (isDoctor) {
          const patientCount = results[1].status === 'fulfilled' ? results[1].value.data.count || results[1].value.data.data?.length || 0 : 0;
          const prescData = results[2].status === 'fulfilled' ? results[2].value.data : { count: 0, data: [] };
          setStats({ doctors: doctorCount, patients: patientCount, prescriptions: prescData.count || prescData.data?.length || 0 });
          setRecentPrescriptions((prescData.data || []).slice(0, 5));
        } else {
          const prescData = results[1].status === 'fulfilled' ? results[1].value.data : { count: 0, data: [] };
          setStats({ doctors: doctorCount, patients: 0, prescriptions: prescData.count || prescData.data?.length || 0 });
          setRecentPrescriptions((prescData.data || []).slice(0, 5));
        }
      } catch (err) {
        console.error('Dashboard fetch error:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [isDoctor, isPatient, user]);

  if (loading) return <div className="flex items-center justify-center h-64"><div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div></div>;

  const statusColor = { ACTIVE: 'bg-green-100 text-green-800', COMPLETED: 'bg-blue-100 text-blue-800', INACTIVE: 'bg-gray-100 text-gray-800', CANCELLED: 'bg-red-100 text-red-800' };

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900">Welcome, {user?.doctorName || user?.patientName || 'User'}</h1>
        <p className="text-gray-500 mt-1">{isDoctor ? 'Doctor Dashboard' : 'Patient Dashboard'}</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <Link to="/doctors" className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500">Total Doctors</p>
              <p className="text-3xl font-bold text-gray-900 mt-1">{stats.doctors}</p>
            </div>
            <div className="bg-blue-100 p-3 rounded-lg"><FiUsers className="text-blue-600 text-xl" /></div>
          </div>
        </Link>
        {isDoctor && (
          <Link to="/patients" className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-500">Total Patients</p>
                <p className="text-3xl font-bold text-gray-900 mt-1">{stats.patients}</p>
              </div>
              <div className="bg-green-100 p-3 rounded-lg"><FiUserPlus className="text-green-600 text-xl" /></div>
            </div>
          </Link>
        )}
        <Link to="/prescriptions" className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500">{isDoctor ? 'My Prescriptions' : 'My Prescriptions'}</p>
              <p className="text-3xl font-bold text-gray-900 mt-1">{stats.prescriptions}</p>
            </div>
            <div className="bg-purple-100 p-3 rounded-lg"><FiFileText className="text-purple-600 text-xl" /></div>
          </div>
        </Link>
      </div>

      {isDoctor && (
        <div className="mb-8">
          <Link to="/prescriptions/create" className="inline-flex items-center gap-2 bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors font-medium">
            <FiFileText /> Create Prescription
          </Link>
        </div>
      )}

      <div className="bg-white rounded-xl shadow-sm border border-gray-200">
        <div className="px-6 py-4 border-b border-gray-200">
          <h2 className="text-lg font-semibold text-gray-900">Recent Prescriptions</h2>
        </div>
        {recentPrescriptions.length === 0 ? (
          <div className="px-6 py-8 text-center text-gray-500">No prescriptions found</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">{isDoctor ? 'Patient' : 'Doctor'}</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Diagnosis</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {recentPrescriptions.map((p) => (
                  <tr key={p.prescriptionId} className="hover:bg-gray-50">
                    <td className="px-6 py-4 text-sm text-gray-900">#{p.prescriptionId}</td>
                    <td className="px-6 py-4 text-sm text-gray-900">{isDoctor ? p.patientName : p.doctorName}</td>
                    <td className="px-6 py-4 text-sm text-gray-600 max-w-xs truncate">{p.diagnosis}</td>
                    <td className="px-6 py-4"><span className={`text-xs px-2 py-1 rounded-full font-medium ${statusColor[p.status] || 'bg-gray-100 text-gray-800'}`}>{p.status}</span></td>
                    <td className="px-6 py-4"><Link to={`/prescriptions/${p.prescriptionId}`} className="text-blue-600 hover:text-blue-800 text-sm font-medium">View</Link></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
