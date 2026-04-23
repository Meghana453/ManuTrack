import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authApi } from '../../services/api';
import {
  ChevronDown, Mail, Lock, User, Phone,
  CheckCircle2, Factory
} from 'lucide-react';
import toast from 'react-hot-toast';

const ROLES = [
  { value: 'PLANNER',     label: 'Production Planner',    desc: 'Schedules & allocates resources' },
  { value: 'SUPERVISOR',  label: 'Shop-Floor Supervisor', desc: 'Oversees work orders & machines' },
  { value: 'INVENTORY',   label: 'Inventory Manager',     desc: 'Monitors stock & material requests' },
  { value: 'PROCUREMENT', label: 'Procurement Officer',   desc: 'Manages purchase orders & vendors' },
  { value: 'LOGISTICS',   label: 'Logistics Coordinator', desc: 'Plans shipments & tracks delivery' },
  { value: 'ADMIN',       label: 'Administrator',         desc: 'Full system configuration access' },
];

const FEATURES = [
  { icon: '📦', label: 'Inventory Control' },
  { icon: '⚙️', label: 'Production Planning' },
  { icon: '🛒', label: 'Procurement' },
  { icon: '🚚', label: 'Logistics Tracking' },
  { icon: '📊', label: 'Live Analytics' },
  { icon: '🔔', label: 'Smart Alerts' },
];

