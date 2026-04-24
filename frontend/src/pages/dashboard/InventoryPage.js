import React, { useEffect, useState } from 'react';
import { inventoryApi } from '../../services/api';
import Table from '../../components/common/Table';
import Badge from '../../components/common/Badge';
import Modal from '../../components/common/Modal';
import StatCard from '../../components/common/StatCard';
import { Package, Plus, Pencil, Trash2, RefreshCw, TrendingDown, MoreVertical } from 'lucide-react';
import toast from 'react-hot-toast';

const ITEM_TYPES = ['RAW_MATERIAL','FINISHED_GOOD'];
const MR_STATUSES = ['PENDING','APPROVED','FULFILLED','REJECTED'];

export default function InventoryPage() {

  const [tab, setTab] = useState('items');
  const [items, setItems] = useState([]);
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modal, setModal] = useState({ open: false, type: '', data: null });

  const load = async () => {
    setLoading(true);
    try {
      const [it, mr] = await Promise.all([
        inventoryApi.items.getAll(),
        inventoryApi.materialRequests.getAll()
      ]);
      setItems(it.data);
      setRequests(mr.data);
    } catch {
      toast.error('Failed to load inventory');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const deleteItem = async (id) => {
    if (!window.confirm('Delete item?')) return;
    try {
      await inventoryApi.items.delete(id);
      toast.success('Deleted');
      load();
    } catch {
      toast.error('Delete failed');
    }
  };

  const deleteReq = async (id) => {
    if (!window.confirm('Delete request?')) return;
    try {
      await inventoryApi.materialRequests.delete(id);
      toast.success('Deleted');
      load();
    } catch {
      toast.error('Delete failed');
    }
  };

  // ------------------------------
  // ✔ NEW THREE-DOTS ACTION MENU
  // ------------------------------
  function ActionMenu({ row }) {
    const [open, setOpen] = useState(false);

    return (
      <div className="relative">
        <button
          onClick={() => setOpen(!open)}
          className="p-1 rounded hover:bg-slate-200"
        >
          <MoreVertical size={18} />
        </button>

        {open && (
          <div className="absolute right-0 mt-2 w-32 bg-white shadow-lg border rounded-md z-20">

            <button
              className="block w-full text-left px-3 py-2 hover:bg-slate-100"
              onClick={() => {
                setOpen(false);
                setModal({ open: true, type: 'adjustStock', data: row });
              }}
            >
              Adjust
            </button>

            <button
              className="block w-full text-left px-3 py-2 hover:bg-slate-100"
              onClick={() => {
                setOpen(false);
                setModal({ open: true, type: 'editItem', data: row });
              }}
            >
              Edit
            </button>

            <button
              className="block w-full text-left px-3 py-2 text-red-500 hover:bg-red-50"
              onClick={() => {
                setOpen(false);
                deleteItem(row.itemId);
              }}
            >
              Delete
            </button>

          </div>
        )}
      </div>
    );
  }

  // ------------------------------
  // TABLE COLUMNS (ITEMS)
  // ------------------------------
  const itemCols = [
    { key: 'itemId', label: 'S.NO' },
    { key: 'description', label: 'Item' },
    { key: 'itemType', label: 'Type', render: r => r.itemType },
    { key: 'unitOfMeasure', label: 'Unit' },
    { key: 'currentStock', label: 'Stock', render: r => r.currentStock?.toLocaleString() },
    { key: 'reorderLevel', label: 'Reorder At', render: r => r.reorderLevel?.toLocaleString() },
    { key: 'status', label: 'Status', render: r => r.status },

    // ------------------------------
    // ✔ Replace actions with menu
    // ------------------------------
    {
      key: 'actions',
      label: 'Actions',
      render: (row) => <ActionMenu row={row} />
    }
  ];

  // ------------------------------
  // TABLE COLUMNS (REQUESTS)
  // ------------------------------ { key: 'workOrderId', label: 'Work Order' },
  const reqCols = [
    { key: 'requestId', label: 'S.no' },
    { key: 'itemDescription', label: 'Item' },

    { key: 'quantity', label: 'Qty' },
    { key: 'requestedDate', label: 'Date' },
    { key: 'status', label: 'Status', render: r => r.status },
    {
      key: 'actions',
      label: 'Actions',
      render: (r) => (
        <div className="flex gap-2">
          <button onClick={() => setModal({ open: true, type: 'editReq', data: r })} className="text-blue-400 hover:text-blue-300">
            <Pencil size={14} />
          </button>
          <button onClick={() => deleteReq(r.requestId)} className="text-red-400 hover:text-red-300">
            <Trash2 size={14} />
          </button>
        </div>
      )
    }
  ];

  // ------------------------------
  // STOCK STATS
  // ------------------------------
  const lowStock = items.filter(i => i.status === 'LOW_STOCK').length;
  const outOfStock = items.filter(i => i.status === 'OUT_OF_STOCK').length;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Inventory</h1>
          <p className="text-slate-400 text-sm mt-1">Items & material requests</p>
        </div>

        <div className="flex gap-2">
          <button onClick={load} className="btn-secondary flex items-center gap-2">
            <RefreshCw size={14} /> Refresh
          </button>

          {tab === 'items' && (
            <button onClick={() => setModal({ open: true, type: 'newItem', data: null })} className="btn-primary flex items-center gap-2">
              <Plus size={14} /> New Item
            </button>
          )}

          {tab === 'requests' && (
            <button onClick={() => setModal({ open: true, type: 'newReq', data: null })} className="btn-primary flex items-center gap-2">
              <Plus size={14} /> New Request
            </button>
          )}
        </div>
      </div>

      {/* STAT CARDS */}
      <div className="grid grid-cols-4 gap-4 mb-6">
        <StatCard title="Total Items" value={items.length} icon={Package} color="blue" />
        <StatCard title="Available" value={items.filter(i => i.status === 'AVAILABLE').length} icon={Package} color="green" />
        <StatCard title="Low Stock" value={lowStock} icon={TrendingDown} color={lowStock > 0 ? 'yellow' : 'green'} />
        <StatCard title="Out of Stock" value={outOfStock} icon={Package} color={outOfStock > 0 ? 'red' : 'green'} />
      </div>

      {/* MAIN TABLE */}
      <div className="card">
        <div className="flex gap-1 mb-4 border-b border-slate-200 pb-3">
          {['items', 'requests'].map(t => (
            <button
              key={t}
              onClick={() => setTab(t)}
              className={`px-4 py-1.5 rounded-lg text-sm font-medium transition-colors ${
                tab === t ? 'bg-blue-600 text-slate-800' : 'text-slate-400 hover:text-slate-700'
              }`}
            >
              {t === 'requests' ? 'Material Requests' : t.charAt(0).toUpperCase() + t.slice(1)}
            </button>
          ))}
        </div>

        {tab === 'items' && <Table columns={itemCols} data={items} loading={loading} />}
        {tab === 'requests' && <Table columns={reqCols} data={requests} loading={loading} />}
      </div>

      {/* MODALS */}
      {['newItem', 'editItem'].includes(modal.type) && (
        <ItemModal isOpen={modal.open} onClose={() => { setModal({ open:false, type:'', data:null }); load(); }} item={modal.data} />
      )}

      {modal.type === 'adjustStock' && (
        <AdjustModal isOpen={modal.open} onClose={() => { setModal({ open:false, type:'', data:null }); load(); }} item={modal.data} />
      )}

      {['newReq', 'editReq'].includes(modal.type) && (
        <RequestModal
          isOpen={modal.open}
          onClose={() => { setModal({ open:false, type:'', data:null }); load(); }}
          req={modal.data}
          items={items}
        />
      )}
    </div>
  );
}




