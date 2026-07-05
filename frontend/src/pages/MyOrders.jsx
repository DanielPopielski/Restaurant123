import { useEffect, useState } from 'react'
import { api } from '../api'
import { STATUS_LABELS } from '../statusUtils'

export default function MyOrders() {
  const [orders, setOrders] = useState([])
  const [error, setError] = useState(null)

  useEffect(() => {
    const load = () => api.getMyOrders().then(setOrders).catch((e) => setError(e.message))
    load()
    const interval = setInterval(load, 10000)
    return () => clearInterval(interval)
  }, [])

  return (
    <div>
      <h1>Moje zamówienia</h1>
      {error && <div className="form-error">{error}</div>}
      {orders.length === 0 && !error && <p className="muted">Nie masz jeszcze żadnych zamówień.</p>}
      <div className="orders-list">
        {orders.map((order) => (
          <div key={order.id} className="card order-card">
            <div className="order-head">
              <strong>Zamówienie #{order.id}</strong>
              <span className={`badge badge-${order.status.toLowerCase()}`}>
                {STATUS_LABELS[order.status] || order.status}
              </span>
            </div>
            <ul className="order-items">
              {order.items.map((item, i) => (
                <li key={i}>
                  <span>{item.quantity} × {item.dishName}</span>
                  <span>{(item.price * item.quantity).toFixed(2)} zł</span>
                </li>
              ))}
            </ul>
            <div className="order-foot">
              <span className="muted">{new Date(order.createdAt).toLocaleString('pl-PL')}</span>
              <strong>{Number(order.totalPrice).toFixed(2)} zł</strong>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
