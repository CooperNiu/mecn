import axios from 'axios'

const API_BASE_URL = '/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Network Analysis API
export const networkApi = {
  buildNetwork: (data: unknown) => api.post('/network/build', data),
  getNetwork: (id: string) => api.get(`/network/${id}`),
  getSystemicImportance: () => api.get('/network/systemic-importance'),
}

// Causal Discovery API
export const causalApi = {
  discover: (data: unknown) => api.post('/causal/discover', data),
  getMethods: () => api.get('/causal/methods'),
}

// Ripple Simulation API
export const rippleApi = {
  simulate: (data: unknown) => api.post('/ripple/simulate', data),
}

// Data Source API
export const dataApi = {
  getSources: () => api.get('/data/sources'),
  getIndicators: (source: string) => api.get(`/data/${source}/indicators`),
  refreshData: (source: string) => api.post(`/data/${source}/refresh`),
}

// Health Check
export const healthApi = {
  check: () => api.get('/health'),
}

export default api
