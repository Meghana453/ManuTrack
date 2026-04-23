import axios from 'axios';

const BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:7071/api';

const api = axios.create({ baseURL: BASE_URL });

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

// Auth
export const authApi = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
};

// Users
export const userApi = {
  getAll: () => api.get('/users'),
  getById: (id) => api.get(`/users/${id}`),
  update: (id, data) => api.put(`/users/${id}`, data),
  delete: (id) => api.delete(`/users/${id}`),
  getAuditLogs: () => api.get('/audit-logs'),
};

// Production
export const productionApi = {
  plans: {
    getAll: () => api.get('/production/plans'),
    getById: (id) => api.get(`/production/plans/${id}`),
    create: (data) => api.post('/production/plans', data),
    update: (id, data) => api.put(`/production/plans/${id}`, data),
    delete: (id) => api.delete(`/production/plans/${id}`),
  },
  machines: {
    getAll: () => api.get('/production/machines'),
    getById: (id) => api.get(`/production/machines/${id}`),
    create: (data) => api.post('/production/machines', data),
    update: (id, data) => api.put(`/production/machines/${id}`, data),
    delete: (id) => api.delete(`/production/machines/${id}`),
  },
  workOrders: {
    getAll: () => api.get('/production/work-orders'),
    getById: (id) => api.get(`/production/work-orders/${id}`),
    create: (data) => api.post('/production/work-orders', data),
    update: (id, data) => api.put(`/production/work-orders/${id}`, data),
    delete: (id) => api.delete(`/production/work-orders/${id}`),
    getByPlan: (planId) => api.get(`/production/plans/${planId}/work-orders`),
  },
};

// Inventory
export const inventoryApi = {
  items: {
    getAll: () => api.get('/inventory/items'),
    getById: (id) => api.get(`/inventory/items/${id}`),
    create: (data) => api.post('/inventory/items', data),
    update: (id, data) => api.put(`/inventory/items/${id}`, data),
    adjustStock: (id, data) => api.patch(`/inventory/items/${id}/adjust-stock`, data),
    delete: (id) => api.delete(`/inventory/items/${id}`),
    getLowStock: () => api.get('/inventory/items/low-stock'),
    getOutOfStock: () => api.get('/inventory/items/out-of-stock'),
  },
  materialRequests: {
    getAll: () => api.get('/inventory/material-requests'),
    getById: (id) => api.get(`/inventory/material-requests/${id}`),
    create: (data) => api.post('/inventory/material-requests', data),
    update: (id, data) => api.put(`/inventory/material-requests/${id}`, data),
    delete: (id) => api.delete(`/inventory/material-requests/${id}`),
  },
};

// Procurement
export const procurementApi = {
  vendors: {
    getAll: () => api.get('/procurement/vendors'),
    getById: (id) => api.get(`/procurement/vendors/${id}`),
    create: (data) => api.post('/procurement/vendors', data),
    update: (id, data) => api.put(`/procurement/vendors/${id}`, data),
    delete: (id) => api.delete(`/procurement/vendors/${id}`),
  },
  purchaseOrders: {
    getAll: () => api.get('/procurement/purchase-orders'),
    getById: (id) => api.get(`/procurement/purchase-orders/${id}`),
    create: (data) => api.post('/procurement/purchase-orders', data),
    update: (id, data) => api.put(`/procurement/purchase-orders/${id}`, data),
    delete: (id) => api.delete(`/procurement/purchase-orders/${id}`),
  },
  invoices: {
    getAll: () => api.get('/procurement/invoices'),
    getById: (id) => api.get(`/procurement/invoices/${id}`),
    create: (data) => api.post('/procurement/invoices', data),
    update: (id, data) => api.put(`/procurement/invoices/${id}`, data),
    delete: (id) => api.delete(`/procurement/invoices/${id}`),
  },
};

// Logistics
export const logisticsApi = {
  carriers: {
    getAll: () => api.get('/logistics/carriers'),
    create: (data) => api.post('/logistics/carriers', data),
    update: (id, data) => api.put(`/logistics/carriers/${id}`, data),
    delete: (id) => api.delete(`/logistics/carriers/${id}`),
  },
  routes: {
    getAll: () => api.get('/logistics/routes'),
    create: (data) => api.post('/logistics/routes', data),
    update: (id, data) => api.put(`/logistics/routes/${id}`, data),
    delete: (id) => api.delete(`/logistics/routes/${id}`),
  },
  shipments: {
    getAll: () => api.get('/logistics/shipments'),
    getById: (id) => api.get(`/logistics/shipments/${id}`),
    create: (data) => api.post('/logistics/shipments', data),
    update: (id, data) => api.put(`/logistics/shipments/${id}`, data),
    delete: (id) => api.delete(`/logistics/shipments/${id}`),
  },
};

// Analytics
export const analyticsApi = {
  getDashboard: () => api.get('/analytics/dashboard'),
  getProduction: () => api.get('/analytics/production'),
  getInventory: () => api.get('/analytics/inventory'),
  getProcurement: () => api.get('/analytics/procurement'),
  getLogistics: () => api.get('/analytics/logistics'),
  generateReport: (scope) => api.post(`/analytics/reports/generate?scope=${scope}`),
  getAllReports: () => api.get('/analytics/reports'),
};

// Notifications
export const notificationApi = {
  getAll: () => api.get('/notifications'),
  getByUser: (userId) => api.get(`/notifications/user/${userId}`),
  getUnread: (userId) => api.get(`/notifications/user/${userId}/unread`),
  countUnread: (userId) => api.get(`/notifications/user/${userId}/unread-count`),
  markRead: (id) => api.patch(`/notifications/${id}/read`),
  dismiss: (id) => api.patch(`/notifications/${id}/dismiss`),
  markAllRead: (userId) => api.patch(`/notifications/user/${userId}/mark-all-read`),
  create: (data) => api.post('/notifications', data),
  delete: (id) => api.delete(`/notifications/${id}`),
};

export default api;

// Password Reset
export const passwordResetApi = {
  forgotPassword: (email) => api.post('/auth/forgot-password', { email }),
  validateToken: (token) => api.get(`/auth/validate-reset-token?token=${token}`),
  resetPassword: (data) => api.post('/auth/reset-password', data),
};
