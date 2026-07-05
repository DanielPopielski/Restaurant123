const BASE = '/api/v1'

async function request(path, options = {}) {
  const saved = localStorage.getItem('auth')
  const token = saved ? JSON.parse(saved).token : null

  const res = await fetch(BASE + path, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  })

  if (res.status === 204) return null

  let body = null
  try {
    body = await res.json()
  } catch {
    /* pusta odpowiedz */
  }

  if (!res.ok) {
    const message = body?.message || `Błąd ${res.status}`
    const error = new Error(message)
    error.status = res.status
    throw error
  }
  return body
}

export const api = {
  register: (data) => request('/auth/register', { method: 'POST', body: JSON.stringify(data) }),
  authenticate: (data) => request('/auth/authenticate', { method: 'POST', body: JSON.stringify(data) }),

  getDishes: () => request('/dishes'),
  createDish: (data) => request('/dishes', { method: 'POST', body: JSON.stringify(data) }),
  updateDish: (id, data) => request(`/dishes/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  deleteDish: (id) => request(`/dishes/${id}`, { method: 'DELETE' }),

  getTables: () => request('/tables'),
  createTable: (data) => request('/tables', { method: 'POST', body: JSON.stringify(data) }),
  deleteTable: (id) => request(`/tables/${id}`, { method: 'DELETE' }),

  createOrder: (data) => request('/orders', { method: 'POST', body: JSON.stringify(data) }),
  getMyOrders: () => request('/orders/my'),
  getAllOrders: () => request('/orders'),
  updateOrderStatus: (id, status) =>
    request(`/orders/${id}/status`, { method: 'PATCH', body: JSON.stringify({ status }) }),
}
