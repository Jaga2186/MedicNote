import { createContext, useContext, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [role, setRole] = useState(localStorage.getItem('role'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (token && storedUser) {
      setUser(JSON.parse(storedUser));
      setRole(localStorage.getItem('role'));
    }
    setLoading(false);
  }, [token]);

  const login = (tokenVal, roleVal, userData) => {
    localStorage.setItem('token', tokenVal);
    localStorage.setItem('role', roleVal);
    localStorage.setItem('user', JSON.stringify(userData));
    setToken(tokenVal);
    setRole(roleVal);
    setUser(userData);
  };

  const logout = () => {
    localStorage.clear();
    setToken(null);
    setRole(null);
    setUser(null);
  };

  const isAuthenticated = !!token;
  const isDoctor = role === 'DOCTOR';
  const isPatient = role === 'PATIENT';

  return (
    <AuthContext.Provider value={{ user, token, role, loading, isAuthenticated, isDoctor, isPatient, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
};
