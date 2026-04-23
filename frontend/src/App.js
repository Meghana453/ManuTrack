import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider, useAuth } from './context/AuthContext';
import Layout from './components/layout/Layout';
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';
import ForgotPasswordPage from './pages/auth/ForgotPasswordPage';
import ResetPasswordPage from './pages/auth/ResetPasswordPage';
import Dashboard from './pages/dashboard/Dashboard';
import ProductionPage from './pages/dashboard/ProductionPage';
import InventoryPage from './pages/dashboard/InventoryPage';
import ProcurementPage from './pages/dashboard/ProcurementPage';
import LogisticsPage from './pages/dashboard/LogisticsPage';
import UsersPage from './pages/dashboard/UsersPage';
import NotificationsPage from './pages/dashboard/NotificationsPage';
import AnalyticsPage from './pages/dashboard/AnalyticsPage';

const PrivateRoute = ({ children }) => {
  const { user, loading } = useAuth();
  if (loading) return (
    <div className="flex items-center justify-center h-screen bg-gray-50">
      <div className="flex flex-col items-center gap-3">
        <div className="w-8 h-8 border-2 border-blue-500 border-t-transparent rounded-full animate-spin"/>
        <p className="text-gray-400 text-sm">Loading...</p>
      </div>
    </div>
  );
  return user ? children : <Navigate to="/login" />;
};

const PublicRoute = ({ children }) => {
  const { user, loading } = useAuth();
  if (loading) return null;
  return user ? <Navigate to="/dashboard" /> : children;
};

const AppRoutes = () => (
  <Routes>
    {/* Public auth routes */}
    <Route path="/login"           element={<PublicRoute><LoginPage /></PublicRoute>} />
    <Route path="/register"        element={<PublicRoute><RegisterPage /></PublicRoute>} />
    <Route path="/forgot-password" element={<PublicRoute><ForgotPasswordPage /></PublicRoute>} />
    <Route path="/reset-password"  element={<ResetPasswordPage />} />

    {/* Protected app routes */}
    <Route path="/" element={<PrivateRoute><Layout /></PrivateRoute>}>
      <Route index element={<Navigate to="/dashboard" />} />
      <Route path="dashboard"     element={<Dashboard />} />
      <Route path="production"    element={<ProductionPage />} />
      <Route path="inventory"     element={<InventoryPage />} />
      <Route path="procurement"   element={<ProcurementPage />} />
      <Route path="logistics"     element={<LogisticsPage />} />
      <Route path="users"         element={<UsersPage />} />
      <Route path="notifications" element={<NotificationsPage />} />
      <Route path="analytics"     element={<AnalyticsPage />} />
    </Route>

    <Route path="*" element={<Navigate to="/dashboard" />} />
  </Routes>
);

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <AppRoutes />
        <Toaster
          position="top-right"
          toastOptions={{
            style: {
              background: '#ffffff',
              color: '#1e293b',
              border: '1px solid #e2e8f0',
              boxShadow: '0 4px 20px rgba(0,0,0,0.08)',
              borderRadius: '10px',
              fontSize: '14px',
            }
          }}
        />
      </BrowserRouter>
    </AuthProvider>
  );
}
