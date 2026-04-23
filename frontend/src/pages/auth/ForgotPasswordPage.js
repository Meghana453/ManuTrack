import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { passwordResetApi } from '../../services/api';
import { Mail, CheckCircle2, Factory, ChevronLeft } from 'lucide-react';
import toast from 'react-hot-toast';

const FEATURES = [
  { icon: '📦', label: 'Inventory Control' },
  { icon: '⚙️', label: 'Production Planning' },
  { icon: '🛒', label: 'Procurement' },
  { icon: '🚚', label: 'Logistics Tracking' },
  { icon: '📊', label: 'Live Analytics' },
  { icon: '🔔', label: 'Smart Alerts' },
];

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [sent, setSent] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await passwordResetApi.forgotPassword(email);
      setSent(true);
      toast.success('Reset link sent to your email!');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Something went wrong. Try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-6 lg:p-12">
      <div className="w-full max-w-[1300px] flex flex-col lg:flex-row gap-16 lg:gap-24 items-center justify-between">

        {/* Branding Panel (Consistent across all auth pages) */}
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
            Recover your

            <span className="text-blue-600">account access.</span>
          </h2>

          <p className="text-slate-500 text-lg mb-10 max-w-md leading-relaxed">
            Lost your credentials? No problem. Enter your email and we'll help you get back into the command centre.
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

            {!sent ? (
              <>
                <header className="mb-10 text-center">
                  <h2 className="text-3xl font-bold text-slate-800 mb-2">Forgot Password</h2>
                  <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">Account Recovery</p>
                </header>

                <p className="text-sm text-slate-500 text-center mb-8 leading-relaxed">
                  Enter your registered email address and we'll send you a link to reset your password.
                </p>

                <form onSubmit={handleSubmit} className="space-y-6" autoComplete="off">
                  <div className="space-y-2">
                    <label className="text-sm font-bold text-slate-700 ml-1">Email Address</label>
                    <div className="relative group">
                      <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-blue-500 transition-colors" size={20} />
                      <input
                        type="email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        placeholder="Enter your email"
                        required
                        className="login-input"
                      />
                    </div>
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
                    {loading ? "Sending link…" : "Send Reset Link"}
                  </button>
                </form>
              </>
            ) : (
              /* Success State */
              <div className="text-center py-4">
                <div className="w-20 h-20 bg-green-50 rounded-full flex items-center justify-center mx-auto mb-6 shadow-inner">
                  <CheckCircle2 size={40} className="text-green-500" />
                </div>
                <h2 className="text-3xl font-bold text-slate-800 mb-2">Check Email</h2>
                <p className="text-sm text-slate-500 mb-10 leading-relaxed px-4">
                  We have sent a password reset link to

                  <strong className="text-slate-800 font-bold">{email}</strong>.
                </p>

                <button
                  onClick={() => { setSent(false); setEmail(''); }}
                  className="flex items-center justify-center gap-2 mx-auto text-sm text-blue-600 hover:text-blue-800 font-bold transition-all"
                >
                  <ChevronLeft size={16} />
                  Try a different email
                </button>
              </div>
            )}

            <div className="mt-10 pt-8 border-t border-slate-100 text-center">
              <p className="text-sm text-slate-500 font-medium">
                Remembered your password?{' '}
                <Link to="/login" className="text-blue-600 font-bold hover:underline transition-all">Sign in</Link>
              </p>
            </div>
          </div>
          <p className="mt-8 text-center text-[10px] text-slate-400 font-bold uppercase tracking-[0.2em]">ManuTrack v1.0 • Secure Recovery</p>
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