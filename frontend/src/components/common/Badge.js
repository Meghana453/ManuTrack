import React from 'react';

const colorMap = {
  PLANNED:      'bg-blue-100 text-blue-700 ring-1 ring-blue-200',
  IN_PROGRESS:  'bg-amber-100 text-amber-700 ring-1 ring-amber-200',
  COMPLETED:    'bg-green-100 text-green-700 ring-1 ring-green-200',
  CANCELLED:    'bg-slate-100 text-slate-500 ring-1 ring-slate-200',
  HALTED:       'bg-red-100 text-red-700 ring-1 ring-red-200',
  PENDING:      'bg-amber-100 text-amber-700 ring-1 ring-amber-200',
  ACTIVE:       'bg-green-100 text-green-700 ring-1 ring-green-200',
  INACTIVE:     'bg-slate-100 text-slate-500 ring-1 ring-slate-200',
  MAINTENANCE:  'bg-orange-100 text-orange-700 ring-1 ring-orange-200',
  IDLE:         'bg-slate-100 text-slate-500 ring-1 ring-slate-200',
  AVAILABLE:    'bg-green-100 text-green-700 ring-1 ring-green-200',
  LOW_STOCK:    'bg-amber-100 text-amber-700 ring-1 ring-amber-200',
  OUT_OF_STOCK: 'bg-red-100 text-red-700 ring-1 ring-red-200',
  OPEN:         'bg-blue-100 text-blue-700 ring-1 ring-blue-200',
  DELIVERED:    'bg-green-100 text-green-700 ring-1 ring-green-200',
  PAID:         'bg-green-100 text-green-700 ring-1 ring-green-200',
  OVERDUE:      'bg-red-100 text-red-700 ring-1 ring-red-200',
  SCHEDULED:    'bg-blue-100 text-blue-700 ring-1 ring-blue-200',
  IN_TRANSIT:   'bg-amber-100 text-amber-700 ring-1 ring-amber-200',
  DELAYED:      'bg-red-100 text-red-700 ring-1 ring-red-200',
  FULFILLED:    'bg-green-100 text-green-700 ring-1 ring-green-200',
  APPROVED:     'bg-blue-100 text-blue-700 ring-1 ring-blue-200',
  REJECTED:     'bg-red-100 text-red-700 ring-1 ring-red-200',
  UNREAD:       'bg-blue-100 text-blue-700 ring-1 ring-blue-200',
  READ:         'bg-slate-100 text-slate-500 ring-1 ring-slate-200',
  DISMISSED:    'bg-slate-100 text-slate-400 ring-1 ring-slate-200',
  RAW_MATERIAL: 'bg-purple-100 text-purple-700 ring-1 ring-purple-200',
  FINISHED_GOOD:'bg-cyan-100 text-cyan-700 ring-1 ring-cyan-200',
};

export default function Badge({ status }) {
  const cls = colorMap[status] || 'bg-slate-100 text-slate-500 ring-1 ring-slate-200';
  const label = status?.replace(/_/g, ' ');
  return <span className={`badge ${cls}`}>{label}</span>;
}
