// src/services/api.js
import axios from 'axios';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to include JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add response interceptor to handle errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('authToken');
      localStorage.removeItem('username');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
};

// Clients API
export const clientsAPI = {
  getAll: (page = 0, size = 10) => api.get(`/clients?page=${page}&size=${size}`),
  getById: (id) => api.get(`/clients/${id}`),
  create: (data) => api.post('/clients', data),
  update: (data) => api.put('/clients', data),
  delete: (id) => api.delete(`/clients/${id}`),
};

// Drivers API
export const driversAPI = {
  getAll: (page = 0, size = 10) => api.get(`/drivers?page=${page}&size=${size}`),
  getById: (id) => api.get(`/drivers/${id}`),
  create: (data) => api.post('/drivers', data),
  update: (data) => api.put('/drivers', data),
  delete: (id) => api.delete(`/drivers/${id}`),
};

// Transport Logs API
export const transportLogsAPI = {
  getAll: (page = 0, size = 10) => api.get(`/trans_logs?page=${page}&size=${size}`),
  getById: (id) => api.get(`/trans_logs/${id}`),
  create: (data) => api.post('/trans_logs', data),
  update: (data) => api.put('/trans_logs', data),
  delete: (id) => api.delete(`/trans_logs/${id}`),
};

export default api;