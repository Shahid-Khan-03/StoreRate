import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { Navbar } from './components/Navbar';
import { ProtectedRoute } from './components/ProtectedRoute';
import { Home } from './pages/Home';
import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { Stores } from './pages/Stores';
import { StoreDetail } from './pages/StoreDetail';
import { AdminDashboard } from './pages/AdminDashboard';
import { StoreForm } from './pages/StoreForm';
import { UsersPage } from './pages/UsersPage';
import { UserDetailsPage } from './pages/UserDetailsPage';
import { AddUserPage } from './pages/AddUserPage';
import { StoresPage } from './pages/StoresPage';
import { AddStorePage } from './pages/AddStorePage';
import { OwnerDashboard } from './pages/OwnerDashboard';
import { OwnerRatingsPage } from './pages/OwnerRatingsPage';
import { Account } from './pages/Account';
import { UserDashboard } from './pages/UserDashboard';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import './App.css';

function App() {
  return (
    <Router>
      <AuthProvider>
        <Navbar />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/stores" element={<Stores />} />
          <Route path="/stores/:id" element={<StoreDetail />} />
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute requiredRoles={['USER']}>
                <UserDashboard />
              </ProtectedRoute>
            }
          />

          {/* Admin Routes */}
          <Route path="/admin" element={<Navigate to="/admin/dashboard" replace />} />
          <Route
            path="/admin/dashboard"
            element={
              <ProtectedRoute requiredRoles={['ADMIN']}>
                <AdminDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/users"
            element={
              <ProtectedRoute requiredRoles={['ADMIN']}>
                <UsersPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/users/new"
            element={
              <ProtectedRoute requiredRoles={['ADMIN']}>
                <AddUserPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/users/:id"
            element={
              <ProtectedRoute requiredRoles={['ADMIN']}>
                <UserDetailsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/stores"
            element={
              <ProtectedRoute requiredRoles={['ADMIN']}>
                <StoresPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/stores/new"
            element={
              <ProtectedRoute requiredRoles={['ADMIN']}>
                <AddStorePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/stores/:id"
            element={
              <ProtectedRoute requiredRoles={['ADMIN']}>
                <StoreForm />
              </ProtectedRoute>
            }
          />
          <Route path="/owner" element={<Navigate to="/owner/dashboard" replace />} />
          <Route
            path="/owner/dashboard"
            element={
              <ProtectedRoute requiredRoles={['STORE_OWNER']}>
                <OwnerDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/owner/ratings"
            element={
              <ProtectedRoute requiredRoles={['STORE_OWNER']}>
                <OwnerRatingsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/account"
            element={
              <ProtectedRoute>
                <Account />
              </ProtectedRoute>
            }
          />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;
