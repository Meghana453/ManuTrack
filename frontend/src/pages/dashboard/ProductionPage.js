import React, { useEffect, useState } from 'react';
import { productionApi } from '../../services/api';
import Table from '../../components/common/Table';
import Badge from '../../components/common/Badge';
import Modal from '../../components/common/Modal';
import StatCard from '../../components/common/StatCard';
import { Factory, Plus, Pencil, Trash2, RefreshCw } from 'lucide-react';
import toast from 'react-hot-toast';

const PLAN_STATUSES = ['PLANNED','IN_PROGRESS','COMPLETED','CANCELLED'];
const MACHINE_STATUSES = ['ACTIVE','MAINTENANCE','IDLE'];
const WO_STATUSES = ['PENDING','IN_PROGRESS','COMPLETED','HALTED'];

export default function ProductionPage() {
  const [tab, setTab] = useState('plans');
  const [plans, setPlans] = useState([]);
  const [machines, setMachines] = useState([]);
  const [workOrders, setWorkOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modal, setModal] = useState({ open: false, type: '', data: null });

  const loadAll = async () => {
    setLoading(true);
    try {
      const [p, m, w] = await Promise.all([
        productionApi.plans.getAll(),
        productionApi.machines.getAll(),
        productionApi.workOrders.getAll(),
      ]);
      setPlans(p.data); setMachines(m.data); setWorkOrders(w.data);
    } catch { toast.error('Failed to load production data'); }
    finally { setLoading(false); }
  };

  useEffect(() => { loadAll(); }, []);

  const openModal = (type, data = null) => setModal({ open: true, type, data });
  const closeModal = () => { setModal({ open: false, type: '', data: null }); loadAll(); };

  const deletePlan = async (id) => {
    if (!window.confirm('Delete this plan?')) return;
    try { await productionApi.plans.delete(id); toast.success('Plan deleted'); loadAll(); }
    catch { toast.error('Delete failed'); }
  };
  const deleteMachine = async (id) => {
    if (!window.confirm('Delete this machine?')) return;
    try { await productionApi.machines.delete(id); toast.success('Machine deleted'); loadAll(); }
    catch { toast.error('Delete failed'); }
  };
  const deleteWO = async (id) => {
    if (!window.confirm('Delete this work order?')) return;
    try { await productionApi.workOrders.delete(id); toast.success('Work order deleted'); loadAll(); }
    catch { toast.error('Delete failed'); }
  };

  const planCols = [
    { key: 'planId', label: 'S.No' },
    { key: 'planName', label: 'Plan Name' },
    { key: 'plantId', label: 'Plant ID' },
    { key: 'startDate', label: 'Start' },
    { key: 'endDate', label: 'End' },
    { key: 'targetUnits', label: 'Target Units', render: r => r.targetUnits?.toLocaleString() },
    { key: 'status', label: 'Status', render: r => <Badge status={r.status} /> },
    { key: 'actions', label: 'Actions', render: r => (
      <div className="flex gap-2">
        <button onClick={() => openModal('editPlan', r)} className="text-blue-400 hover:text-blue-300"><Pencil size={14} /></button>
        <button onClick={() => deletePlan(r.planId)} className="text-red-400 hover:text-red-300"><Trash2 size={14} /></button>
      </div>
    )},
  ];

  const machineCols = [
    { key: 'machineId', label: 'S.no' },
    { key: 'name', label: 'Machine' },
    { key: 'plantId', label: 'Plant ID' },
    { key: 'capacity', label: 'Capacity' },
    { key: 'status', label: 'Status', render: r => <Badge status={r.status} /> },
    { key: 'actions', label: 'Actions', render: r => (
      <div className="flex gap-2">
        <button onClick={() => openModal('editMachine', r)} className="text-blue-400 hover:text-blue-300"><Pencil size={14} /></button>
        <button onClick={() => deleteMachine(r.machineId)} className="text-red-400 hover:text-red-300"><Trash2 size={14} /></button>
      </div>
    )},
  ];

  const woCols = [
    { key: 'workOrderId', label: 'Work Order Id' },
    { key: 'productId', label: 'Product' },
    { key: 'planName', label: 'Plan' },
    { key: 'machineName', label: 'Machine' },
    { key: 'quantity', label: 'Qty' },
    { key: 'status', label: 'Status', render: r => <Badge status={r.status} /> },
    { key: 'actions', label: 'Actions', render: r => (
      <div className="flex gap-2">
        <button onClick={() => openModal('editWO', r)} className="text-blue-400 hover:text-blue-300"><Pencil size={14} /></button>
        <button onClick={() => deleteWO(r.workOrderId)} className="text-red-400 hover:text-red-300"><Trash2 size={14} /></button>
      </div>
    )},
  ];

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Production</h1>
          <p className="text-slate-400 text-sm mt-1">Plans, machines & work orders</p>
        </div>
        <div className="flex gap-2">
          <button onClick={loadAll} className="btn-secondary flex items-center gap-2"><RefreshCw size={14} />Refresh</button>
          {tab === 'plans' && <button onClick={() => openModal('newPlan')} className="btn-primary flex items-center gap-2"><Plus size={14} />New Plan</button>}
          {tab === 'machines' && <button onClick={() => openModal('newMachine')} className="btn-primary flex items-center gap-2"><Plus size={14} />New Machine</button>}
          {tab === 'workOrders' && <button onClick={() => openModal('newWO')} className="btn-primary flex items-center gap-2"><Plus size={14} />New Work Order</button>}
        </div>
      </div>

      <div className="grid grid-cols-4 gap-4 mb-6">
        <StatCard title="Plans" value={plans.length} icon={Factory} color="blue" />
        <StatCard title="Machines" value={machines.length} subtitle={`${machines.filter(m=>m.status==='ACTIVE').length} active`} icon={Factory} color="green" />
        <StatCard title="Work Orders" value={workOrders.length} icon={Factory} color="yellow" />
        <StatCard title="Halted WOs" value={workOrders.filter(w=>w.status==='HALTED').length} icon={Factory} color="red" />
      </div>

      <div className="card">
        <div className="flex gap-1 mb-4 border-b border-slate-200 pb-3">
          {['plans','machines','workOrders'].map(t => (
            <button key={t} onClick={() => setTab(t)}
              className={`px-4 py-1.5 rounded-lg text-sm font-medium transition-colors ${tab===t ? 'bg-blue-600 text-slate-800' : 'text-slate-400 hover:text-slate-700'}`}>
              {t === 'workOrders' ? 'Work Orders' : t.charAt(0).toUpperCase()+t.slice(1)}
            </button>
          ))}
        </div>
        {tab === 'plans' && <Table columns={planCols} data={plans} loading={loading} />}
        {tab === 'machines' && <Table columns={machineCols} data={machines} loading={loading} />}
        {tab === 'workOrders' && <Table columns={woCols} data={workOrders} loading={loading} />}
      </div>

      {/* Plan Modals */}
      {(modal.type === 'newPlan' || modal.type === 'editPlan') && (
        <PlanModal isOpen={modal.open} onClose={closeModal} plan={modal.data} />
      )}
      {(modal.type === 'newMachine' || modal.type === 'editMachine') && (
        <MachineModal isOpen={modal.open} onClose={closeModal} machine={modal.data} />
      )}
      {(modal.type === 'newWO' || modal.type === 'editWO') && (
        <WorkOrderModal isOpen={modal.open} onClose={closeModal} wo={modal.data} plans={plans} machines={machines} />
      )}
    </div>
  );
}

