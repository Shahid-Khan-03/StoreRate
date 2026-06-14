import React, { createContext, useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const parseToken = (token) => {
    const decoded = jwtDecode(token);
    return {
      id: decoded.id,
      name: decoded.name || decoded.fullName || decoded.sub,
      email: decoded.sub,
      role: decoded.role || 'USER',
    };
  };

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        setUser(parseToken(token));
      } catch (error) {
        console.error('Invalid token:', error);
        localStorage.removeItem('token');
      }
    }
    setLoading(false);
  }, []);

  const login = (authData) => {
    const token = typeof authData === 'string' ? authData : authData.token;
    localStorage.setItem('token', token);
    try {
      setUser(typeof authData === 'string' ? parseToken(token) : {
        id: authData.id,
        name: authData.name,
        email: authData.email,
        role: authData.role,
      });
    } catch (error) {
      console.error('Invalid token:', error);
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthProvider;
