import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});


apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export const authService = {
  login: (email, password) =>
    apiClient.post('/auth/login', { email, password }),
  register: (name, email, password, address) =>
    apiClient.post('/auth/register', { name, email, password, address }),
};

export const storeService = {
  getAll: (params = {}) => apiClient.get('/stores', { params }),
  getById: (id) => apiClient.get(`/stores/${id}`),
  create: (store) => apiClient.post('/stores', store),
  update: (id, store) => apiClient.put(`/stores/${id}`, store),
  delete: (id) => apiClient.delete(`/stores/${id}`),
};

export const userService = {
  getAll: (params = {}) => apiClient.get('/users', { params }),
  create: (user) => apiClient.post('/users', user),
  updatePassword: (currentPassword, newPassword) =>
    apiClient.put('/users/me/password', { currentPassword, newPassword }),
};

export const adminService = {
  dashboard: () => apiClient.get('/admin/dashboard'),
  getUsers: (params = {}) => apiClient.get('/admin/users', { params }),
  getUser: (id) => apiClient.get(`/admin/users/${id}`),
  createUser: (user) => apiClient.post('/admin/users', user),
  getStores: (params = {}) => apiClient.get('/admin/stores', { params }),
  createStore: (store) => apiClient.post('/admin/stores', store),
};

export const ownerService = {
  dashboard: () => apiClient.get('/owner/dashboard'),
  getRatings: () => apiClient.get('/owner/ratings'),
};

export const dashboardService = {
  admin: () => apiClient.get('/admin/dashboard'),
  owner: () => apiClient.get('/owner/dashboard'),
};

export const ratingService = {
  getByStore: (storeId) => apiClient.get(`/ratings/store/${storeId}`),
  getById: (id) => apiClient.get(`/ratings/${id}`),
  create: (rating) => apiClient.post('/ratings', rating),
  update: (id, rating) => apiClient.put(`/ratings/${id}`, rating),
  delete: (id) => apiClient.delete(`/ratings/${id}`),
};

export default apiClient;
