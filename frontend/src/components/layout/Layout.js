import React, { useState } from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
  LayoutDashboard, Factory, Package, ShoppingCart, Truck,
  Users, Bell, BarChart3, LogOut, Menu, X, ChevronRight
} from 'lucide-react';

const navItems = [
  { to: '/dashboard',      icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/production',     icon: Factory,         label: 'Production' },
  { to: '/inventory',      icon: Package,         label: 'Inventory' },
  { to: '/procurement',    icon: ShoppingCart,    label: 'Procurement' },
  { to: '/logistics',      icon: Truck,           label: 'Logistics' },
  { to: '/analytics',      icon: BarChart3,       label: 'Analytics' },
  { to: '/notifications',  icon: Bell,            label: 'Notifications' },
  { to: '/users',          icon: Users,           label: 'Users' },
];

const roleColors = {
  ADMIN:       'bg-purple-100 text-purple-700',
  PLANNER:     'bg-blue-100 text-blue-700',
  SUPERVISOR:  'bg-cyan-100 text-cyan-700',
  INVENTORY:   'bg-green-100 text-green-700',
  PROCUREMENT: 'bg-amber-100 text-amber-700',
  LOGISTICS:   'bg-orange-100 text-orange-700',
};

export default function Layout() {
  const { user, logout } = useAuth();
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();

  const handleLogout = () => { logout(); navigate('/login'); };

  return (
    <div className="flex h-screen bg-slate-100 overflow-hidden">

      {/* ── Sidebar ── */}
      <aside className={`${collapsed ? 'w-16' : 'w-60'} bg-white border-r border-slate-200 flex flex-col transition-all duration-300 flex-shrink-0 shadow-sm`}>

        {/* Logo */}
        <div className="flex items-center justify-between px-4 h-16 border-b border-slate-100">
          {!collapsed && (
            <div className="flex items-center gap-2.5">
              <div className="w-8 h-8 rounded-lg bg-blue-600 flex items-center justify-center shadow-sm">
                <Factory size={15} className="text-white" />
              </div>
              <div>
                <span className="font-bold text-slate-800 tracking-tight text-sm">ManuTrack</span>
                <div className="text-xs text-slate-400 font-normal leading-tight">Manufacturing OS</div>
              </div>
            </div>
          )}
          <button
            onClick={() => setCollapsed(!collapsed)}
            className="text-slate-400 hover:text-slate-600 hover:bg-slate-100 rounded-lg p-1.5 transition-colors ml-auto"
          >
            {collapsed ? <Menu size={17} /> : <X size={17} />}
          </button>
        </div>

        {/* Nav */}
        <nav className="flex-1 py-3 overflow-y-auto px-2">
          {!collapsed && (
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider px-3 mb-2">Navigation</p>
          )}
          {navItems.map(({ to, icon: Icon, label }) => (
            <NavLink
              key={to} to={to}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg mb-0.5 transition-all text-sm font-medium group
                ${isActive
                  ? 'bg-blue-50 text-blue-700 shadow-sm border border-blue-100'
                  : 'text-slate-500 hover:text-slate-800 hover:bg-slate-50'}`
              }
            >
              <Icon size={17} className="flex-shrink-0" />
              {!collapsed && (
                <>
                  <span>{label}</span>
                  <ChevronRight size={13} className="ml-auto opacity-0 group-hover:opacity-100 transition-opacity text-slate-400" />
                </>
              )}
            </NavLink>
          ))}
        </nav>

        {/* User card */}
        <div className="border-t border-slate-100 p-3">
          {!collapsed ? (
            <div className="flex items-center gap-3 bg-slate-50 rounded-xl px-3 py-2.5 border border-slate-100">
              <div className="w-8 h-8 rounded-lg bg-blue-600 flex items-center justify-center text-white text-xs font-bold flex-shrink-0 shadow-sm">
                {user?.name?.charAt(0) || 'U'}
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-semibold text-slate-700 truncate">{user?.name}</p>
                <span className={`text-xs font-medium px-1.5 py-0.5 rounded-md ${roleColors[user?.role] || 'bg-slate-100 text-slate-500'}`}>
                  {user?.role}
                </span>
              </div>
              <button onClick={handleLogout} className="text-slate-400 hover:text-red-500 transition-colors p-1 rounded-lg hover:bg-red-50" title="Logout">
                <LogOut size={15} />
              </button>
            </div>
          ) : (
            <button onClick={handleLogout} className="w-full flex justify-center text-slate-400 hover:text-red-500 transition-colors py-2 rounded-lg hover:bg-red-50">
              <LogOut size={17} />
            </button>
          )}
        </div>
      </aside>

      {/* ── Main content ── */}
      <main className="flex-1 overflow-auto">
        {/* Top bar */}
        <div className="h-14 bg-white border-b border-slate-200 flex items-center px-6 gap-4 sticky top-0 z-30 shadow-sm">
          <div className="flex-1" />
          <div className="flex items-center gap-2">
            <div className="flex items-center gap-1.5 bg-green-50 border border-green-200 rounded-full px-3 py-1">
              <div className="w-1.5 h-1.5 rounded-full bg-green-500 animate-pulse" />
              <span className="text-xs font-medium text-green-700">System Live</span>
            </div>
            <div className="w-8 h-8 rounded-lg bg-blue-600 flex items-center justify-center text-white text-xs font-bold shadow-sm">
              {user?.name?.charAt(0) || 'U'}
            </div>
          </div>
        </div>

        {/* Page content */}
        <div className="p-6 min-h-full">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
