import React, { useEffect, useState } from 'react';
import { analyticsApi } from '../../services/api';
import StatCard from '../../components/common/StatCard';
import { BarChart3, RefreshCw, Download } from 'lucide-react';
import toast from 'react-hot-toast';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line, PieChart, Pie, Cell, Legend, RadialBarChart, RadialBar } from 'recharts';

const COLORS = ['#3b82f6','#10b981','#f59e0b','#ef4444','#8b5cf6','#06b6d4'];

export default function AnalyticsPage() {
  const [data, setData] = useState(null);
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);

  const load = async() => {
    setLoading(true);
    try {
      const [d, r] = await Promise.all([analyticsApi.getDashboard(), analyticsApi.getAllReports()]);
      setData(d.data); setReports(r.data);
    } catch { toast.error('Failed to load analytics'); }
    finally { setLoading(false); }
  };
  useEffect(()=>{load();},[]);

  const generateReport = async() => {
    setGenerating(true);
    try {
      await analyticsApi.generateReport('FULL');
      toast.success('Report generated'); load();
    } catch { toast.error('Failed'); }
    finally { setGenerating(false); }
  };

  if(loading) return <div className="text-slate-400">Loading analytics...</div>;
  if(!data) return <div className="text-red-400">Failed to load analytics.</div>;

  const { production, inventory, procurement, logistics } = data;

  const machineData = [
    {name:'Active',value:production.activeMachines,fill:'#10b981'},
    {name:'Maintenance',value:production.maintenanceMachines,fill:'#f59e0b'},
    {name:'Idle',value:production.totalMachines-production.activeMachines-production.maintenanceMachines,fill:'#6b7280'},
  ];

  const woData = [
    {name:'Pending',value:production.pendingWorkOrders},
    {name:'In Progress',value:production.totalWorkOrders-production.pendingWorkOrders-production.haltedWorkOrders},
    {name:'Halted',value:production.haltedWorkOrders},
  ];

  const shipData = [
    {name:'Scheduled',value:logistics.scheduledShipments,fill:'#3b82f6'},
    {name:'In Transit',value:logistics.inTransitShipments,fill:'#f59e0b'},
    {name:'Delivered',value:logistics.deliveredShipments,fill:'#10b981'},
    {name:'Delayed',value:logistics.delayedShipments,fill:'#ef4444'},
  ];

  const invData = [
    {name:'Available',value:inventory.availableItems,fill:'#10b981'},
    {name:'Low Stock',value:inventory.lowStockItems,fill:'#f59e0b'},
    {name:'Out of Stock',value:inventory.outOfStockItems,fill:'#ef4444'},
  ];

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div><h1 className="text-2xl font-bold text-slate-800">Analytics & Reporting</h1><p className="text-slate-400 text-sm mt-1">Operational KPIs and insights</p></div>
        <div className="flex gap-2">
          <button onClick={load} className="btn-secondary flex items-center gap-2"><RefreshCw size={14}/>Refresh</button>
          <button onClick={generateReport} disabled={generating} className="btn-primary flex items-center gap-2"><Download size={14}/>{generating?'Generating...':'Generate Report'}</button>
        </div>
      </div>

      {/* Production KPIs */}
      <h2 className="text-sm font-semibold text-slate-400 uppercase tracking-wider mb-3">Production</h2>
      <div className="grid grid-cols-4 gap-4 mb-6">
        <StatCard title="Total Plans" value={production.totalPlans} color="blue" icon={BarChart3}/>
        <StatCard title="In Progress" value={production.inProgressPlans} color="yellow" icon={BarChart3}/>
        <StatCard title="Completed" value={production.completedPlans} color="green" icon={BarChart3}/>
        <StatCard title="Work Orders" value={production.totalWorkOrders} color="purple" icon={BarChart3}/>
      </div>

      <div className="grid grid-cols-2 gap-4 mb-6">
        <div className="card">
          <h3 className="text-sm font-semibold text-slate-600 mb-4">Machine Status Distribution</h3>
          <ResponsiveContainer width="100%" height={220}>
            <PieChart><Pie data={machineData} cx="50%" cy="50%" outerRadius={80} dataKey="value" label={({name,value})=>value>0?`${name}: ${value}`:''}>
              {machineData.map((e,i)=><Cell key={i} fill={e.fill}/>)}
            </Pie><Tooltip contentStyle={{background:'#ffffff',border:'1px solid #334155',borderRadius:8}}/><Legend/></PieChart>
          </ResponsiveContainer>
        </div>
        <div className="card">
          <h3 className="text-sm font-semibold text-slate-600 mb-4">Work Order Status</h3>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={woData}><CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9"/>
              <XAxis dataKey="name" tick={{fill:'#94a3b8',fontSize:11}}/><YAxis tick={{fill:'#94a3b8',fontSize:11}}/>
              <Tooltip contentStyle={{background:'#ffffff',border:'1px solid #334155',borderRadius:8}}/>
              <Bar dataKey="value" fill="#3b82f6" radius={[4,4,0,0]}/>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Inventory & Logistics KPIs */}
      <h2 className="text-sm font-semibold text-slate-400 uppercase tracking-wider mb-3 mt-2">Inventory & Logistics</h2>
      <div className="grid grid-cols-2 gap-4 mb-6">
        <div className="card">
          <h3 className="text-sm font-semibold text-slate-600 mb-4">Inventory Health</h3>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={invData} layout="vertical"><CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9"/>
              <XAxis type="number" tick={{fill:'#94a3b8',fontSize:11}}/><YAxis dataKey="name" type="category" tick={{fill:'#94a3b8',fontSize:11}} width={80}/>
              <Tooltip contentStyle={{background:'#ffffff',border:'1px solid #334155',borderRadius:8}}/>
              <Bar dataKey="value" radius={[0,4,4,0]}>{invData.map((e,i)=><Cell key={i} fill={e.fill}/>)}</Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
        <div className="card">
          <h3 className="text-sm font-semibold text-slate-600 mb-4">Shipment Status</h3>
          <ResponsiveContainer width="100%" height={220}>
            <PieChart><Pie data={shipData} cx="50%" cy="50%" outerRadius={80} dataKey="value" label={({name,value})=>value>0?`${name}: ${value}`:''}>
              {shipData.map((e,i)=><Cell key={i} fill={e.fill}/>)}
            </Pie><Tooltip contentStyle={{background:'#ffffff',border:'1px solid #334155',borderRadius:8}}/><Legend/></PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Procurement KPIs */}
      <h2 className="text-sm font-semibold text-slate-400 uppercase tracking-wider mb-3">Procurement</h2>
      <div className="grid grid-cols-4 gap-4 mb-6">
        <StatCard title="Active Vendors" value={procurement.activeVendors} subtitle={`of ${procurement.totalVendors} total`} color="blue" icon={BarChart3}/>
        <StatCard title="Open POs" value={procurement.openPOs} color="yellow" icon={BarChart3}/>
        <StatCard title="Total PO Value" value={`$${(procurement.totalPOValue||0).toLocaleString()}`} color="green" icon={BarChart3}/>
        <StatCard title="Overdue Invoices" value={procurement.overdueInvoices} color={procurement.overdueInvoices>0?'red':'green'} icon={BarChart3}/>
      </div>

      {/* Reports history */}
      {reports.length > 0 && (
        <div className="card">
          <h3 className="text-sm font-semibold text-slate-600 mb-4">Generated Reports ({reports.length})</h3>
          <div className="space-y-2">
            {reports.slice().reverse().map(r=>(
              <div key={r.reportId} className="flex items-center justify-between py-2 border-b border-slate-200 last:border-0">
                <div>
                  <span className="text-slate-700 text-sm font-medium">Report #{r.reportId} — {r.scope}</span>
                  <span className="text-slate-400 text-xs ml-3">{r.generatedDate?.replace('T',' ').slice(0,19)}</span>
                </div>
                <span className="badge bg-green-500/20 text-green-300">Saved</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}