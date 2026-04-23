import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { passwordResetApi } from '../../services/api';
import { Lock, CheckCircle2, AlertCircle, Factory, ChevronLeft } from 'lucide-react';
import toast from 'react-hot-toast';

const FEATURES = [
  { icon: '📦', label: 'Inventory Control' },
  { icon: '⚙️', label: 'Production Planning' },
  { icon: '🛒', label: 'Procurement' },
  { icon: '🚚', label: 'Logistics Tracking' },
  { icon: '📊', label: 'Live Analytics' },
  { icon: '🔔', label: 'Smart Alerts' },
];

export default function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [newPassword, setNew] = useState('');
  const [confirmPassword, setConfirm] = useState('');
  const [loading, setLoading] = useState(false);
  const [validating, setValidating] = useState(true);
  const [tokenError, setTokenError] = useState('');
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    if (!token) {
      setTokenError('No reset token found. Please request a new link.');
      setValidating(false);
      return;
    }
    passwordResetApi.validateToken(token)
      .then(res => {
        const msg = res.data?.message || '';
        const emailMatch = msg.match(/Email:\s*(.+)/);
        if (emailMatch) setEmail(emailMatch[1].trim());
        setValidating(false);
      })
      .catch(err => {
        setTokenError(err.response?.data?.message || 'Invalid or expired reset link.');
        setValidating(false);
      });
  }, [token]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (newPassword.length < 6) {
      toast.error('Password must be at least 6 characters.');
      return;
    }
    if (newPassword !== confirmPassword) {
      toast.error('Passwords do not match.');
      return;
    }
    setLoading(true);
    try {
      await passwordResetApi.resetPassword({ token, email, newPassword, confirmPassword });
      setSuccess(true);
      toast.success('Password updated successfully!');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Reset failed. Try again.');
    } finally {
      setLoading(false);
    }
  };

  // 1. Validating State Layout
  if (validating) return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center">
      <div className="flex flex-col items-center gap-4">
        <div className="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"/>
        <p className="text-slate-400 text-xs font-bold uppercase tracking-[0.2em]">Verifying Link...</p>
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-6 lg:p-12">
      <div className="w-full max-w-[1300px] flex flex-col lg:flex-row gap-16 lg:gap-24 items-center justify-between">

        {/* Branding Panel */}
        <div className="hidden lg:flex flex-col flex-1 max-w-lg">
          <div className="flex items-center gap-4 mb-10">
            <div className="w-14 h-14 rounded-2xl bg-blue-600 flex items-center justify-center shadow-lg shadow-blue-200">
              <Factory size={28} className="text-white" />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-slate-800 tracking-tight">ManuTrack</h1>
              <p className="text-slate-500 text-sm font-medium">Manufacturing Management System</p>
            </div>
          </div>

          <h2 className="text-5xl font-extrabold text-slate-800 leading-[1.1] mb-6">
            Secure your

            <span className="text-blue-600">credentials.</span>
          </h2>

          <p className="text-slate-500 text-lg mb-10 max-w-md leading-relaxed">
            Update your account password to regain access to your manufacturing dashboard.
          </p>

          <div className="grid grid-cols-2 gap-4">
            {FEATURES.map(f => (
              <div key={f.label} className="flex items-center gap-3 bg-white rounded-2xl px-4 py-4 border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
                <span className="text-xl">{f.icon}</span>
                <span className="text-sm font-bold text-slate-700">{f.label}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Form Card */}
        <div className="w-full max-w-[540px] shrink-0">
          <div className="bg-white rounded-[3rem] border border-slate-200 shadow-2xl p-8 md:p-12">

            {/* 2. Token Error State */}
            {tokenError ? (
              <div className="text-center py-4">
                <div className="w-20 h-20 bg-red-50 rounded-full flex items-center justify-center mx-auto mb-6">
                  <AlertCircle size={40} className="text-red-500" />
                </div>
                <h2 className="text-3xl font-bold text-slate-800 mb-2">Link Expired</h2>
                <p className="text-sm text-slate-500 mb-10 leading-relaxed px-4">{tokenError}</p>
                <Link
                  to="/forgot-password"
                  className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-4 rounded-2xl shadow-lg transition-all text-center inline-block"
                >
                  Request New Link
                </Link>
              </div>
            ) : success ? (
              /* 3. Success State */
              <div className="text-center py-4">
                <div className="w-20 h-20 bg-green-50 rounded-full flex items-center justify-center mx-auto mb-6 shadow-inner">
                  <CheckCircle2 size={40} className="text-green-500" />
                </div>
                <h2 className="text-3xl font-bold text-slate-800 mb-2">Success!</h2>
                <p className="text-sm text-slate-500 mb-10 leading-relaxed px-4">
                  Your password has been updated. You can now use your new credentials to sign in.
                </p>
                <button
                  onClick={() => navigate('/login')}
                  className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-4 rounded-2xl shadow-lg transition-all"
                >
                  Back to Login
                </button>
              </div>
            ) : (
              /* 4. Reset Form State */
              <>
                <header className="mb-10 text-center">
                  <h2 className="text-3xl font-bold text-slate-800 mb-2">Reset Password</h2>
                  <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">Secure Update</p>
                </header>

                <form onSubmit={handleSubmit} className="space-y-6" autoComplete="off">
                  {/* Readonly Email */}
                  <div className="space-y-2">
                    <label className="text-sm font-bold text-slate-700 ml-1">Account Email</label>
                    <input
                      type="email" value={email} readOnly
                      className="login-input bg-slate-50 text-slate-400 cursor-not-allowed border-dashed pl-6"
                    />
                  </div>

                  {/* New Password */}
                  <div className="space-y-2">
                    <label className="text-sm font-bold text-slate-700 ml-1">New Password</label>
                    <div className="relative group">
                      <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-blue-500 transition-colors" size={20} />
                      <input
                        type="password"
                        value={newPassword}
                        onChange={e => setNew(e.target.value)}
                        placeholder="Min. 6 characters"
                        required
                        className="login-input"
                      />
                    </div>
                  </div>

                  {/* Confirm Password */}
                  <div className="space-y-2">
                    <label className="text-sm font-bold text-slate-700 ml-1">Confirm New Password</label>
                    <div className="relative group">
                      <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-blue-500 transition-colors" size={20} />
                      <input
                        type="password"
                        value={confirmPassword}
                        onChange={e => setConfirm(e.target.value)}
                        placeholder="Re-enter password"
                        required
                        className={`login-input ${confirmPassword && newPassword !== confirmPassword ? 'border-red-500 bg-red-50' : ''}`}
                      />
                    </div>
                    {confirmPassword && newPassword !== confirmPassword && (
                      <p className="text-[11px] text-red-500 font-bold ml-1">Passwords do not match</p>
                    )}
                  </div>

                  <button
                    type="submit"
                    disabled={loading}
                    style={{
                      width: '60%',
                      margin: '32px auto 0',
                      padding: '12px',
                      background: loading
                        ? '#93c5fd'
                        : 'linear-gradient(135deg,#2563eb 0%,#1d4ed8 100%)',
                      color: '#fff',
                      border: 'none',
                      borderRadius: 12,
                      fontSize: 14,
                      fontWeight: 700,
                      cursor: loading ? 'not-allowed' : 'pointer',
                      boxShadow: loading ? 'none' : '0 4px 18px rgba(37,99,235,0.3)',
                      transition: 'all 0.22s',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      gap: 8,
                    }}
                  >
                    {loading ? "Updating..." : "Reset Password"}
                  </button>
                </form>
              </>
            )}

            <div className="mt-10 pt-8 border-t border-slate-100 text-center">
              <Link to="/login" className="flex items-center justify-center gap-2 text-sm text-blue-600 font-bold hover:underline transition-all">
                <ChevronLeft size={16} />
                Back to Sign In
              </Link>
            </div>
          </div>
          <p className="mt-8 text-center text-[10px] text-slate-400 font-bold uppercase tracking-[0.2em]">ManuTrack v1.0 • Secure Access</p>
        </div>
      </div>

      <style jsx>{`
        .login-input {
          width: 100%;
          padding: 1.125rem 1rem 1.125rem 3.5rem;
          background-color: #fcfdfe;
          border: 1px solid #e2e8f0;
          border-radius: 1.25rem;
          font-size: 0.95rem;
          color: #1e293b;
          outline: none;
          transition: all 0.2s ease;
        }
        .login-input:focus {
          border-color: #3b82f6;
          background-color: #ffffff;
          box-shadow: 0 0 0 5px rgba(59, 130, 246, 0.08);
        }
        .login-input::placeholder {
          color: #94a3b8;
        }
      `}</style>
    </div>
  );
}