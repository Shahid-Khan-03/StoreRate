import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { adminService, userService } from '../services/api';

export const AddStorePage = () => {
  const navigate = useNavigate();
  const [owners, setOwners] = useState([]);
  const [form, setForm] = useState({
    name: '',
    email: '',
    address: '',
    description: '',
    ownerId: '',
  });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchOwners();
  }, []);

  const fetchOwners = async () => {
    try {
      const response = await userService.getAll({ search: 'STORE_OWNER', sortBy: 'name', direction: 'asc' });
      setOwners(
        (response.data || []).filter((user) => user.role === 'STORE_OWNER')
      );
    } catch (err) {
      setOwners([]);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      await adminService.createStore({
        name: form.name,
        email: form.email,
        address: form.address,
        description: form.description,
        ownerId: form.ownerId ? Number(form.ownerId) : null,
      });
      navigate('/admin/stores');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create store');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="container mt-5">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1>Add Store</h1>
        <Link to="/admin/stores" className="btn btn-secondary">Back to Stores</Link>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <form onSubmit={handleSubmit} className="card p-4">
        <div className="mb-3">
          <label htmlFor="name" className="form-label">Store Name</label>
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
          />
        </div>
        <div className="mb-3">
          <label htmlFor="address" className="form-label">Address</label>
          <input
            id="address"
            className="form-control"
            value={form.address}
            onChange={(e) => setForm({ ...form, address: e.target.value })}
          />
        </div>
        <div className="mb-3">
          <label htmlFor="description" className="form-label">Description</label>
          <textarea
            id="description"
            className="form-control"
            rows="3"
            value={form.description}
            onChange={(e) => setForm({ ...form, description: e.target.value })}
          />
        </div>
        <div className="mb-3">
          <label htmlFor="ownerId" className="form-label">Assign Store Owner</label>
          <select
            id="ownerId"
            className="form-select"
            value={form.ownerId}
            onChange={(e) => setForm({ ...form, ownerId: e.target.value })}
          >
            <option value="">No owner assigned</option>
            {owners.map((owner) => (
              <option key={owner.id} value={owner.id}>
                {owner.name} ({owner.email})
              </option>
            ))}
          </select>
        </div>
        <button className="btn btn-primary" disabled={submitting}>
          {submitting ? 'Creating...' : 'Create Store'}
        </button>
      </form>
    </div>
  );
};
