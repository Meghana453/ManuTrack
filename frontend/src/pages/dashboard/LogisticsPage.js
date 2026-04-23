import React, { useEffect, useState } from 'react';
import { logisticsApi } from '../../services/api';
import Table from '../../components/common/Table';
import Badge from '../../components/common/Badge';
import Modal from '../../components/common/Modal';
import StatCard from '../../components/common/StatCard';
import { Truck, Plus, Pencil, Trash2, RefreshCw } from 'lucide-react';
import toast from 'react-hot-toast';
//'CANCELLED'
const SHIPMENT_STATUSES = ['SCHEDULED','IN_TRANSIT','DELIVERED','DELAYED'];
const CARRIER_STATUSES = ['ACTIVE','INACTIVE'];

export default function LogisticsPage() {
  const [tab, setTab] = useState('shipments');
  const [shipments, setShipments] = useState([]);
  const [carriers, setCarriers] = useState([]);
  const [routes, setRoutes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modal, setModal] = useState({open:false,type:'',data:null});

  const load = async () => {
    setLoading(true);
    try {
      const [s,c,r] = await Promise.all([logisticsApi.shipments.getAll(), logisticsApi.carriers.getAll(), logisticsApi.routes.getAll()]);
      setShipments(s.data); setCarriers(c.data); setRoutes(r.data);
    } catch { toast.error('Load failed'); }
    finally { setLoading(false); }
  };
  useEffect(()=>{load();},[]);

  const delS = async(id)=>{ if(!window.confirm('Delete?')) return; try{await logisticsApi.shipments.delete(id);toast.success('Deleted');load();}catch{toast.error('Failed');} };
  const delC = async(id)=>{ if(!window.confirm('Delete?')) return; try{await logisticsApi.carriers.delete(id);toast.success('Deleted');load();}catch{toast.error('Failed');} };
  const delR = async(id)=>{ if(!window.confirm('Delete?')) return; try{await logisticsApi.routes.delete(id);toast.success('Deleted');load();}catch{toast.error('Failed');} };

  const shipCols = [
    {key:'shipmentId',label:'S.no'},{key:'destination',label:'Destination'},
    {key:'carrierName',label:'Carrier'},{key:'scheduledDate',label:'Scheduled'},
    {key:'actualDispatchDate',label:'Dispatched'},{key:'deliveryDate',label:'Delivered'},
    {key:'status',label:'Status',render:r=><Badge status={r.status}/>},
    {key:'actions',label:'',render:r=><div className="flex gap-2"><button onClick={()=>setModal({open:true,type:'editShip',data:r})} className="text-blue-400 hover:text-blue-300"><Pencil size={14}/></button><button onClick={()=>delS(r.shipmentId)} className="text-red-400 hover:text-red-300"><Trash2 size={14}/></button></div>}
  ];
  const carrierCols = [
    {key:'carrierId',label:'S.no'},{key:'name',label:'Carrier'},{key:'contactInfo',label:'Contact'},
    {key:'rating',label:'Rating',render:r=>`⭐ ${r.rating?.toFixed(1)}`},
    {key:'status',label:'Status',render:r=><Badge status={r.status}/>},
    {key:'actions',label:'',render:r=><div className="flex gap-2"><button onClick={()=>setModal({open:true,type:'editCarrier',data:r})} className="text-blue-400 hover:text-blue-300"><Pencil size={14}/></button><button onClick={()=>delC(r.carrierId)} className="text-red-400 hover:text-red-300"><Trash2 size={14}/></button></div>}
  ];
  const routeCols = [
    {key:'routeId',label:'S.no'},{key:'origin',label:'Origin'},{key:'destination',label:'Destination'},
    {key:'distance',label:'Distance (km)'},{key:'estimatedTimeHours',label:'Est. Time (hrs)'},
    {key:'actions',label:'',render:r=><div className="flex gap-2"><button onClick={()=>setModal({open:true,type:'editRoute',data:r})} className="text-blue-400 hover:text-blue-300"><Pencil size={14}/></button><button onClick={()=>delR(r.routeId)} className="text-red-400 hover:text-red-300"><Trash2 size={14}/></button></div>}
  ];

  const closeModal = ()=>{ setModal({open:false,type:'',data:null}); load(); };
  const delayed = shipments.filter(s=>s.status==='DELAYED').length;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div><h1 className="text-2xl font-bold text-slate-800">Logistics</h1><p className="text-slate-400 text-sm mt-1">Shipments, carriers & routes</p></div>
        <div className="flex gap-2">
          <button onClick={load} className="btn-secondary flex items-center gap-2"><RefreshCw size={14}/>Refresh</button>
          {tab==='shipments'&&<button onClick={()=>setModal({open:true,type:'newShip',data:null})} className="btn-primary flex items-center gap-2"><Plus size={14}/>New Shipment</button>}
          {tab==='carriers'&&<button onClick={()=>setModal({open:true,type:'newCarrier',data:null})} className="btn-primary flex items-center gap-2"><Plus size={14}/>New Carrier</button>}
          {tab==='routes'&&<button onClick={()=>setModal({open:true,type:'newRoute',data:null})} className="btn-primary flex items-center gap-2"><Plus size={14}/>New Route</button>}
        </div>
      </div>
      <div className="grid grid-cols-4 gap-4 mb-6">
        <StatCard title="Total Shipments" value={shipments.length} icon={Truck} color="blue"/>
        <StatCard title="In Transit" value={shipments.filter(s=>s.status==='IN_TRANSIT').length} icon={Truck} color="yellow"/>
        <StatCard title="Delivered" value={shipments.filter(s=>s.status==='DELIVERED').length} icon={Truck} color="green"/>
        <StatCard title="Delayed" value={delayed} icon={Truck} color={delayed>0?'red':'green'}/>
      </div>
      <div className="card">
        <div className="flex gap-1 mb-4 border-b border-slate-200 pb-3">
          {[['shipments','Shipments'],['carriers','Carriers'],['routes','Routes']].map(([t,l])=>(
            <button key={t} onClick={()=>setTab(t)} className={`px-4 py-1.5 rounded-lg text-sm font-medium transition-colors ${tab===t?'bg-blue-600 text-slate-800':'text-slate-400 hover:text-slate-700'}`}>{l}</button>
          ))}
        </div>
        {tab==='shipments'&&<Table columns={shipCols} data={shipments} loading={loading}/>}
        {tab==='carriers'&&<Table columns={carrierCols} data={carriers} loading={loading}/>}
        {tab==='routes'&&<Table columns={routeCols} data={routes} loading={loading}/>}
      </div>
      {['newShip','editShip'].includes(modal.type)&&<ShipmentModal isOpen={modal.open} onClose={closeModal} shipment={modal.data} carriers={carriers}/>}
      {['newCarrier','editCarrier'].includes(modal.type)&&<CarrierModal isOpen={modal.open} onClose={closeModal} carrier={modal.data}/>}
      {['newRoute','editRoute'].includes(modal.type)&&<RouteModal isOpen={modal.open} onClose={closeModal} route={modal.data}/>}
    </div>
  );
}

