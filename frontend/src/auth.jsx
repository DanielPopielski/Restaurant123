import { createContext, useContext, useState } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('auth')
    return saved ? JSON.parse(saved) : null
  })

  const login = (data) => {
    localStorage.setItem('auth', JSON.stringify(data))
    setUser(data)
  }

  const logout = () => {
    localStorage.removeItem('auth')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