function PlanModal({ isOpen, onClose, plan }) {
  const [form, setForm] = useState({ planName:'', plantId:1, startDate:'', endDate:'', targetUnits:100, status:'PLANNED', ...plan });
  const [saving, setSaving] = useState(false);
  const set = (k,v) => setForm(f => ({...f,[k]:v}));
  const save = async () => {
    setSaving(true);
    try {
      if (plan?.planId) await productionApi.plans.update(plan.planId, form);
      else await productionApi.plans.create(form);
      toast.success(`Plan ${plan ? 'updated' : 'created'}`);
      onClose();
    } catch(e) { toast.error(e.response?.data?.message || 'Save failed'); }
    finally { setSaving(false); }
  };
  return (
    <Modal isOpen={isOpen} onClose={onClose} title={plan ? 'Edit Plan' : 'New Production Plan'}>
      <div className="space-y-3">
        <div><label className="label">Plan Name</label><input className="input" value={form.planName} onChange={e=>set('planName',e.target.value)} /></div>
        <div className="grid grid-cols-2 gap-3">
          <div><label className="label">Plant ID</label><input type="number" className="input" value={form.plantId} onChange={e=>set('plantId',e.target.value)} /></div>
          <div><label className="label">Target Units</label><input type="number" className="input" value={form.targetUnits} onChange={e=>set('targetUnits',e.target.value)} /></div>
        </div>
        <div className="grid grid-cols-2 gap-3">
          <div><label className="label">Start Date</label><input type="date" className="input" value={form.startDate?.slice(0,10)||''} onChange={e=>set('startDate',e.target.value)} /></div>
          <div><label className="label">End Date</label><input type="date" className="input" value={form.endDate?.slice(0,10)||''} onChange={e=>set('endDate',e.target.value)} /></div>
        </div>
        <div><label className="label">Status</label>
          <select className="input" value={form.status} onChange={e=>set('status',e.target.value)}>
            {PLAN_STATUSES.map(s=><option key={s}>{s}</option>)}
          </select>
        </div>
        <div className="flex gap-2 justify-end pt-2">
          <button className="btn-secondary" onClick={onClose}>Cancel</button>
          <button className="btn-primary" onClick={save} disabled={saving}>{saving?'Saving...':'Save'}</button>
        </div>
      </div>
    </Modal>
  );
}

