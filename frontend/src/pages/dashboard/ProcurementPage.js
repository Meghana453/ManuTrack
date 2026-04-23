import React, { useEffect, useState } from 'react';
import { procurementApi } from '../../services/api';
import Table from '../../components/common/Table';
import Badge from '../../components/common/Badge';
import Modal from '../../components/common/Modal';
import StatCard from '../../components/common/StatCard';
import { ShoppingCart, Plus, Pencil, Trash2, RefreshCw } from 'lucide-react';
import toast from 'react-hot-toast';

const PO_STATUSES = ['OPEN','DELIVERED','CANCELLED'];
const INV_STATUSES = ['PENDING','PAID','OVERDUE'];
const VENDOR_STATUSES = ['ACTIVE','INACTIVE'];

export default function ProcurementPage() {
  const [tab, setTab] = useState('vendors');
  const [vendors, setVendors] = useState([]);
  const [pos, setPos] = useState([]);
  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modal, setModal] = useState({open:false,type:'',data:null});

  const load = async () => {
    setLoading(true);
    try {
      const [v,p,i] = await Promise.all([procurementApi.vendors.getAll(), procurementApi.purchaseOrders.getAll(), procurementApi.invoices.getAll()]);
      setVendors(v.data); setPos(p.data); setInvoices(i.data);
    } catch { toast.error('Load failed'); }
    finally { setLoading(false); }
  };
  useEffect(()=>{load();},[]);

  const delV = async(id)=>{ if(!window.confirm('Delete?')) return; try{await procurementApi.vendors.delete(id);toast.success('Deleted');load();}catch{toast.error('Failed');} };
  const delPO = async(id)=>{ if(!window.confirm('Delete?')) return; try{await procurementApi.purchaseOrders.delete(id);toast.success('Deleted');load();}catch{toast.error('Failed');} };
  const delInv = async(id)=>{ if(!window.confirm('Delete?')) return; try{await procurementApi.invoices.delete(id);toast.success('Deleted');load();}catch{toast.error('Failed');} };

  const vendorCols = [
    {key:'vendorId',label:'S.NO'},{key:'name',label:'Vendor'},
    {key:'contactInfo',label:'Contact'},{key:'rating',label:'Rating',render:r=>`⭐ ${r.rating?.toFixed(1)}`},
    {key:'status',label:'Status',render:r=><Badge status={r.status}/>},
    {key:'actions',label:'',render:r=><div className="flex gap-2"><button onClick={()=>setModal({open:true,type:'editVendor',data:r})} className="text-blue-400 hover:text-blue-300"><Pencil size={14}/></button><button onClick={()=>delV(r.vendorId)} className="text-red-400 hover:text-red-300"><Trash2 size={14}/></button></div>}
  ];
  const poCols = [
    {key:'poId',label:'S.NO'},{key:'vendorName',label:'Vendor'},{key:'itemId',label:'Item ID'},
    {key:'quantity',label:'Qty'},{key:'unitPrice',label:'Unit Price',render:r=>`$${r.unitPrice}`},
    {key:'totalAmount',label:'Total',render:r=>`$${(r.totalAmount||0).toLocaleString()}`},
    {key:'orderDate',label:'Order Date'},{key:'expectedDeliveryDate',label:'Exp. Delivery'},
    {key:'status',label:'Status',render:r=><Badge status={r.status}/>},
    {key:'actions',label:'',render:r=><div className="flex gap-2"><button onClick={()=>setModal({open:true,type:'editPO',data:r})} className="text-blue-400 hover:text-blue-300"><Pencil size={14}/></button><button onClick={()=>delPO(r.poId)} className="text-red-400 hover:text-red-300"><Trash2 size={14}/></button></div>}
  ];
  const invCols = [
    {key:'invoiceId',label:'S.NO'},{key:'poId',label:'PO '},{key:'vendorName',label:'Vendor'},
    {key:'amount',label:'Amount',render:r=>`$${r.amount?.toLocaleString()}`},
    {key:'issueDate',label:'Issue Date'},{key:'dueDate',label:'Due Date'},
    {key:'status',label:'Status',render:r=><Badge status={r.status}/>},
    {key:'actions',label:'',render:r=><div className="flex gap-2"><button onClick={()=>setModal({open:true,type:'editInvoice',data:r})} className="text-blue-400 hover:text-blue-300"><Pencil size={14}/></button><button onClick={()=>delInv(r.invoiceId)} className="text-red-400 hover:text-red-300"><Trash2 size={14}/></button></div>}
  ];

  const closeModal = () => { setModal({open:false,type:'',data:null}); load(); };
  const totalPOValue = pos.reduce((s,p)=>s+(p.totalAmount||0),0);

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div><h1 className="text-2xl font-bold text-slate-800">Procurement</h1><p className="text-slate-400 text-sm mt-1">Vendors, POs & invoices</p></div>
        <div className="flex gap-2">
          <button onClick={load} className="btn-secondary flex items-center gap-2"><RefreshCw size={14}/>Refresh</button>
          {tab==='vendors'&&<button onClick={()=>setModal({open:true,type:'newVendor',data:null})} className="btn-primary flex items-center gap-2"><Plus size={14}/>New Vendor</button>}
          {tab==='pos'&&<button onClick={()=>setModal({open:true,type:'newPO',data:null})} className="btn-primary flex items-center gap-2"><Plus size={14}/>New PO</button>}
          {tab==='invoices'&&<button onClick={()=>setModal({open:true,type:'newInvoice',data:null})} className="btn-primary flex items-center gap-2"><Plus size={14}/>New Invoice</button>}
        </div>
      </div>
      <div className="grid grid-cols-4 gap-4 mb-6">
        <StatCard title="Vendors" value={vendors.length} subtitle={`${vendors.filter(v=>v.status==='ACTIVE').length} active`} icon={ShoppingCart} color="blue"/>
        <StatCard title="Open POs" value={pos.filter(p=>p.status==='OPEN').length} icon={ShoppingCart} color="yellow"/>
        <StatCard title="Total PO Value" value={`$${totalPOValue.toLocaleString()}`} icon={ShoppingCart} color="green"/>
        <StatCard title="Overdue Invoices" value={invoices.filter(i=>i.status==='OVERDUE').length} icon={ShoppingCart} color={invoices.filter(i=>i.status==='OVERDUE').length>0?'red':'green'}/>
      </div>
      <div className="card">
        <div className="flex gap-1 mb-4 border-b border-slate-200 pb-3">
          {[['vendors','Vendors'],['pos','Purchase Orders'],['invoices','Invoices']].map(([t,l])=>(
            <button key={t} onClick={()=>setTab(t)} className={`px-4 py-1.5 rounded-lg text-sm font-medium transition-colors ${tab===t?'bg-blue-600 text-slate-800':'text-slate-400 hover:text-slate-700'}`}>{l}</button>
          ))}
        </div>
        {tab==='vendors'&&<Table columns={vendorCols} data={vendors} loading={loading}/>}
        {tab==='pos'&&<Table columns={poCols} data={pos} loading={loading}/>}
        {tab==='invoices'&&<Table columns={invCols} data={invoices} loading={loading}/>}
      </div>
      {['newVendor','editVendor'].includes(modal.type)&&<VendorModal isOpen={modal.open} onClose={closeModal} vendor={modal.data}/>}
      {['newPO','editPO'].includes(modal.type)&&<POModal isOpen={modal.open} onClose={closeModal} po={modal.data} vendors={vendors}/>}
      {['newInvoice','editInvoice'].includes(modal.type)&&<InvoiceModal isOpen={modal.open} onClose={closeModal} invoice={modal.data} pos={pos}/>}
    </div>
  );
}

