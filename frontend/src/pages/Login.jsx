import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api'
import { useAuth } from '../auth'

export default function Login() {
  const [mode, setMode] = useState('login')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const submit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const fn = mode === 'login' ? api.authenticate : api.register
      const data = await fn({ username, password })
      login(data)
      navigate('/')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-wrap">
      <div className="card login-card">
        <h1 className="login-title">
          {mode === 'login' ? 'Witaj ponownie' : 'Załóż konto'}
        </h1>
        <p className="login-sub">
          {mode === 'login' ? 'Zaloguj się, żeby zamawiać' : 'Rejestracja zajmie chwilę'}
        </p>

        <form onSubmit={submit} className="form">
          <label>
            Nazwa użytkownika
            <input value={username} onChange={(e) => setUsername(e.target.value)}
                   placeholder="np. daniel" required minLength={3} />
          </label>
          <label>
            Hasło
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)}
                   placeholder={mode === 'register' ? 'min. 8 znaków' : '••••••••'}
                   required minLength={mode === 'register' ? 8 : 1} />
          </label>

          {error && <div className="form-error">{error}</div>}

          <button className="btn btn-primary btn-full" disabled={loading}>
            {loading ? '...' : mode === 'login' ? 'Zaloguj się' : 'Zarejestruj się'}
          </button>
        </form>

        <button className="btn-link"
                onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setError(null) }}>
          {mode === 'login' ? 'Nie masz konta? Zarejestruj się' : 'Masz już konto? Zaloguj się'}
        </button>
      </div>
    </div>
  )
}
