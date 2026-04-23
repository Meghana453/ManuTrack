import React, { useState } from 'react';

import { useNavigate, Link } from 'react-router-dom';

import { useAuth } from '../../context/AuthContext';

import { Lock, Mail, AlertCircle, Factory } from 'lucide-react';

import toast from 'react-hot-toast';
 
const FEATURES = [

  { icon: '📦', label: 'Inventory Control' },

  { icon: '⚙️', label: 'Production Planning' },

  { icon: '🛒', label: 'Procurement' },

  { icon: '🚚', label: 'Logistics Tracking' },

  { icon: '📊', label: 'Live Analytics' },

  { icon: '🔔', label: 'Smart Alerts' },

];
 
export default function LoginPage() {

  const [form, setForm] = useState({ email: '', password: '' });

  const [errors, setErrors] = useState({});

  const [remember, setRemember] = useState(false);

  const [loading, setLoading] = useState(false);
 
  const { login } = useAuth();

  const navigate = useNavigate();
 
  const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
 
  const set = (k, v) => {

    setForm(f => ({ ...f, [k]: v }));

    if (errors[k] || errors.auth) {

      setErrors(e => ({ ...e, [k]: '', auth: '' }));

    }

  };
 
  const validateForm = () => {

    const newErrors = {};

    if (!form.email.trim()) {

      newErrors.email = "Email is required";

    } else if (!emailRegex.test(form.email)) {

      newErrors.email = "Invalid email format";

    }

    if (!form.password) {

      newErrors.password = "Password is required";

    } else if (form.password.length < 6) {

      newErrors.password = "Invalid password";

    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;

  };
 
  const handleSubmit = async (e) => {

    e.preventDefault();

    if (!validateForm()) return;
 
    setLoading(true);

    try {

      await login(form.email, form.password);

      toast.success('Welcome back!');

      navigate('/dashboard');

    } catch (err) {

      setLoading(false);

      setForm({ email: form.email, password: '' });

      const status = err.response?.status;

      if (status === 401) {

        setErrors({

          email: 'Invalid credentials',

          password: 'Invalid password',

          auth: 'The email or password you entered is incorrect.'

        });

        setTimeout(() => {
          setErrors({});
        }, 30000000000000);


      } else {

        toast.error('System error. Please try again later.');

      }

    }

  };
 
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

            Secure access to your<br />
<span className="text-blue-600">workspace.</span>
</h2>
 
          <p className="text-slate-500 text-lg mb-10 max-w-md leading-relaxed">

            Enter your credentials to access the ManuTrack command centre.
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
<header className="mb-10 text-center">
<h2 className="text-3xl font-bold text-slate-800 mb-2">Sign In</h2>
<p className="text-xs font-bold text-slate-400 uppercase tracking-widest">Enterprise Login</p>
</header>
 
            {errors.auth && (
<div className="mb-6 p-3 bg-red-50 border border-red-100 rounded-lg flex items-center gap-2 text-red-600 text-[11px] font-bold">
<AlertCircle size={14} />

                {errors.auth}
</div>

            )}
 
            <form onSubmit={handleSubmit} className="space-y-6" autoComplete="chrome-off">

              {/* Email */}
<div className="space-y-2">
<label className="text-sm font-bold text-slate-700 ml-1">Email Address</label>
<div className="relative group">
<Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-blue-500 transition-colors" size={20} />
<input

                    type="email"

                    name="manutrack-user-email"

                    autoComplete="off"

                    value={form.email}

                    onChange={e => set('email', e.target.value)}

                    className={`login-input ${errors.email ? 'border-red-500 bg-red-50' : ''}`}

                    placeholder="Enter your email"

                  />
</div>

                {errors.email && <p className="text-[11px] text-red-500 font-bold ml-1">{errors.email}</p>}
</div>
 
              {/* Password - Eye Icon Removed */}
<div className="space-y-2">
<label className="text-sm font-bold text-slate-700 ml-1">Password</label>
<div className="relative group">
<Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-blue-500 transition-colors" size={20} />
<input

                    type="password"

                    name="manutrack-user-password"

                    autoComplete="new-password"

                    value={form.password}

                    onChange={e => set('password', e.target.value)}

                    className={`login-input ${errors.password ? 'border-red-500 bg-red-50' : ''}`}

                    placeholder="Enter your password"

                  />
</div>

                {errors.password && <p className="text-[11px] text-red-500 font-bold ml-1">{errors.password}</p>}
</div>
 
              <div className="flex items-center justify-between px-1">
<label className="flex items-center gap-3 cursor-pointer group">
<input

                    type="checkbox" checked={remember} onChange={e => setRemember(e.target.checked)}

                    className="w-5 h-5 rounded border-slate-300 text-blue-600 focus:ring-0 cursor-pointer"

                  />
<span className="text-sm text-slate-500 font-medium group-hover:text-slate-700 transition-colors">

                    Remember me
</span>
</label>
<Link to="/forgot-password" size="sm" className="text-sm text-blue-600 hover:text-blue-800 font-bold hover:underline">

                  Forgot password?
</Link>
</div>
 
              <button

                type="submit"

                disabled={loading}

                style={{

                  width: '50%',

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

                {loading ? "Authenticating..." : "Sign In"}
</button>
</form>
 
            <div className="mt-10 pt-8 border-t border-slate-100 text-center">
<p className="text-sm text-slate-500 font-medium">

                Don't have an account? <Link to="/register" className="text-blue-600 font-bold hover:underline">Register now</Link>
</p>
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
 