function MachineModal({ isOpen, onClose, machine }) {
  const [form, setForm] = useState({ name:'', plantId:1, capacity:100, status:'IDLE', ...machine });
  const [saving, setSaving] = useState(false);
  const set = (k,v) => setForm(f=>({...f,[k]:v}));
  const save = async () => {
    setSaving(true);
    try {
      if (machine?.machineId) await productionApi.machines.update(machine.machineId, form);
      else await productionApi.machines.create(form);
      toast.success(`Machine ${machine?'updated':'created'}`);
      onClose();
    } catch(e) { toast.error(e.response?.data?.message||'Save failed'); }
    finally { setSaving(false); }
  };
  return (
    <Modal isOpen={isOpen} onClose={onClose} title={machine?'Edit Machine':'New Machine'}>
      <div className="space-y-3">
        <div><label className="label">Name</label><input className="input" value={form.name} onChange={e=>set('name',e.target.value)} /></div>
        <div className="grid grid-cols-2 gap-3">
          <div><label className="label">Plant ID</label><input type="number" className="input" value={form.plantId} onChange={e=>set('plantId',e.target.value)} /></div>
          <div><label className="label">Capacity</label><input type="number" className="input" value={form.capacity} onChange={e=>set('capacity',e.target.value)} /></div>
        </div>
        <div><label className="label">Status</label>
          <select className="input" value={form.status} onChange={e=>set('status',e.target.value)}>
            {MACHINE_STATUSES.map(s=><option key={s}>{s}</option>)}
          </select>
        </div>
        <div className="flex gap-2 justify-end pt-2">
          <button className="btn-secondary" onClick={onClose}>Cancel</button>
          <button className="btn-primary" onClick={save} disabled={saving}>{saving?'Saving...':'Save'}</button>
        </div>
      </div>
    </Modal>
  );
}

function WorkOrderModal({ isOpen, onClose, wo, plans, machines }) {
  const [form, setForm] = useState({ planId:'', machineId:'', productId:'', quantity:100, status:'PENDING', ...wo, planId: wo?.planId||'', machineId: wo?.machineId||'' });
  const [saving, setSaving] = useState(false);
  const set = (k,v) => setForm(f=>({...f,[k]:v}));
  const save = async () => {
    setSaving(true);
    try {
      const payload = {...form, planId: Number(form.planId), machineId: form.machineId ? Number(form.machineId) : null };
      if (wo?.workOrderId) await productionApi.workOrders.update(wo.workOrderId, payload);
      else await productionApi.workOrders.create(payload);
      toast.success(`Work order ${wo?'updated':'created'}`);
      onClose();
    } catch(e) { toast.error(e.response?.data?.message||'Save failed'); }
    finally { setSaving(false); }
  };
  return (
    <Modal isOpen={isOpen} onClose={onClose} title={wo?'Edit Work Order':'New Work Order'}>
      <div className="space-y-3">
        <div><label className="label">Production Plan</label>
          <select className="input" value={form.planId} onChange={e=>set('planId',e.target.value)}>
            <option value="">Select plan</option>
            {plans.map(p=><option key={p.planId} value={p.planId}>{p.planName}</option>)}
          </select>
        </div>
        <div><label className="label">Machine (optional)</label>
          <select className="input" value={form.machineId} onChange={e=>set('machineId',e.target.value)}>
            <option value="">No machine</option>
            {machines.map(m=><option key={m.machineId} value={m.machineId}>{m.name}</option>)}
          </select>
        </div>
        <div className="grid grid-cols-2 gap-3">
          <div><label className="label">Product ID</label><input className="input" value={form.productId} onChange={e=>set('productId',e.target.value)} /></div>
          <div><label className="label">Quantity</label><input type="number" className="input" value={form.quantity} onChange={e=>set('quantity',e.target.value)} /></div>
        </div>
        <div><label className="label">Status</label>
          <select className="input" value={form.status} onChange={e=>set('status',e.target.value)}>
            {WO_STATUSES.map(s=><option key={s}>{s}</option>)}
          </select>
        </div>
        <div className="flex gap-2 justify-end pt-2">
          <button className="btn-secondary" onClick={onClose}>Cancel</button>
          <button className="btn-primary" onClick={save} disabled={saving}>{saving?'Saving...':'Save'}</button>
        </div>
      </div>
    </Modal>
  );
}