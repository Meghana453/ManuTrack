import React from 'react';

export default function StatCard({ title, value, subtitle, icon: Icon, color = 'blue' }) {
  const colors = {
    blue:   { wrap: 'bg-blue-50 border-blue-100',   icon: 'bg-blue-100 text-blue-600',   val: 'text-blue-700' },
    green:  { wrap: 'bg-green-50 border-green-100',  icon: 'bg-green-100 text-green-600',  val: 'text-green-700' },
    yellow: { wrap: 'bg-amber-50 border-amber-100',  icon: 'bg-amber-100 text-amber-600',  val: 'text-amber-700' },
    red:    { wrap: 'bg-red-50 border-red-100',      icon: 'bg-red-100 text-red-600',      val: 'text-red-700' },
    purple: { wrap: 'bg-purple-50 border-purple-100',icon: 'bg-purple-100 text-purple-600',val: 'text-purple-700' },
    orange: { wrap: 'bg-orange-50 border-orange-100',icon: 'bg-orange-100 text-orange-600',val: 'text-orange-700' },
  };
  const c = colors[color] || colors.blue;
  return (
    <div className={`rounded-xl border p-5 flex items-start gap-4 shadow-sm ${c.wrap}`}>
      {Icon && (
        <div className={`p-2.5 rounded-lg flex-shrink-0 ${c.icon}`}>
          <Icon size={20} />
        </div>
      )}
      <div className="flex-1 min-w-0">
        <p className="text-slate-500 text-sm font-medium">{title}</p>
        <p className={`text-2xl font-bold mt-0.5 ${c.val}`}>{value}</p>
        {subtitle && <p className="text-xs text-slate-400 mt-1">{subtitle}</p>}
      </div>
    </div>
  );
}