function ShipmentModal({isOpen,onClose,shipment,carriers}) {
  const [form,setForm]=useState({carrierId:'',originWarehouseId:1,destination:'',scheduledDate:'',status:'SCHEDULED',...shipment,carrierId:shipment?.carrierId||''});
  const [saving,setSaving]=useState(false);
  const set=(k,v)=>setForm(f=>({...f,[k]:v}));
  const save=async()=>{setSaving(true);try{const p={...form,carrierId:form.carrierId?Number(form.carrierId):null};if(shipment?.shipmentId)await logisticsApi.shipments.update(shipment.shipmentId,p);else await logisticsApi.shipments.create(p);toast.success(shipment?'Updated':'Created');onClose();}catch(e){toast.error(e.response?.data?.message||'Failed');}finally{setSaving(false);}};
  return(<Modal isOpen={isOpen} onClose={onClose} title={shipment?'Edit Shipment':'New Shipment'}><div className="space-y-3">
    <div><label className="label">Carrier</label><select className="input" value={form.carrierId} onChange={e=>set('carrierId',e.target.value)}><option value="">No carrier</option>{carriers.map(c=><option key={c.carrierId} value={c.carrierId}>{c.name}</option>)}</select></div>
    <div><label className="label">Destination</label><input className="input" value={form.destination} onChange={e=>set('destination',e.target.value)}/></div>
    <div className="grid grid-cols-2 gap-3">
      <div><label className="label">Origin Warehouse ID</label><input type="number" className="input" value={form.originWarehouseId} onChange={e=>set('originWarehouseId',e.target.value)}/></div>
      <div><label className="label">Status</label><select className="input" value={form.status} onChange={e=>set('status',e.target.value)}>{SHIPMENT_STATUSES.map(s=><option key={s}>{s}</option>)}</select></div>
    </div>
    <div className="grid grid-cols-2 gap-3">
      <div><label className="label">Scheduled Date</label><input type="date" className="input" value={form.scheduledDate?.slice(0,10)||''} onChange={e=>set('scheduledDate',e.target.value)}/></div>
      <div><label className="label">Actual Dispatch</label><input type="date" className="input" value={form.actualDispatchDate?.slice(0,10)||''} onChange={e=>set('actualDispatchDate',e.target.value)}/></div>
    </div>
    <div><label className="label">Delivery Date</label><input type="date" className="input" value={form.deliveryDate?.slice(0,10)||''} onChange={e=>set('deliveryDate',e.target.value)}/></div>
//    <div className="flex gap-2 justify-end pt-2"><button className="btn-secondary" onClick={onClose}>Cancel</button><button className="btn-primary" onClick={save} disabled={saving}>{saving?'Saving...':'Save'}</button></div>
  </div></Modal>);
}

