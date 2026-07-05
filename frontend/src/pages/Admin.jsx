import { useEffect, useState } from 'react'
import { api } from '../api'
import Toast from '../components/Toast'
import { STATUS_LABELS, STATUS_ORDER } from '../statusUtils'

export default function Admin() {
  const [tab, setTab] = useState('dishes')
  const [toast, setToast] = useState(null)

  return (
    <div>
      <h1>Panel admina</h1>
      <div className="tabs">
        <button className={tab === 'dishes' ? 'tab active' : 'tab'} onClick={() => setTab('dishes')}>Dania</button>
        <button className={tab === 'tables' ? 'tab active' : 'tab'} onClick={() => setTab('tables')}>Stoliki</button>
        <button className={tab === 'orders' ? 'tab active' : 'tab'} onClick={() => setTab('orders')}>Zamówienia</button>
      </div>
      {tab === 'dishes' && <DishesTab setToast={setToast} />}
      {tab === 'tables' && <TablesTab setToast={setToast} />}
      {tab === 'orders' && <OrdersTab setToast={setToast} />}
      <Toast toast={toast} onClose={() => setToast(null)} />
    </div>
  )
}

function DishesTab({ setToast }) {
  const [dishes, setDishes] = useState([])
  const [name, setName] = useState('')
  const [price, setPrice] = useState('')

  const load = () => api.getDishes().then(setDishes).catch(() => {})
  useEffect(() => { load() }, [])

  const add = async (e) => {
    e.preventDefault()
    try {
      await api.createDish({ name, price: Number(price) })
      setName(''); setPrice('')
      setToast({ type: 'success', message: 'Danie dodane' })
      load()
    } catch (err) {
      setToast({ type: 'error', message: err.message })
    }
  }

  const remove = async (id) => {
    try {
      await api.deleteDish(id)
      setToast({ type: 'success', message: 'Danie usunięte' })
      load()
    } catch (err) {
      setToast({ type: 'error', message: err.message })
    }
  }

  return (
    <div className="admin-grid">
      <form onSubmit={add} className="card form">
        <h3>Nowe danie</h3>
        <label>Nazwa
          <input value={name} onChange={(e) => setName(e.target.value)} placeholder="np. Pierogi ruskie" required />
        </label>
        <label>Cena (zł)
          <input type="number" step="0.01" min="0.01" value={price}
                 onChange={(e) => setPrice(e.target.value)} placeholder="24.50" required />
        </label>
        <button className="btn btn-primary">Dodaj danie</button>
      </form>

      <div className="card">
        <h3>Menu ({dishes.length})</h3>
        <ul className="admin-list">
          {dishes.map((dish) => (
            <li key={dish.id}>
              <span>{dish.name} — <strong>{Number(dish.price).toFixed(2)} zł</strong></span>
              <button className="btn btn-danger" onClick={() => remove(dish.id)}>Usuń</button>
            </li>
          ))}
        </ul>
      </div>
    </div>
  )
}

function TablesTab({ setToast }) {
  const [tables, setTables] = useState([])
  const [tableNumber, setTableNumber] = useState('')
  const [seats, setSeats] = useState('')

  const load = () => api.getTables().then(setTables).catch(() => {})
  useEffect(() => { load() }, [])

  const add = async (e) => {
    e.preventDefault()
    try {
      await api.createTable({ tableNumber: Number(tableNumber), seats: Number(seats) })
      setTableNumber(''); setSeats('')
      setToast({ type: 'success', message: 'Stolik dodany' })
      load()
    } catch (err) {
      setToast({ type: 'error', message: err.message })
    }
  }

  const remove = async (id) => {
    try {
      await api.deleteTable(id)
      setToast({ type: 'success', message: 'Stolik usunięty' })
      load()
    } catch (err) {
      setToast({ type: 'error', message: err.message })
    }
  }

  return (
    <div className="admin-grid">
      <form onSubmit={add} className="card form">
        <h3>Nowy stolik</h3>
        <label>Numer stolika
          <input type="number" min="1" value={tableNumber}
                 onChange={(e) => setTableNumber(e.target.value)} required />
        </label>
        <label>Liczba miejsc
          <input type="number" min="1" value={seats}
                 onChange={(e) => setSeats(e.target.value)} required />
        </label>
        <button className="btn btn-primary">Dodaj stolik</button>
      </form>

      <div className="card">
        <h3>Stoliki ({tables.length})</h3>
        <ul className="admin-list">
          {tables.map((table) => (
            <li key={table.id}>
              <span>Stolik {table.tableNumber} — {table.seats} miejsc</span>
              <button className="btn btn-danger" onClick={() => remove(table.id)}>Usuń</button>
            </li>
          ))}
        </ul>
      </div>
    </div>
  )
}

function OrdersTab({ setToast }) {
  const [orders, setOrders] = useState([])

  const load = () => api.getAllOrders().then(setOrders).catch(() => {})
  useEffect(() => {
    load()
    const interval = setInterval(load, 10000)
    return () => clearInterval(interval)
  }, [])

  const changeStatus = async (id, status) => {
    try {
      await api.updateOrderStatus(id, status)
      setToast({ type: 'success', message: `Zamówienie #${id}: ${STATUS_LABELS[status]}` })
      load()
    } catch (err) {
      setToast({ type: 'error', message: err.message })
    }
  }

  return (
    <div className="orders-list">
      {orders.length === 0 && <p className="muted">Brak zamówień.</p>}
      {orders.map((order) => (
        <div key={order.id} className="card order-card">
          <div className="order-head">
            <strong>#{order.id} — stolik {order.tableId}</strong>
            <select value={order.status} onChange={(e) => changeStatus(order.id, e.target.value)}>
              {STATUS_ORDER.map((s) => (
                <option key={s} value={s}>{STATUS_LABELS[s]}</option>
              ))}
            </select>
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
  )
}
