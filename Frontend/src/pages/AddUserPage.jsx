import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { adminService } from '../services/api';

export const AddUserPage = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    name: '',
    email: '',
    password: '',
    address: '',
    role: 'USER',
  });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');


    if (form.name.length < 20 || form.name.length > 60) {
      setError('Name must be between 20 and 60 characters');
      return;
    }
    const passwordPattern = /^(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,16}$/;
    if (!passwordPattern.test(form.password)) {
      setError('Password must be 8-16 chars, include an uppercase letter and a special character');
      return;
    }
    if (form.address && form.address.length > 400) {
      setError('Address must be 400 characters or less');
      return;
    }

    setSubmitting(true);

    try {
      await adminService.createUser(form);
      navigate('/admin/users');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create user');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="container mt-5">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1>Create User</h1>
        <Link to="/admin/users" className="btn btn-secondary">Back</Link>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <form onSubmit={handleSubmit} className="card p-4">
        <div className="mb-3">
          <label htmlFor="name" className="form-label">Name</label>
          <input
            id="name"
            className="form-control"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="email" className="form-label">Email</label>
          <input
            id="email"
            type="email"
            className="form-control"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="password" className="form-label">Password</label>
          <input
            id="password"
            type="password"
            className="form-control"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="address" className="form-label">Address</label>
          <textarea
            id="address"
            className="form-control"
            rows="3"
            value={form.address}
            onChange={(e) => setForm({ ...form, address: e.target.value })}
          />
        </div>
        <div className="mb-3">
          <label htmlFor="role" className="form-label">Role</label>
          <select
            id="role"
            className="form-select"
            value={form.role}
            onChange={(e) => setForm({ ...form, role: e.target.value })}
          >
            <option value="USER">User</option>
            <option value="ADMIN">Admin</option>
            <option value="STORE_OWNER">Store Owner</option>
          </select>
        </div>
        <button className="btn btn-primary" disabled={submitting}>
          {submitting ? 'Creating...' : 'Create User'}
        </button>
      </form>
    </div>
  );
};
