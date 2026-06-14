import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { storeService, userService } from '../services/api';

export const StoreForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [address, setAddress] = useState('');
  const [description, setDescription] = useState('');
  const [ownerId, setOwnerId] = useState('');
  const [owners, setOwners] = useState([]);
  const [loading, setLoading] = useState(!!id);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchOwners();
    if (id) {
      fetchStore();
    }
  }, [id]);

  const fetchStore = async () => {
    try {
      const response = await storeService.getById(id);
      setName(response.data.name);
      setAddress(response.data.address);
      setDescription(response.data.description || '');
      setOwnerId(response.data.ownerId || '');
    } catch (err) {
      setError('Failed to load store');
    } finally {
      setLoading(false);
    }
  };

  const fetchOwners = async () => {
    try {
      const response = await userService.getAll({ search: 'STORE_OWNER', sortBy: 'name' });
      setOwners(response.data.filter((user) => user.role === 'STORE_OWNER'));
    } catch (err) {
      setOwners([]);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');

    try {
      const data = { name, address, description, ownerId: ownerId ? parseInt(ownerId, 10) : null };
      if (id) {
        await storeService.update(id, data);
      } else {
        await storeService.create(data);
      }
      navigate('/admin');
    } catch (err) {
      setError(err.response?.data?.messages?.[0] || 'Failed to save store');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <div className="container mt-5"><div className="spinner-border"></div></div>;
  }

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-8">
          <h1 className="mb-4">{id ? 'Edit Store' : 'Add New Store'}</h1>

          {error && <div className="alert alert-danger">{error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label htmlFor="name" className="form-label">
                Store Name
              </label>
              <input
                type="text"
                className="form-control"
                id="name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </div>

            <div className="mb-3">
              <label htmlFor="address" className="form-label">
                Address
              </label>
              <input
                type="text"
                className="form-control"
                id="address"
                value={address}
                onChange={(e) => setAddress(e.target.value)}
              />
            </div>

            <div className="mb-3">
              <label htmlFor="description" className="form-label">
                Description
              </label>
              <textarea
                className="form-control"
                id="description"
                rows="4"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
              ></textarea>
            </div>

            <div className="mb-3">
              <label htmlFor="ownerId" className="form-label">
                Store Owner
              </label>
              <select className="form-select" id="ownerId" value={ownerId} onChange={(e) => setOwnerId(e.target.value)}>
                <option value="">No owner assigned</option>
                {owners.map((owner) => (
                  <option key={owner.id} value={owner.id}>{owner.name} ({owner.email})</option>
                ))}
              </select>
            </div>

            <div>
              <button
                type="submit"
                className="btn btn-primary me-2"
                disabled={submitting}
              >
                {submitting ? 'Saving...' : 'Save'}
              </button>
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => navigate('/admin')}
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};