function VendorModal({isOpen,onClose,vendor}) {
  const [form,setForm]=useState({name:'',contactInfo:'',rating:4.0,status:'ACTIVE',...vendor});
  const [saving,setSaving]=useState(false);
  const set=(k,v)=>setForm(f=>({...f,[k]:v}));
  const save=async()=>{setSaving(true);try{if(vendor?.vendorId)await procurementApi.vendors.update(vendor.vendorId,form);else await procurementApi.vendors.create(form);toast.success(vendor?'Updated':'Created');onClose();}catch(e){toast.error(e.response?.data?.message||'Failed');}finally{setSaving(false);}};
  return(<Modal isOpen={isOpen} onClose={onClose} title={vendor?'Edit Vendor':'New Vendor'}><div className="space-y-3">
    <div><label className="label">Name</label><input className="input" value={form.name} onChange={e=>set('name',e.target.value)}/></div>
    <div><label className="label">Contact Info</label><input className="input" value={form.contactInfo} onChange={e=>set('contactInfo',e.target.value)}/></div>
    <div className="grid grid-cols-2 gap-3">
      <div><label className="label">Rating (0-5)</label><input type="number" step="0.1" min="0" max="5" className="input" value={form.rating} onChange={e=>set('rating',e.target.value)}/></div>
      <div><label className="label">Status</label><select className="input" value={form.status} onChange={e=>set('status',e.target.value)}>{VENDOR_STATUSES.map(s=><option key={s}>{s}</option>)}</select></div>
    </div>
    <div className="flex gap-2 justify-end pt-2"><button className="btn-secondary" onClick={onClose}>Cancel</button><button className="btn-primary" onClick={save} disabled={saving}>{saving?'Saving...':'Save'}</button></div>
  </div></Modal>);
}