export default function RegisterPage() {
  const [form, setForm] = useState({ name:'', role:'', email:'', phone:'', password:'', confirm:'' });
  const [errors, setErrors] = useState({});
  const [agreed, setAgreed] = useState(false);
  const [loading, setLoading] = useState(false);
  const [roleOpen, setRoleOpen] = useState(false);
  const navigate = useNavigate();
  const roleRef = useRef(null);

  const strongPasswordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/;
  const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

  useEffect(() => {
    const handleClick = (e) => { if (roleRef.current && !roleRef.current.contains(e.target)) setRoleOpen(false); };
    document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, []);

  const set = (k, v) => {
    setForm(f => ({ ...f, [k]: v }));
    if (errors[k]) setErrors(e => ({ ...e, [k]: '' }));
  };

  const selectedRole = ROLES.find(r => r.value === form.role);

  const handleSubmit = async (e) => {
    if (e) e.preventDefault();
    const newErrors = {};

    if (!form.name.trim()) newErrors.name = "Name is required";
    if (!form.role) newErrors.role = "Select a role";
    if (!emailRegex.test(form.email)) newErrors.email = "Enter valid email";
    if (!/^[0-9]{10}$/.test(form.phone)) newErrors.phone = "10 digits required";
    if (!strongPasswordRegex.test(form.password)) newErrors.password = "Need Uppercase, Number & Symbol";
    if (form.password !== form.confirm) newErrors.confirm = "Passwords do not match";
    if (!agreed) newErrors.agreed = "You must accept the Terms";

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setLoading(true);
    try {
      await authApi.register({
        name: form.name,
        role: form.role,
        email: form.email,
        phone: form.phone,
        password: form.password
      });
      toast.success("Account created! Redirecting...");
      setTimeout(() => navigate("/login"), 2500);
    } catch (err) {
      setLoading(false);
      const data = err.response?.data;
      if (data?.message) toast.error(data.message);
      if (data?.fieldErrors) setErrors(data.fieldErrors);
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
            Your manufacturing

            <span className="text-blue-600">command centre.</span>
          </h2>

          <p className="text-slate-500 text-lg mb-10 max-w-md leading-relaxed">
            Manage production, inventory, procurement, and logistics in one unified platform built for modern manufacturing.
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
        <div className="w-full max-w-[640px] shrink-0">
          <div className="bg-white rounded-[3rem] border border-slate-200 shadow-2xl p-8 md:p-12">
            <header className="mb-10 text-center">
              <h2 className="text-3xl font-bold text-slate-800 mb-2">Create Account</h2>
              <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">Professional registration for ManuTrack v1.0</p>
            </header>

            <form onSubmit={handleSubmit} className="space-y-6" autoComplete="off">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* Full Name */}
                <div className="space-y-2">
                  <label className="text-sm font-bold text-slate-700 ml-1">Full Name</label>
                  <div className="relative group">
                    <User className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-blue-500 transition-colors" size={20} />
                    <input
                      type="text"
                      value={form.name}
                      onChange={e => set('name', e.target.value)}
                      className={`login-input ${errors.name ? 'border-red-500 bg-red-50' : ''}`}
                      placeholder="Enter your name"
                    />
                  </div>
                  {errors.name && <p className="text-[11px] text-red-500 font-bold ml-1">{errors.name}</p>}
                </div>

                {/* Role */}
                <div className="space-y-2" ref={roleRef}>
                  <label className="text-sm font-bold text-slate-700 ml-1">Work Role</label>
                  <div className="relative group">
                    <button
                      type="button" onClick={() => setRoleOpen(!roleOpen)}
                      className={`login-input text-left flex justify-between items-center ${errors.role ? 'border-red-500 bg-red-50' : ''}`}
                    >
                      <span className={form.role ? 'text-slate-800' : 'text-slate-400 truncate mr-2'}>
                        {selectedRole ? selectedRole.label : '-- Select Role --'}
                      </span>
                      <ChevronDown size={18} className={`shrink-0 text-slate-400 transition-transform ${roleOpen ? 'rotate-180' : ''}`} />
                    </button>
                    {roleOpen && (
                      <div className="absolute w-full mt-2 bg-white border border-slate-200 rounded-2xl shadow-2xl z-50 py-2 max-h-48 overflow-y-auto">
                        {ROLES.map(r => (
                          <button
                            key={r.value} type="button"
                            onClick={() => { set('role', r.value); setRoleOpen(false); }}
                            className="w-full text-left px-4 py-3 hover:bg-blue-50 transition-colors"
                          >
                            <div className="text-sm font-bold text-slate-800">{r.label}</div>
                            <div className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">{r.desc}</div>
                          </button>
                        ))}
                      </div>
                    )}
                  </div>
                  {errors.role && <p className="text-[11px] text-red-500 font-bold ml-1">{errors.role}</p>}
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-2">
                  <label className="text-sm font-bold text-slate-700 ml-1">Email Address</label>
                  <div className="relative group">
                    <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-blue-500 transition-colors" size={20} />
                    <input
                      type="email"
                      value={form.email}
                      onChange={e => set('email', e.target.value)}
                      className={`login-input ${errors.email ? 'border-red-500 bg-red-50' : ''}`}
                      placeholder="Enter your email"
                    />
                  </div>
                  {errors.email && <p className="text-[11px] text-red-500 font-bold ml-1">{errors.email}</p>}
                </div>

                <div className="space-y-2">
                  <label className="text-sm font-bold text-slate-700 ml-1">Phone Number</label>
                  <div className="relative group">
                    <Phone className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-blue-500 transition-colors" size={20} />
                    <input
                      type="tel"
                      value={form.phone}
                      onChange={e => set('phone', e.target.value)}
                      className={`login-input ${errors.phone ? 'border-red-500 bg-red-50' : ''}`}
                      placeholder="Enter phone number"
                    />
                  </div>
                  {errors.phone && <p className="text-[11px] text-red-500 font-bold ml-1">{errors.phone}</p>}
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-2">
                  <label className="text-sm font-bold text-slate-700 ml-1">Password</label>
                  <div className="relative group">
                    <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-blue-500 transition-colors" size={20} />
                    <input
                      type="password"
                      value={form.password}
                      onChange={e => set('password', e.target.value)}
                      className={`login-input ${errors.password ? 'border-red-500 bg-red-50' : ''}`}
                      placeholder="Enter password"
                    />
                  </div>
                  {errors.password && <p className="text-[11px] text-red-500 font-bold ml-1">{errors.password}</p>}
                </div>

                <div className="space-y-2">
                  <label className="text-sm font-bold text-slate-700 ml-1">Confirm Password</label>
                  <div className="relative group">
                    <CheckCircle2 className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-blue-500 transition-colors" size={20} />
                    <input
                      type="password"
                      value={form.confirm}
                      onChange={e => set('confirm', e.target.value)}
                      className={`login-input ${errors.confirm ? 'border-red-500 bg-red-50' : ''}`}
                      placeholder="Confirm password"
                    />
                  </div>
                  {errors.confirm && <p className="text-[11px] text-red-500 font-bold ml-1">{errors.confirm}</p>}
                </div>
              </div>

              <div className="pt-2 px-1">
                <label className="flex items-center gap-3 cursor-pointer group">
                  <input
                    type="checkbox" checked={agreed} onChange={e => setAgreed(e.target.checked)}
                    className="w-5 h-5 rounded border-slate-300 text-blue-600 focus:ring-0 cursor-pointer"
                  />
                  <span className="text-sm text-slate-500 font-medium group-hover:text-slate-700 transition-colors">
                    I agree to the <span className="text-blue-600 font-bold">Terms</span> and <span className="text-blue-600 font-bold">Privacy Policy</span>
                  </span>
                </label>
              </div>

             {/* REDUCED SIZE Continue Button */}
             <button
               type="button"
               onClick={handleSubmit}
               disabled={loading}
               style={{
                 width: '45%',
                 // CHANGE THIS: Increased top margin from 12px to 32px
                 margin: '32px auto 0',
                 padding: '10px',
                 background: loading
                   ? '#93c5fd'
                   : 'linear-gradient(135deg,#2563eb 0%,#1d4ed8 100%)',
                 color: '#fff',
                 border: 'none',
                 borderRadius: 12,
                 fontSize: 14,
                 fontWeight: 700,
                 letterSpacing: '0.01em',
                 fontFamily: 'inherit',
                 cursor: loading ? 'not-allowed' : 'pointer',
                 boxShadow: loading ? 'none' : '0 4px 18px rgba(37,99,235,0.3)',
                 transition: 'all 0.22s',
                 display: 'flex',
                 alignItems: 'center',
                 justifyContent: 'center',
                 gap: 8,
               }}
             >
               {loading ? "Creating account…" : "Continue"}
             </button>
            </form>

            <div className="mt-10 pt-8 border-t border-slate-100 text-center">
              <p className="text-sm text-slate-500 font-medium">
                Already have an account? <Link to="/login" className="text-blue-600 font-bold hover:underline transition-all">Sign in</Link>
              </p>
            </div>
          </div>
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