function CarrierModal({isOpen,onClose,carrier}) {
  const [form,setForm]=useState({name:'',contactInfo:'',rating:4.0,status:'ACTIVE',...carrier});
  const [saving,setSaving]=useState(false);
  const set=(k,v)=>setForm(f=>({...f,[k]:v}));
  const save=async()=>{setSaving(true);try{if(carrier?.carrierId)await logisticsApi.carriers.update(carrier.carrierId,form);else await logisticsApi.carriers.create(form);toast.success(carrier?'Updated':'Created');onClose();}catch(e){toast.error(e.response?.data?.message||'Failed');}finally{setSaving(false);}};
  return(<Modal isOpen={isOpen} onClose={onClose} title={carrier?'Edit Carrier':'New Carrier'}><div className="space-y-3">
    <div><label className="label">Name</label><input className="input" value={form.name} onChange={e=>set('name',e.target.value)}/></div>
    <div><label className="label">Contact Info</label><input className="input" value={form.contactInfo} onChange={e=>set('contactInfo',e.target.value)}/></div>
    <div className="grid grid-cols-2 gap-3">
      <div><label className="label">Rating</label><input type="number" step="0.1" min="0" max="5" className="input" value={form.rating} onChange={e=>set('rating',e.target.value)}/></div>
      <div><label className="label">Status</label><select className="input" value={form.status} onChange={e=>set('status',e.target.value)}>{CARRIER_STATUSES.map(s=><option key={s}>{s}</option>)}</select></div>
    </div>
//    <div className="flex gap-2 justify-end pt-2"><button className="btn-secondary" onClick={onClose}>Cancel</button><button className="btn-primary" onClick={save} disabled={saving}>{saving?'Saving...':'Save'}</button></div>
  </div></Modal>);
}

function RouteModal({isOpen,onClose,route}) {
  const [form,setForm]=useState({origin:'',destination:'',distance:0,estimatedTimeHours:0,...route});
  const [saving,setSaving]=useState(false);
  const set=(k,v)=>setForm(f=>({...f,[k]:v}));
  const save=async()=>{setSaving(true);try{if(route?.routeId)await logisticsApi.routes.update(route.routeId,form);else await logisticsApi.routes.create(form);toast.success(route?'Updated':'Created');onClose();}catch(e){toast.error(e.response?.data?.message||'Failed');}finally{setSaving(false);}};
  return(<Modal isOpen={isOpen} onClose={onClose} title={route?'Edit Route':'New Route'}><div className="space-y-3">
    <div><label className="label">Origin</label><input className="input" value={form.origin} onChange={e=>set('origin',e.target.value)}/></div>
    <div><label className="label">Destination</label><input className="input" value={form.destination} onChange={e=>set('destination',e.target.value)}/></div>
    <div className="grid grid-cols-2 gap-3">
      <div><label className="label">Distance (km)</label><input type="number" className="input" value={form.distance} onChange={e=>set('distance',e.target.value)}/></div>
      <div><label className="label">Est. Time (hrs)</label><input type="number" className="input" value={form.estimatedTimeHours} onChange={e=>set('estimatedTimeHours',e.target.value)}/></div>
    </div>
//   <div className="flex gap-2 justify-end pt-2"><button className="btn-secondary" onClick={onClose}>Cancel</button><button className="btn-primary" onClick={save} disabled={saving}>{saving?'Saving...':'Save'}</button></div>
  </div></Modal>);
}