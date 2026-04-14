import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { FiPlus, FiTrash2, FiArrowLeft, FiSave } from 'react-icons/fi';
import { createPrescription, getPatients } from '../../api/services';
import { useAuth } from '../../context/AuthContext';

const emptyMedication = () => ({
  medicineName: '',
  dosage: '',
  frequency: '',
  duration: '',
  instructions: '',
});

export default function PrescriptionCreate() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [patients, setPatients] = useState([]);
  const [loadingPatients, setLoadingPatients] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const [formData, setFormData] = useState({
    doctorId: user?.doctorId || '',
    patientId: '',
    diagnosis: '',
    notes: '',
    medications: [emptyMedication()],
  });

  useEffect(() => {
    const fetchPatients = async () => {
      try {
        const res = await getPatients();
        setPatients(res.data.data || []);
      } catch (err) {
        const msg =
          err.response?.data?.message ||
          err.response?.data?.error ||
          'Failed to load patients';
        toast.error(msg);
      } finally {
        setLoadingPatients(false);
      }
    };
    fetchPatients();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleMedicationChange = (index, e) => {
    const { name, value } = e.target;
    setFormData((prev) => {
      const medications = [...prev.medications];
      medications[index] = { ...medications[index], [name]: value };
      return { ...prev, medications };
    });
  };

  const addMedication = () => {
    setFormData((prev) => ({
      ...prev,
      medications: [...prev.medications, emptyMedication()],
    }));
  };

  const removeMedication = (index) => {
    setFormData((prev) => {
      const medications = prev.medications.filter((_, i) => i !== index);
      return { ...prev, medications: medications.length > 0 ? medications : [emptyMedication()] };
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.patientId) {
      toast.error('Please select a patient');
      return;
    }
    if (!formData.diagnosis.trim()) {
      toast.error('Please enter a diagnosis');
      return;
    }
    const hasEmptyMed = formData.medications.some((m) => !m.medicineName.trim());
    if (hasEmptyMed) {
      toast.error('Please fill in the medication name for all entries');
      return;
    }

    setSubmitting(true);
    try {
      const payload = {
        doctorId: Number(formData.doctorId),
        patientId: Number(formData.patientId),
        diagnosis: formData.diagnosis.trim(),
        notes: formData.notes.trim(),
        medications: formData.medications.map((m) => ({
          medicineName: m.medicineName.trim(),
          dosage: m.dosage.trim(),
          frequency: m.frequency.trim(),
          duration: m.duration.trim(),
          instructions: m.instructions.trim(),
        })),
      };

      const res = await createPrescription(payload);
      const created = res.data.data;
      toast.success(res.data.message || 'Prescription created successfully!');
      navigate(created?.prescriptionId ? `/prescriptions/${created.prescriptionId}` : '/prescriptions');
    } catch (err) {
      const msg =
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Failed to create prescription';
      toast.error(msg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto">
      {/* Header */}
      <div className="flex items-center gap-4 mb-6">
        <button
          onClick={() => navigate('/prescriptions')}
          className="p-2 rounded-lg border border-gray-300 hover:bg-gray-50 transition-colors"
        >
          <FiArrowLeft className="text-lg text-gray-600" />
        </button>
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Create Prescription</h1>
          <p className="text-gray-500 text-sm mt-1">Fill out the form below to issue a new prescription</p>
        </div>
      </div>

      {/* Form Card */}
      <form onSubmit={handleSubmit} className="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden">
        {/* Doctor Info (read-only) */}
        <div className="px-6 pt-6 pb-4 border-b border-gray-100">
          <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">Doctor Information</h2>
          <div className="flex items-center gap-3 bg-gray-50 rounded-lg px-4 py-3">
            <div className="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-bold text-sm">
              {user?.name?.charAt(0) || 'D'}
            </div>
            <div>
              <p className="text-sm font-medium text-gray-900">{user?.name || `Doctor #${user?.doctorId}`}</p>
              <p className="text-xs text-gray-500">Doctor ID: {user?.doctorId}</p>
            </div>
          </div>
        </div>

        {/* Patient Selection */}
        <div className="px-6 pt-5 pb-4 border-b border-gray-100">
          <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">Patient</h2>
          <select
            name="patientId"
            value={formData.patientId}
            onChange={handleChange}
            required
            disabled={loadingPatients}
            className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-shadow text-sm bg-white disabled:bg-gray-100 disabled:cursor-not-allowed"
          >
            <option value="">
              {loadingPatients ? 'Loading patients...' : '-- Select a patient --'}
            </option>
            {patients.map((p) => (
              <option key={p.patientId} value={p.patientId}>
                {p.name || p.patientName || `Patient #${p.patientId}`} {p.email ? `(${p.email})` : ''}
              </option>
            ))}
          </select>
        </div>

        {/* Diagnosis & Notes */}
        <div className="px-6 pt-5 pb-4 border-b border-gray-100 space-y-4">
          <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider">Diagnosis &amp; Notes</h2>
          <div>
            <label htmlFor="diagnosis" className="block text-sm font-medium text-gray-700 mb-1">
              Diagnosis <span className="text-red-500">*</span>
            </label>
            <textarea
              id="diagnosis"
              name="diagnosis"
              rows={3}
              required
              value={formData.diagnosis}
              onChange={handleChange}
              placeholder="Enter diagnosis details..."
              className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-shadow text-sm resize-none"
            />
          </div>
          <div>
            <label htmlFor="notes" className="block text-sm font-medium text-gray-700 mb-1">
              Notes <span className="text-gray-400 text-xs font-normal">(optional)</span>
            </label>
            <textarea
              id="notes"
              name="notes"
              rows={2}
              value={formData.notes}
              onChange={handleChange}
              placeholder="Additional notes..."
              className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-shadow text-sm resize-none"
            />
          </div>
        </div>

        {/* Medications */}
        <div className="px-6 pt-5 pb-6 space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider">
              Medications ({formData.medications.length})
            </h2>
            <button
              type="button"
              onClick={addMedication}
              className="inline-flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold rounded-lg bg-green-50 text-green-700 hover:bg-green-100 border border-green-200 transition-colors"
            >
              <FiPlus className="text-sm" />
              Add Medication
            </button>
          </div>

          {formData.medications.map((med, index) => (
            <div
              key={index}
              className="relative border border-gray-200 rounded-xl p-4 bg-gray-50/50 space-y-3"
            >
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-gray-400 uppercase">
                  Medication #{index + 1}
                </span>
                {formData.medications.length > 1 && (
                  <button
                    type="button"
                    onClick={() => removeMedication(index)}
                    className="inline-flex items-center gap-1 text-xs font-medium text-red-500 hover:text-red-700 transition-colors"
                  >
                    <FiTrash2 className="text-sm" />
                    Remove
                  </button>
                )}
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">
                    Medication Name <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="text"
                    name="medicineName"
                    value={med.medicineName}
                    onChange={(e) => handleMedicationChange(index, e)}
                    required
                    placeholder="e.g. Amoxicillin"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-shadow text-sm"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">Dosage</label>
                  <input
                    type="text"
                    name="dosage"
                    value={med.dosage}
                    onChange={(e) => handleMedicationChange(index, e)}
                    placeholder="e.g. 500mg"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-shadow text-sm"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">Frequency</label>
                  <input
                    type="text"
                    name="frequency"
                    value={med.frequency}
                    onChange={(e) => handleMedicationChange(index, e)}
                    placeholder="e.g. 3 times daily"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-shadow text-sm"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">Duration</label>
                  <input
                    type="text"
                    name="duration"
                    value={med.duration}
                    onChange={(e) => handleMedicationChange(index, e)}
                    placeholder="e.g. 7 days"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-shadow text-sm"
                  />
                </div>
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Instructions</label>
                <input
                  type="text"
                  name="instructions"
                  value={med.instructions}
                  onChange={(e) => handleMedicationChange(index, e)}
                  placeholder="e.g. Take after meals"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-shadow text-sm"
                />
              </div>
            </div>
          ))}
        </div>

        {/* Submit */}
        <div className="px-6 py-4 bg-gray-50 border-t border-gray-100 flex items-center justify-end gap-3">
          <button
            type="button"
            onClick={() => navigate('/prescriptions')}
            className="px-5 py-2.5 text-sm font-medium rounded-lg border border-gray-300 bg-white hover:bg-gray-50 text-gray-700 transition-colors"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={submitting}
            className="inline-flex items-center gap-2 px-5 py-2.5 text-sm font-semibold rounded-lg bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 disabled:cursor-not-allowed text-white transition-colors"
          >
            {submitting ? (
              <>
                <svg
                  className="animate-spin h-4 w-4 text-white"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                >
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                </svg>
                Creating...
              </>
            ) : (
              <>
                <FiSave className="text-base" />
                Create Prescription
              </>
            )}
          </button>
        </div>
      </form>
    </div>
  );
}
