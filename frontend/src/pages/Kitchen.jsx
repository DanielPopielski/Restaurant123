import { useEffect, useState } from 'react'
import { Client } from '@stomp/stompjs'
import { api } from '../api'
import Toast from '../components/Toast'

export default function Kitchen() {
  const [connected, setConnected] = useState(false)
  const [orders, setOrders] = useState([])
  const [tables, setTables] = useState({})
  const [toast, setToast] = useState(null)

  // on mount: load active orders (NEW / IN_PROGRESS) from the API
  useEffect(() => {
    api.getTables()
      .then((list) => setTables(Object.fromEntries(list.map((t) => [t.id, t.tableNumber]))))
      .catch(() => {})
    api.getAllOrders()
      .then((all) => setOrders(
        all
          .filter((o) => o.status === 'NEW' || o.status === 'IN_PROGRESS')
          .map((o) => ({
            id: o.id,
            tableId: o.tableId,
            status: o.status,
            createdAt: o.createdAt,
            items: o.items.map((i) => ({ dishName: i.dishName, quantity: i.quantity })),
          }))
          .reverse(),
      ))
      .catch(() => setToast({ type: 'error', message: 'Nie udało się pobrać zamówień' }))
  }, [])

  // live: new orders over WebSocket (Kafka -> STOMP)
  useEffect(() => {
    const proto = window.location.protocol === 'https:' ? 'wss' : 'ws'
    const client = new Client({
      brokerURL: `${proto}://${window.location.host}/ws`,
      reconnectDelay: 5000,
      onConnect: () => {
        setConnected(true)
        client.subscribe('/topic/kitchen', (msg) => {
          const n = JSON.parse(msg.body)
          setOrders((prev) => prev.some((o) => o.id === n.orderId) ? prev : [{
            id: n.orderId,
            tableNumber: n.tableNumber,
            status: n.status,
            createdAt: n.createdAt,
            items: n.items,
          }, ...prev])
        })
      },
      onWebSocketClose: () => setConnected(false),
    })
    client.activate()
    return () => client.deactivate()
  }, [])

  const changeStatus = async (id, status) => {
    try {
      await api.updateOrderStatus(id, status)
      if (status === 'READY') {
        setOrders((prev) => prev.filter((o) => o.id !== id))
        setToast({ type: 'success', message: `Zamówienie #${id} gotowe do wydania ✅` })
      } else {
        setOrders((prev) => prev.map((o) => (o.id === id ? { ...o, status } : o)))
      }
    } catch (err) {
      setToast({ type: 'error', message: err.message })
    }
  }

  const tableLabel = (order) =>
    order.tableNumber ?? tables[order.tableId] ?? order.tableId

  return (
    <div>
      <div className="kitchen-head">
        <h1>Ekran kuchni</h1>
        <span className={`conn-dot ${connected ? 'on' : 'off'}`}>
          {connected ? 'na żywo' : 'łączenie...'}
        </span>
      </div>
      {orders.length === 0 && (
        <p className="muted">Brak aktywnych zamówień. Nowe pojawią się tu same.</p>
      )}
      <div className="orders-list">
        {orders.map((order) => (
          <div key={order.id}
               className={`card order-card kitchen-order ${order.status === 'IN_PROGRESS' ? 'in-progress' : ''}`}>
            <div className="order-head">
              <strong>#{order.id} — stolik {tableLabel(order)}</strong>
              <span className="muted">{new Date(order.createdAt).toLocaleTimeString('pl-PL')}</span>
            </div>
            <ul className="order-items">
              {order.items.map((item, i) => (
                <li key={i}><span>{item.quantity} × {item.dishName}</span></li>
              ))}
            </ul>
            <div className="kitchen-actions">
              {order.status === 'NEW' ? (
                <button className="btn btn-primary" onClick={() => changeStatus(order.id, 'IN_PROGRESS')}>
                  🍳 Zaczynam gotować
                </button>
              ) : (
                <button className="btn btn-ready" onClick={() => changeStatus(order.id, 'READY')}>
                  ✅ Gotowe do wydania
                </button>
              )}
            </div>
          </div>
        ))}
      </div>
      <Toast toast={toast} onClose={() => setToast(null)} />
    </div>
  )
}
