import { useEffect, useState } from 'react'
import { api } from '../api'
import Toast from '../components/Toast'

export default function Menu() {
  const [dishes, setDishes] = useState([])
  const [tables, setTables] = useState([])
  const [cart, setCart] = useState({})
  const [tableId, setTableId] = useState('')
  const [toast, setToast] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    api.getDishes().then(setDishes).catch(() => setToast({ type: 'error', message: 'Nie udało się pobrać menu' }))
    api.getTables().then(setTables).catch(() => {})
  }, [])

  const changeQty = (dishId, delta) => {
    setCart((prev) => {
      const qty = (prev[dishId] || 0) + delta
      const next = { ...prev }
      if (qty <= 0) delete next[dishId]
      else next[dishId] = qty
      return next
    })
  }

  const cartItems = Object.entries(cart).map(([dishId, quantity]) => {
    const dish = dishes.find((d) => d.id === Number(dishId))
    return { dish, quantity }
  })
  const total = cartItems.reduce((sum, { dish, quantity }) => sum + (dish ? dish.price * quantity : 0), 0)

  const placeOrder = async () => {
    setSubmitting(true)
    try {
      await api.createOrder({
        tableId: Number(tableId),
        items: Object.entries(cart).map(([dishId, quantity]) => ({ dishId: Number(dishId), quantity })),
      })
      setCart({})
      setToast({ type: 'success', message: 'Zamówienie złożone! Kuchnia już wie 🍳' })
    } catch (err) {
      setToast({ type: 'error', message: err.message })
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="menu-layout">
      <section>
        <h1>Menu</h1>
        {dishes.length === 0 && <p className="muted">Brak dań w menu. Admin musi je dodać.</p>}
        <div className="dish-grid">
          {dishes.map((dish) => (
            <div key={dish.id} className="card dish-card">
              <div className="dish-emoji">🍽️</div>
              <h3>{dish.name}</h3>
              <div className="dish-price">{Number(dish.price).toFixed(2)} zł</div>
              {cart[dish.id] ? (
                <div className="qty-controls">
                  <button className="btn btn-ghost" onClick={() => changeQty(dish.id, -1)}>−</button>
                  <span className="qty">{cart[dish.id]}</span>
                  <button className="btn btn-ghost" onClick={() => changeQty(dish.id, 1)}>+</button>
                </div>
              ) : (
                <button className="btn btn-primary" onClick={() => changeQty(dish.id, 1)}>
                  Dodaj do koszyka
                </button>
              )}
            </div>
          ))}
        </div>
      </section>

      <aside className="card cart">
        <h2>Koszyk</h2>
        {cartItems.length === 0 ? (
          <p className="muted">Pusto tu... dodaj coś z menu 😋</p>
        ) : (
          <>
            <ul className="cart-list">
              {cartItems.map(({ dish, quantity }) => dish && (
                <li key={dish.id}>
                  <span>{quantity} × {dish.name}</span>
                  <span>{(dish.price * quantity).toFixed(2)} zł</span>
                </li>
              ))}
            </ul>
            <div className="cart-total">
              <span>Razem</span>
              <strong>{total.toFixed(2)} zł</strong>
            </div>
            <label className="cart-table">
              Stolik
              <select value={tableId} onChange={(e) => setTableId(e.target.value)} required>
                <option value="">— wybierz stolik —</option>
                {tables.map((t) => (
                  <option key={t.id} value={t.id}>Stolik {t.tableNumber} ({t.seats} os.)</option>
                ))}
              </select>
            </label>
            <button className="btn btn-primary btn-full" disabled={!tableId || submitting} onClick={placeOrder}>
              {submitting ? '...' : 'Zamawiam 🚀'}
            </button>
          </>
        )}
      </aside>

      <Toast toast={toast} onClose={() => setToast(null)} />
    </div>
  )
}
