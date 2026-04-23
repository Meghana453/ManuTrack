import React, { useEffect, useState } from 'react';
import { notificationApi } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import Badge from '../../components/common/Badge';
import { Bell, CheckCheck, Trash2, RefreshCw } from 'lucide-react';
import toast from 'react-hot-toast';

const CATEGORY_ICONS = {
  INVENTORY: '📦', SHIPMENT: '🚚', MACHINE: '⚙️',
  WORK_ORDER: '🔧', PROCUREMENT: '🛒', GENERAL: '📢'
};

const CATEGORY_COLORS = {
  INVENTORY:   'bg-green-50 border-green-200',
  SHIPMENT:    'bg-orange-50 border-orange-200',
  MACHINE:     'bg-cyan-50 border-cyan-200',
  WORK_ORDER:  'bg-blue-50 border-blue-200',
  PROCUREMENT: 'bg-amber-50 border-amber-200',
  GENERAL:     'bg-slate-50 border-slate-200',
};

export default function NotificationsPage() {
  const { user } = useAuth();
  const [notifs, setNotifs]   = useState([]);
  const [filter, setFilter]   = useState('all');
  const [loading, setLoading] = useState(false);

  const load = async () => {
    setLoading(true);
    try { const r = await notificationApi.getByUser(user.userId); setNotifs(r.data); }
    catch { toast.error('Failed to load notifications'); }
    finally { setLoading(false); }
  };

  useEffect(() => { if (user?.userId) load(); }, [user]);

  const markRead  = async (id) => { try { await notificationApi.markRead(id);  load(); } catch { toast.error('Failed'); } };
  const dismiss   = async (id) => { try { await notificationApi.dismiss(id);   load(); } catch { toast.error('Failed'); } };
  const del       = async (id) => { try { await notificationApi.delete(id);    load(); } catch { toast.error('Failed'); } };
  const markAll   = async ()   => { try { await notificationApi.markAllRead(user.userId); toast.success('All marked read'); load(); } catch { toast.error('Failed'); } };

  const filtered = filter === 'all' ? notifs : notifs.filter(n => n.status === filter.toUpperCase());
  const unread = notifs.filter(n => n.status === 'UNREAD').length;

  const filters = [
    { key: 'all',       label: 'All',       count: notifs.length },
    { key: 'unread',    label: 'Unread',    count: unread },
    { key: 'read',      label: 'Read',      count: notifs.filter(n => n.status === 'READ').length },
    { key: 'dismissed', label: 'Dismissed', count: notifs.filter(n => n.status === 'DISMISSED').length },
  ];

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-800 flex items-center gap-2">
            Notifications
            {unread > 0 && (
              <span className="bg-red-500 text-white text-xs rounded-full px-2 py-0.5 font-semibold">{unread}</span>
            )}
          </h1>
          <p className="text-slate-500 text-sm mt-0.5">Your alerts and system notifications</p>
        </div>
        <div className="flex gap-2">
          <button onClick={load} className="btn-secondary flex items-center gap-2 text-sm">
            <RefreshCw size={14} /> Refresh
          </button>
          {unread > 0 && (
            <button onClick={markAll} className="btn-secondary flex items-center gap-2 text-sm">
              <CheckCheck size={14} /> Mark All Read
            </button>
          )}
        </div>
      </div>

      {/* Filter pills */}
      <div className="flex gap-2 mb-5">
        {filters.map(f => (
          <button key={f.key} onClick={() => setFilter(f.key)}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-sm font-medium transition-all border
              ${filter === f.key
                ? 'bg-blue-600 text-white border-blue-600 shadow-sm'
                : 'bg-white text-slate-600 border-slate-200 hover:border-slate-300 hover:bg-slate-50'}`}>
            {f.label}
            {f.count > 0 && (
              <span className={`text-xs px-1.5 py-0.5 rounded-full ${filter === f.key ? 'bg-blue-500 text-white' : 'bg-slate-100 text-slate-500'}`}>
                {f.count}
              </span>
            )}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="flex items-center justify-center py-16">
          <div className="w-6 h-6 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
        </div>
      ) : filtered.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-200 text-center py-16 shadow-sm">
          <Bell size={40} className="text-slate-300 mx-auto mb-3" />
          <p className="text-slate-500 font-medium">No notifications</p>
          <p className="text-slate-400 text-sm mt-1">You're all caught up!</p>
        </div>
      ) : (
        <div className="flex flex-col gap-2">
          {filtered.map(n => (
            <div key={n.notificationId}
              className={`bg-white rounded-xl border shadow-sm flex items-start gap-4 p-4 transition-all
                ${n.status === 'UNREAD' ? `${CATEGORY_COLORS[n.category] || 'bg-blue-50 border-blue-200'} shadow-md` : 'border-slate-100'}`}>
              <div className={`w-10 h-10 rounded-xl flex items-center justify-center text-lg flex-shrink-0
                ${CATEGORY_COLORS[n.category]?.split(' ')[0] || 'bg-slate-50'}`}>
                {CATEGORY_ICONS[n.category] || '📢'}
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 mb-1 flex-wrap">
                  <Badge status={n.status} />
                  <span className="text-xs text-slate-400 font-medium">{n.category?.replace(/_/g, ' ')}</span>
                  <span className="text-xs text-slate-300 ml-auto">{n.createdDate?.replace('T', ' ').slice(0, 16)}</span>
                </div>
                <p className="text-sm text-slate-700 leading-relaxed">{n.message}</p>
              </div>
              <div className="flex gap-1.5 flex-shrink-0">
                {n.status === 'UNREAD' && (
                  <button onClick={() => markRead(n.notificationId)}
                    className="text-xs bg-blue-50 hover:bg-blue-100 text-blue-600 border border-blue-200 px-2.5 py-1 rounded-lg font-medium transition-colors">
                    Mark Read
                  </button>
                )}
                {n.status !== 'DISMISSED' && (
                  <button onClick={() => dismiss(n.notificationId)}
                    className="text-xs bg-slate-50 hover:bg-slate-100 text-slate-500 border border-slate-200 px-2.5 py-1 rounded-lg font-medium transition-colors">
                    Dismiss
                  </button>
                )}
                <button onClick={() => del(n.notificationId)}
                  className="text-red-400 hover:text-red-600 hover:bg-red-50 p-1.5 rounded-lg transition-colors">
                  <Trash2 size={14} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
