import React, { useEffect, useState } from 'react';
import { userApi } from '../../services/api';
import { authApi } from '../../services/api';
import Table from '../../components/common/Table';
import Badge from '../../components/common/Badge';
import Modal from '../../components/common/Modal';
import StatCard from '../../components/common/StatCard';
import { Users, Plus, Pencil, Trash2, RefreshCw } from 'lucide-react';
import toast from 'react-hot-toast';

const ROLES = ['ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS'];

export default function UsersPage() {
  const [users, setUsers] = useState([]);
  const [auditLogs, setAuditLogs] = useState([]);
  const [tab, setTab] = useState('users');
  const [loading, setLoading] = useState(false);
  const [modal, setModal] = useState({open:false,type:'',data:null});

  const load = async () => {
    setLoading(true);
    try {
      const [u, a] = await Promise.all([userApi.getAll(), userApi.getAuditLogs()]);
      setUsers(u.data); setAuditLogs(a.data);
    } catch { toast.error('Load failed'); }
    finally { setLoading(false); }
  };
  useEffect(()=>{load();},[]);

  const delUser = async(id)=>{ if(!window.confirm('Delete user?')) return; try{await userApi.delete(id);toast.success('Deleted');load();}catch{toast.error('Failed');} };
  const closeModal = ()=>{ setModal({open:false,type:'',data:null}); load(); };

  const userCols = [
    {key:'userId',label:'S.no'},{key:'name',label:'Name'},{key:'email',label:'Email'},
    {key:'phone',label:'Phone'},{key:'role',label:'Role',render:r=><span className="badge bg-blue-500/20 text-blue-300">{r.role}</span>},
    {key:'active',label:'Active',render:r=><Badge status={r.active?'ACTIVE':'INACTIVE'}/>},
    {key:'createdAt',label:'Created',render:r=>r.createdAt?.slice(0,10)},
    {key:'actions',label:'',render:r=><div className="flex gap-2"><button onClick={()=>setModal({open:true,type:'edit',data:r})} className="text-blue-400 hover:text-blue-300"><Pencil size={14}/></button><button onClick={()=>delUser(r.userId)} className="text-red-400 hover:text-red-300"><Trash2 size={14}/></button></div>}
  ];
  const logCols = [
    {key:'auditId',label:'S.no'},{key:'userId',label:'User ID'},{key:'action',label:'Action'},
    {key:'resource',label:'Resource'},{key:'timestamp',label:'Time',render:r=>r.timestamp?.replace('T',' ').slice(0,19)},
  ];

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div><h1 className="text-2xl font-bold text-slate-800">Users & Access</h1><p className="text-slate-400 text-sm mt-1">User management & audit logs</p></div>
        <div className="flex gap-2">
          <button onClick={load} className="btn-secondary flex items-center gap-2"><RefreshCw size={14}/>Refresh</button>
          {tab==='users'&&<button onClick={()=>setModal({open:true,type:'new',data:null})} className="btn-primary flex items-center gap-2"><Plus size={14}/>New User</button>}
        </div>
      </div>
      <div className="grid grid-cols-3 gap-4 mb-6">
        <StatCard title="Total Users" value={users.length} icon={Users} color="blue"/>
        <StatCard title="Active Users" value={users.filter(u=>u.active).length} icon={Users} color="green"/>
        <StatCard title="Audit Events" value={auditLogs.length} icon={Users} color="purple"/>
      </div>
      <div className="card">
        <div className="flex gap-1 mb-4 border-b border-slate-200 pb-3">
          {[['users','Users'],['audit','Audit Logs']].map(([t,l])=>(
            <button key={t} onClick={()=>setTab(t)} className={`px-4 py-1.5 rounded-lg text-sm font-medium transition-colors ${tab===t?'bg-blue-600 text-slate-800':'text-slate-400 hover:text-slate-700'}`}>{l}</button>
          ))}
        </div>
        {tab==='users'&&<Table columns={userCols} data={users} loading={loading}/>}
        {tab==='audit'&&<Table columns={logCols} data={auditLogs} loading={loading}/>}
      </div>
      {['new','edit'].includes(modal.type)&&<UserModal isOpen={modal.open} onClose={closeModal} user={modal.data}/>}
    </div>
  );
}

function UserModal({isOpen,onClose,user}) {
  const [form,setForm]=useState({name:'',email:'',password:'',phone:'',role:'PLANNER',active:true,...user});
  const [saving,setSaving]=useState(false);
  const set=(k,v)=>setForm(f=>({...f,[k]:v}));
  const save=async()=>{
    setSaving(true);
    try{
      if(user?.userId) await userApi.update(user.userId,{name:form.name,phone:form.phone,role:form.role,active:form.active});
      else await authApi.register({name:form.name,email:form.email,password:form.password,phone:form.phone,role:form.role});
      toast.success(user?'Updated':'Created'); onClose();
    }catch(e){toast.error(e.response?.data?.message||'Save failed');}
    finally{setSaving(false);}
  };
  return(<Modal isOpen={isOpen} onClose={onClose} title={user?'Edit User':'New User'}><div className="space-y-3">
    <div><label className="label">Full Name</label><input className="input" value={form.name} onChange={e=>set('name',e.target.value)}/></div>
    {!user&&<div><label className="label">Email</label><input type="email" className="input" value={form.email} onChange={e=>set('email',e.target.value)}/></div>}
    {!user&&<div><label className="label">Password</label><input type="password" className="input" value={form.password} onChange={e=>set('password',e.target.value)}/></div>}
    <div><label className="label">Phone</label><input className="input" value={form.phone} onChange={e=>set('phone',e.target.value)}/></div>
    <div><label className="label">Role</label><select className="input" value={form.role} onChange={e=>set('role',e.target.value)}>{ROLES.map(r=><option key={r}>{r}</option>)}</select></div>
    {user&&<div className="flex items-center gap-2"><input type="checkbox" id="active" checked={form.active} onChange={e=>set('active',e.target.checked)} className="w-4 h-4"/><label htmlFor="active" className="text-sm text-slate-600">Active</label></div>}
    <div className="flex gap-2 justify-end pt-2"><button className="btn-secondary" onClick={onClose}>Cancel</button><button className="btn-primary" onClick={save} disabled={saving}>{saving?'Saving...':'Save'}</button></div>
  </div></Modal>);
}