/* ------------------------------
  ITEM MODAL
------------------------------ */
function ItemModal({ isOpen, onClose, item }) {
  const [form, setForm] = useState({
    description: '',

    itemType: 'RAW_MATERIAL',
    unitOfMeasure: 'KG',
    currentStock: 0,
    reorderLevel: 100,
    warehouseId: 1,
    ...item
  });

  const [saving, setSaving] = useState(false);
  const setField = (k, v) => setForm(f => ({ ...f, [k]: v }));

  const save = async () => {
    setSaving(true);
    try {
      if (item?.itemId) await inventoryApi.items.update(item.itemId, form);
      else await inventoryApi.items.create(form);
      toast.success(item ? 'Updated' : 'Created');
      onClose();
    } catch (e) {
      toast.error(e.response?.data?.message || 'Save failed');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={item ? 'Edit Item' : 'New Inventory Item'}>
      <div className="space-y-3">
        <div>
          <label className="label">Description</label>
          <input className="input" value={form.description} onChange={e => setField('description', e.target.value)} />
        </div>

        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="label">Type</label>
            <select className="input" value={form.itemType} onChange={e => setField('itemType', e.target.value)}>
              {ITEM_TYPES.map(t => <option key={t}>{t}</option>)}
            </select>
          </div>

          <div>
            <label className="label">Unit of Measure</label>
            <input className="input" value={form.unitOfMeasure} onChange={e => setField('unitOfMeasure', e.target.value)} />
          </div>
        </div>

        <div className="grid grid-cols-3 gap-3">
          <div>
            <label className="label">Current Stock</label>
            <input type="number" className="input" value={form.currentStock} onChange={e => setField('currentStock', e.target.value)} />
          </div>
          <div>
            <label className="label">Reorder Level</label>
            <input type="number" className="input" value={form.reorderLevel} onChange={e => setField('reorderLevel', e.target.value)} />
          </div>
          <div>
            <label className="label">Warehouse ID</label>
            <input type="number" className="input" value={form.warehouseId} onChange={e => setField('warehouseId', e.target.value)} />
          </div>
        </div>

        <div className="flex gap-2 justify-end pt-2">
          <button className="btn-secondary" onClick={onClose}>Cancel</button>
          <button className="btn-primary" onClick={save} disabled={saving}>
            {saving ? 'Saving...' : 'Save'}
          </button>
        </div>
      </div>
    </Modal>
  );
}




/* ------------------------------
  ADJUST STOCK MODAL
------------------------------ */
function AdjustModal({ isOpen, onClose, item }) {
  const [qty, setQty] = useState(0);
  const [reason, setReason] = useState('Manual adjustment');
  const [saving, setSaving] = useState(false);

  const save = async () => {
    setSaving(true);
    try {
      await inventoryApi.items.adjustStock(item.itemId, { quantity: Number(qty), reason });
      toast.success('Stock adjusted');
      onClose();
    } catch (e) {
      toast.error(e.response?.data?.message || 'Failed');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`Adjust Stock: ${item?.description}`}>
      <div className="space-y-3">
        <p className="text-sm text-slate-400">
          Current stock: <span className="text-slate-800 font-medium">{item?.currentStock} {item?.unitOfMeasure}</span>
        </p>

        <div>
          <label className="label">Adjustment Quantity (use - to decrease)</label>
          <input type="number" className="input" value={qty} onChange={e => setQty(e.target.value)} />
        </div>

        <div>
          <label className="label">Reason</label>
          <input className="input" value={reason} onChange={e => setReason(e.target.value)} />
        </div>

        <div className="flex gap-2 justify-end pt-2">
          <button className="btn-secondary" onClick={onClose}>Cancel</button>
          <button className="btn-primary" onClick={save} disabled={saving}>
            {saving ? 'Saving...' : 'Apply'}
          </button>
        </div>
      </div>
    </Modal>
  );
}




/* ------------------------------
  MATERIAL REQUEST MODAL
------------------------------ */
function RequestModal({ isOpen, onClose, req, items }) {
  const [form, setForm] = useState({
    itemId: '',
    workOrderId: '',
    quantity: 1,
    requestedDate: new Date().toISOString().slice(0, 10),
    status: 'PENDING',
    ...req,
    itemId: req?.itemId || ''
  });

  const [saving, setSaving] = useState(false);
  const setField = (k, v) => setForm(f => ({ ...f, [k]: v }));

  const save = async () => {
    setSaving(true);
    try {
      const payload = {
        ...form,
        itemId: Number(form.itemId),
        quantity: Number(form.quantity)
      };

      if (req?.requestId) await inventoryApi.materialRequests.update(req.requestId, payload);
      else await inventoryApi.materialRequests.create(payload);

      toast.success(req ? 'Updated' : 'Created');
      onClose();
    } catch (e) {
      toast.error(e.response?.data?.message || 'Save failed');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={req ? 'Edit Request' : 'New Material Request'}>
      <div className="space-y-3">

        <div>
          <label className="label">Item</label>
          <select className="input" value={form.itemId} onChange={e => setField('itemId', e.target.value)}>
            <option value="">Select item</option>
            {items.map(i => (
              <option key={i.itemId} value={i.itemId}>{i.description}</option>
            ))}
          </select>
        </div>

        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="label">Work Order ID</label>
            <input type="number" className="input" value={form.workOrderId}
              onChange={e => setField('workOrderId', e.target.value)} />
          </div>

          <div>
            <label className="label">Quantity</label>
            <input type="number" className="input" value={form.quantity}
              onChange={e => setField('quantity', e.target.value)} />
          </div>
        </div>

        <div>
          <label className="label">Status</label>
          <select className="input" value={form.status} onChange={e => setField('status', e.target.value)}>
            {MR_STATUSES.map(s => <option key={s}>{s}</option>)}
          </select>
        </div>

        <div className="flex gap-2 justify-end pt-2">
          <button className="btn-secondary" onClick={onClose}>Cancel</button>
          <button className="btn-primary" onClick={save} disabled={saving}>
            {saving ? 'Saving...' : 'Save'}
          </button>
        </div>

      </div>
    </Modal>
  );
}