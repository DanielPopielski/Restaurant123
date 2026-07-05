import { Routes, Route, NavLink, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from './auth'
import Login from './pages/Login'
import Menu from './pages/Menu'
import MyOrders from './pages/MyOrders'
import Admin from './pages/Admin'
import Kitchen from './pages/Kitchen'

function RequireAuth({ children, admin = false }) {
  const { user } = useAuth()
  if (!user) return <Navigate to="/login" replace />
  if (admin && user.role !== 'ADMIN') return <Navigate to="/" replace />
  return children
}

export default function App() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="app">
      <nav className="nav">
        <div className="nav-brand">
          <span className="nav-logo">R</span> Restaurant
        </div>
        {user && (
          <div className="nav-links">
            <NavLink to="/">Menu</NavLink>
            <NavLink to="/orders">Moje zamówienia</NavLink>
            {user.role === 'ADMIN' && <NavLink to="/admin">Panel admina</NavLink>}
            {user.role === 'ADMIN' && <NavLink to="/kitchen">Kuchnia</NavLink>}
          </div>
        )}
        <div className="nav-user">
          {user ? (
            <>
              <span className="nav-username">
                {user.username}
                {user.role === 'ADMIN' && <span className="badge badge-admin">ADMIN</span>}
              </span>
              <button className="btn btn-ghost" onClick={handleLogout}>Wyloguj</button>
            </>
          ) : (
            <NavLink to="/login" className="btn btn-primary">Zaloguj</NavLink>
          )}
        </div>
      </nav>

      <main className="main">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/" element={<RequireAuth><Menu /></RequireAuth>} />
          <Route path="/orders" element={<RequireAuth><MyOrders /></RequireAuth>} />
          <Route path="/admin" element={<RequireAuth admin><Admin /></RequireAuth>} />
          <Route path="/kitchen" element={<RequireAuth admin><Kitchen /></RequireAuth>} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </div>
  )
}
