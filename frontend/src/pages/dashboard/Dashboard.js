import React, { useEffect, useState } from 'react';
import { analyticsApi } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import StatCard from '../../components/common/StatCard';
import { Factory, Package, ShoppingCart, Truck, AlertTriangle, Clock, TrendingUp } from 'lucide-react';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, Legend
} from 'recharts';

const COLORS = ['#3b82f6','#10b981','#f59e0b','#ef4444','#8b5cf6'];

const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    return (
      <div className="bg-white border border-slate-200 rounded-xl shadow-lg px-4 py-3">
        <p className="text-xs font-semibold text-slate-500 mb-1">{label}</p>
        {payload.map((p, i) => (
          <p key={i} className="text-sm font-bold" style={{ color: p.color }}>{p.name}: {p.value}</p>
        ))}
      </div>
    );
  }
  return null;
};

export default function Dashboard() {
  const { user } = useAuth();
  const [data, setData]     = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    analyticsApi.getDashboard()
      .then(r => setData(r.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return (
    <div className="flex items-center justify-center h-64">
      <div className="flex flex-col items-center gap-3">
        <div className="w-8 h-8 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
        <p className="text-slate-400 text-sm">Loading dashboard...</p>
      </div>
    </div>
  );
  if (!data) return <div className="text-red-500 p-4 bg-red-50 rounded-xl border border-red-200">Failed to load dashboard data.</div>;

  const { production, inventory, procurement, logistics } = data;

  const productionChartData = [
    { name: 'Planned',     value: Math.max(0, production.totalPlans - production.inProgressPlans - production.completedPlans) },
    { name: 'In Progress', value: production.inProgressPlans },
    { name: 'Completed',   value: production.completedPlans },
  ].filter(d => d.value > 0);

  const inventoryChartData = [
    { name: 'Available',    value: inventory.availableItems,    fill: '#10b981' },
    { name: 'Low Stock',    value: inventory.lowStockItems,     fill: '#f59e0b' },
    { name: 'Out of Stock', value: inventory.outOfStockItems,   fill: '#ef4444' },
  ].filter(d => d.value > 0);

  const shipmentData = [
    { name: 'Scheduled', value: logistics.scheduledShipments },
    { name: 'In Transit', value: logistics.inTransitShipments },
    { name: 'Delivered',  value: logistics.deliveredShipments },
    { name: 'Delayed',    value: logistics.delayedShipments },
  ];

  return (
    <div>
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-800">Dashboard</h1>
        <p className="text-slate-500 mt-0.5">
          Welcome back, <span className="font-semibold text-slate-700">{user?.name}</span>
          {' · '}
          <span className="text-blue-600 font-medium">{user?.role}</span>
        </p>
      </div>

      {/* KPI Row 1 */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
        <StatCard title="Active Plans"    value={production.inProgressPlans}   subtitle={`${production.totalPlans} total plans`}     icon={Factory}       color="blue" />
        <StatCard title="Work Orders"     value={production.totalWorkOrders}   subtitle={`${production.pendingWorkOrders} pending`}   icon={Clock}         color="yellow" />
        <StatCard title="Inventory Items" value={inventory.totalItems}         subtitle={`${inventory.lowStockItems} low stock`}      icon={Package}       color="green" />
        <StatCard title="Open POs"        value={procurement.openPOs}          subtitle={`$${procurement.totalPOValue?.toLocaleString() ?? 0} total`} icon={ShoppingCart} color="purple" />
      </div>

      {/* KPI Row 2 */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <StatCard title="Active Machines"   value={production.activeMachines}           subtitle={`${production.maintenanceMachines} in maintenance`}         icon={Factory}       color="blue" />
        <StatCard title="Shipments"         value={logistics.totalShipments}            subtitle={`${logistics.delayedShipments} delayed`}                    icon={Truck}         color={logistics.delayedShipments > 0 ? 'red' : 'orange'} />
        <StatCard title="Overdue Invoices"  value={procurement.overdueInvoices}         subtitle={`${procurement.totalInvoices} total invoices`}              icon={AlertTriangle} color={procurement.overdueInvoices > 0 ? 'red' : 'green'} />
        <StatCard title="Out of Stock"      value={inventory.outOfStockItems}           subtitle="Items needing reorder"                                       icon={AlertTriangle} color={inventory.outOfStockItems > 0 ? 'red' : 'green'} />
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">

        {/* Production Plans Donut */}
        <div className="card">
          <h3 className="text-sm font-semibold text-slate-700 mb-4 flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-blue-500 inline-block"></span>
            Production Plans Status
          </h3>
          <ResponsiveContainer width="100%" height={200}>
            <PieChart>
              <Pie data={productionChartData} cx="50%" cy="50%" innerRadius={52} outerRadius={78} dataKey="value"
                   label={({ name, value }) => value > 0 ? name : ''} labelLine={false}>
                {productionChartData.map((_, i) => <Cell key={i} fill={COLORS[i]} />)}
              </Pie>
              <Tooltip content={<CustomTooltip />} />
              <Legend wrapperStyle={{ fontSize: 11, color: '#64748b' }} />
            </PieChart>
          </ResponsiveContainer>
        </div>

        {/* Inventory Bar */}
        <div className="card">
          <h3 className="text-sm font-semibold text-slate-700 mb-4 flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-green-500 inline-block"></span>
            Inventory Health
          </h3>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={inventoryChartData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" />
              <XAxis dataKey="name" tick={{ fill: '#94a3b8', fontSize: 11 }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fill: '#94a3b8', fontSize: 11 }} axisLine={false} tickLine={false} />
              <Tooltip content={<CustomTooltip />} />
              <Bar dataKey="value" radius={[6,6,0,0]}>
                {inventoryChartData.map((entry, i) => <Cell key={i} fill={entry.fill} />)}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Shipments Horizontal Bar */}
        <div className="card">
          <h3 className="text-sm font-semibold text-slate-700 mb-4 flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-orange-500 inline-block"></span>
            Shipment Overview
          </h3>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={shipmentData} layout="vertical">
              <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" />
              <XAxis type="number" tick={{ fill: '#94a3b8', fontSize: 11 }} axisLine={false} tickLine={false} />
              <YAxis dataKey="name" type="category" tick={{ fill: '#64748b', fontSize: 11 }} width={72} axisLine={false} tickLine={false} />
              <Tooltip content={<CustomTooltip />} />
              <Bar dataKey="value" fill="#3b82f6" radius={[0,6,6,0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
}
