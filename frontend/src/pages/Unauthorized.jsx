import { Link } from 'react-router-dom';
import { FiAlertTriangle } from 'react-icons/fi';

export default function Unauthorized() {
  return (
    <div className="flex flex-col items-center justify-center h-[60vh] text-center">
      <FiAlertTriangle className="text-red-500 text-6xl mb-4" />
      <h1 className="text-2xl font-bold text-gray-900 mb-2">Access Denied</h1>
      <p className="text-gray-500 mb-6">You don't have permission to access this page.</p>
      <Link to="/dashboard" className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors font-medium">
        Go to Dashboard
      </Link>
    </div>
  );
}
