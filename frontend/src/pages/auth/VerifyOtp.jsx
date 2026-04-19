import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { FiShield } from 'react-icons/fi';
import { verifyOtp } from '../../api/services';
import { useAuth } from '../../context/AuthContext';

export default function VerifyOtp() {
    const [otp, setOtp] = useState(['', '', '', '', '', '']);
    const [loading, setLoading] = useState(false);
    const inputRefs = useRef([]);
    const { login, isAuthenticated } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (isAuthenticated) navigate('/dashboard', { replace: true });

        // If no sessionToken, redirect back to login
        if (!sessionStorage.getItem('sessionToken')) {
            toast.error('Session expired. Please login again.');
            navigate('/login', { replace: true });
        }
    }, [isAuthenticated, navigate]);

    const handleChange = (index, value) => {
        // Allow only digits
        if (!/^\d*$/.test(value)) return;

        const newOtp = [...otp];
        newOtp[index] = value.slice(-1); // only last digit
        setOtp(newOtp);

        // Auto focus next input
        if (value && index < 5) {
            inputRefs.current[index + 1]?.focus();
        }
    };

    const handleKeyDown = (index, e) => {
        // On backspace, go to previous input
        if (e.key === 'Backspace' && !otp[index] && index > 0) {
            inputRefs.current[index - 1]?.focus();
        }
    };

    const handlePaste = (e) => {
        e.preventDefault();
        const pasted = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6);
        const newOtp = [...otp];
        pasted.split('').forEach((char, i) => {
            newOtp[i] = char;
        });
        setOtp(newOtp);
        // Focus last filled input
        inputRefs.current[Math.min(pasted.length, 5)]?.focus();
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const otpCode = otp.join('');

        if (otpCode.length !== 6) {
            toast.error('Please enter the complete 6-digit OTP');
            return;
        }

        const sessionToken = sessionStorage.getItem('sessionToken');
        if (!sessionToken) {
            toast.error('Session expired. Please login again.');
            navigate('/login', { replace: true });
            return;
        }

        setLoading(true);
        try {
            const res = await verifyOtp({ sessionToken, otpCode });
            const { token, role, data: userData, message } = res.data;

            // Save JWT and clear session token
            login(token, role, userData);
            sessionStorage.removeItem('sessionToken');
            sessionStorage.removeItem('loginRole');

            toast.success(message || 'Login successful!');
            navigate('/dashboard');

        } catch (err) {
            const msg =
                err.response?.data?.message ||
                err.response?.data?.error ||
                'Invalid OTP. Please try again.';
            toast.error(msg);
        } finally {
            setLoading(false);
        }
    };

    const handleBackToLogin = () => {
        sessionStorage.removeItem('sessionToken');
        sessionStorage.removeItem('loginRole');
        navigate('/login');
    };

    return (
        <div className="min-h-[80vh] flex items-center justify-center px-4">
            <div className="w-full max-w-md">
                {/* Header */}
                <div className="text-center mb-8">
                    <div className="flex justify-center mb-4">
                        <div className="bg-blue-100 p-4 rounded-full">
                            <FiShield className="text-blue-600 text-3xl" />
                        </div>
                    </div>
                    <h1 className="text-3xl font-bold text-gray-900">Verify OTP</h1>
                    <p className="text-gray-500 mt-2">
                        Enter the 6-digit code sent to your registered email
                    </p>
                </div>

                {/* Card */}
                <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-8">
                    <form onSubmit={handleSubmit} className="space-y-6">
                        {/* OTP Inputs */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-4 text-center">
                                Enter OTP
                            </label>
                            <div className="flex justify-center gap-3" onPaste={handlePaste}>
                                {otp.map((digit, index) => (
                                    <input
                                        key={index}
                                        ref={(el) => (inputRefs.current[index] = el)}
                                        type="text"
                                        inputMode="numeric"
                                        maxLength={1}
                                        value={digit}
                                        onChange={(e) => handleChange(index, e.target.value)}
                                        onKeyDown={(e) => handleKeyDown(index, e)}
                                        className="w-12 h-12 text-center text-xl font-bold border-2 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-shadow"
                                    />
                                ))}
                            </div>
                            <p className="text-center text-xs text-gray-400 mt-3">
                                OTP is valid for 5 minutes
                            </p>
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
                                    Verifying...
                                </>
                            ) : (
                                'Verify OTP'
                            )}
                        </button>

                        {/* Back to login */}
                        <p className="text-center text-sm text-gray-500">
                            Didn&apos;t receive the OTP?{' '}
                            <button
                                type="button"
                                onClick={handleBackToLogin}
                                className="text-blue-600 hover:text-blue-700 font-medium"
                            >
                                Back to Login
                            </button>
                        </p>
                    </form>
                </div>
            </div>
        </div>
    );
}