function POModal({isOpen,onClose,po,vendors}) {
  const [form,setForm]=useState({vendorId:'',itemId:'',quantity:1,unitPrice:0,orderDate:new Date().toISOString().slice(0,10),expectedDeliveryDate:'',status:'OPEN',...po,vendorId:po?.vendorId||''});
  const [saving,setSaving]=useState(false);
  const set=(k,v)=>setForm(f=>({...f,[k]:v}));
  const save=async()=>{setSaving(true);try{const p={...form,vendorId:Number(form.vendorId),itemId:Number(form.itemId),quantity:Number(form.quantity),unitPrice:Number(form.unitPrice)};if(po?.poId)await procurementApi.purchaseOrders.update(po.poId,p);else await procurementApi.purchaseOrders.create(p);toast.success(po?'Updated':'Created');onClose();}catch(e){toast.error(e.response?.data?.message||'Failed');}finally{setSaving(false);}};
  return(<Modal isOpen={isOpen} onClose={onClose} title={po?'Edit PO':'New Purchase Order'}><div className="space-y-3">
    <div><label className="label">Vendor</label><select className="input" value={form.vendorId} onChange={e=>set('vendorId',e.target.value)}><option value="">Select vendor</option>{vendors.map(v=><option key={v.vendorId} value={v.vendorId}>{v.name}</option>)}</select></div>
    <div className="grid grid-cols-2 gap-3">
      <div><label className="label">Item ID</label><input type="number" className="input" value={form.itemId} onChange={e=>set('itemId',e.target.value)}/></div>
      <div><label className="label">Status</label><select className="input" value={form.status} onChange={e=>set('status',e.target.value)}>{PO_STATUSES.map(s=><option key={s}>{s}</option>)}</select></div>
    </div>
    <div className="grid grid-cols-2 gap-3">
      <div><label className="label">Quantity</label><input type="number" className="input" value={form.quantity} onChange={e=>set('quantity',e.target.value)}/></div>
      <div><label className="label">Unit Price ($)</label><input type="number" step="0.01" className="input" value={form.unitPrice} onChange={e=>set('unitPrice',e.target.value)}/></div>
    </div>
    <div className="grid grid-cols-2 gap-3">
      <div><label className="label">Order Date</label><input type="date" className="input" value={form.orderDate?.slice(0,10)||''} onChange={e=>set('orderDate',e.target.value)}/></div>
      <div><label className="label">Expected Delivery</label><input type="date" className="input" value={form.expectedDeliveryDate?.slice(0,10)||''} onChange={e=>set('expectedDeliveryDate',e.target.value)}/></div>
    </div>
    <div className="flex gap-2 justify-end pt-2"><button className="btn-secondary" onClick={onClose}>Cancel</button><button className="btn-primary" onClick={save} disabled={saving}>{saving?'Saving...':'Save'}</button></div>
  </div></Modal>);
}

function InvoiceModal({isOpen,onClose,invoice,pos}) {
  const [form,setForm]=useState({poId:'',amount:0,issueDate:new Date().toISOString().slice(0,10),dueDate:'',status:'PENDING',...invoice,poId:invoice?.poId||''});
  const [saving,setSaving]=useState(false);
  const set=(k,v)=>setForm(f=>({...f,[k]:v}));
  const save=async()=>{setSaving(true);try{const p={...form,poId:Number(form.poId),amount:Number(form.amount)};if(invoice?.invoiceId)await procurementApi.invoices.update(invoice.invoiceId,p);else await procurementApi.invoices.create(p);toast.success(invoice?'Updated':'Created');onClose();}catch(e){toast.error(e.response?.data?.message||'Failed');}finally{setSaving(false);}};
  return(<Modal isOpen={isOpen} onClose={onClose} title={invoice?'Edit Invoice':'New Invoice'}><div className="space-y-3">
    <div><label className="label">Purchase Order</label><select className="input" value={form.poId} onChange={e=>set('poId',e.target.value)}><option value="">Select PO</option>{pos.map(p=><option key={p.poId} value={p.poId}>PO #{p.poId} - {p.vendorName}</option>)}</select></div>
    <div><label className="label">Amount ($)</label><input type="number" step="0.01" className="input" value={form.amount} onChange={e=>set('amount',e.target.value)}/></div>
    <div className="grid grid-cols-2 gap-3">
      <div><label className="label">Issue Date</label><input type="date" className="input" value={form.issueDate?.slice(0,10)||''} onChange={e=>set('issueDate',e.target.value)}/></div>
      <div><label className="label">Due Date</label><input type="date" className="input" value={form.dueDate?.slice(0,10)||''} onChange={e=>set('dueDate',e.target.value)}/></div>
    </div>
    <div><label className="label">Status</label><select className="input" value={form.status} onChange={e=>set('status',e.target.value)}>{INV_STATUSES.map(s=><option key={s}>{s}</option>)}</select></div>
    <div className="flex gap-2 justify-end pt-2"><button className="btn-secondary" onClick={onClose}>Cancel</button><button className="btn-primary" onClick={save} disabled={saving}>{saving?'Saving...':'Save'}</button></div>
  </div></Modal>);
}