import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { storeService } from '../services/api';
import { useAuth } from '../utils/useAuth';

export const Stores = () => {
  const [stores, setStores] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState('name');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { user } = useAuth();

  useEffect(() => {
    fetchStores();
  }, [searchTerm, sortBy]);

  const fetchStores = async () => {
    try {
      const response = await storeService.getAll({
        search: searchTerm,
        sortBy,
        direction: sortBy === 'rating' ? 'desc' : 'asc',
      });
      setStores(response.data);
    } catch (err) {
      setError('Failed to fetch stores');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="container mt-5"><div className="spinner-border"></div></div>;
  }

  return (
    <div className="container mt-5">
      <h1 className="mb-4">Stores</h1>
      {error && <div className="alert alert-danger">{error}</div>}

      <div className="row mb-4">
        <div className="col-md-7">
          <input
            type="text"
            className="form-control"
            placeholder="Search by store name or address"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <div className="col-md-5">
          <select className="form-select" value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
            <option value="name">Sort by Name</option>
            <option value="address">Sort by Address</option>
            <option value="rating">Sort by Rating</option>
          </select>
        </div>
      </div>

      {user?.role === 'ADMIN' && (
        <div className="mb-4">
          <Link to="/admin/stores/new" className="btn btn-primary">Add New Store</Link>
        </div>
      )}

      <div className="row">
        {stores.length > 0 ? stores.map((store) => (
          <div key={store.id} className="col-md-6 col-lg-4 mb-4">
            <div className="card h-100">
              <div className="card-body">
                <h5 className="card-title">{store.name}</h5>
                <p className="card-text">{store.address}</p>
                <p className="card-text text-muted">{store.description}</p>
                {store.averageRating ? (
                  <div className="mb-2">
                    <strong>Overall rating: {store.averageRating.toFixed(1)}</strong> ({store.ratingsCount} ratings)
                  </div>
                ) : (
                  <p className="text-muted">No ratings yet</p>
                )}
                {user?.role === 'USER' && (
                  <p className="card-text">Your rating: {store.userRating || 'Not submitted'}</p>
                )}
              </div>
              <div className="card-footer">
                <Link to={`/stores/${store.id}`} className="btn btn-sm btn-primary w-100">View Details</Link>
              </div>
            </div>
          </div>
        )) : (
          <p className="text-center text-muted">No stores found</p>
        )}
      </div>
    </div>
  );
};
