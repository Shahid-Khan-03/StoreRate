import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { adminService } from '../services/api';

export const StoresPage = () => {
  const [stores, setStores] = useState([]);
  const [search, setSearch] = useState('');
  const [sortBy, setSortBy] = useState('name');
  const [direction, setDirection] = useState('asc');
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchStores();
  }, [search, sortBy, direction, page]);

  const fetchStores = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await adminService.getStores({
        search,
        sort: `${sortBy},${direction}`,
        page,
        size: 10,
      });
      setStores(response.data.content || []);
      setTotal(response.data.totalElements || 0);
    } catch (err) {
      setError('Failed to load stores');
    } finally {
      setLoading(false);
    }
  };

  const toggleSort = (field) => {
    if (sortBy === field) {
      setDirection(direction === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(field);
      setDirection('asc');
    }
  };

  const totalPages = Math.ceil(total / 10);

  return (
    <div className="container mt-5">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1>Manage Stores</h1>
        <Link to="/admin/stores/new" className="btn btn-primary">Add Store</Link>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="row mb-3">
        <div className="col-md-8 mb-2">
          <input
            className="form-control"
            placeholder="Search by name, email, or address"
            value={search}
            onChange={(e) => {
              setPage(0);
              setSearch(e.target.value);
            }}
          />
        </div>
        <div className="col-md-4 d-flex">
          <select
            className="form-select me-2"
            value={sortBy}
            onChange={(e) => {
              setPage(0);
              setSortBy(e.target.value);
            }}
          >
            <option value="name">Name</option>
            <option value="email">Email</option>
            <option value="address">Address</option>
          </select>
          <button
            type="button"
            className="btn btn-outline-secondary"
            onClick={() => setDirection(direction === 'asc' ? 'desc' : 'asc')}
          >
            {direction === 'asc' ? 'Asc' : 'Desc'}
          </button>
        </div>
      </div>

      <div className="table-responsive">
        <table className="table table-hover align-middle">
          <thead>
            <tr>
              <th onClick={() => toggleSort('name')} style={{ cursor: 'pointer' }}>Name</th>
              <th onClick={() => toggleSort('email')} style={{ cursor: 'pointer' }}>Email</th>
              <th onClick={() => toggleSort('address')} style={{ cursor: 'pointer' }}>Address</th>
              <th>Average Rating</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {stores.map((store) => (
              <tr key={store.id}>
                <td>{store.name}</td>
                <td>{store.email || '-'}</td>
                <td>{store.address || '-'}</td>
                <td>{store.averageRating == null ? 'No rating' : store.averageRating.toFixed(1)}</td>
                <td>
                  <Link to={`/admin/stores/${store.id}`} className="btn btn-sm btn-outline-primary">
                    Edit
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="d-flex justify-content-between align-items-center mt-3">
        <div>{total} stores found</div>
        <div>
          <button className="btn btn-outline-secondary me-2" disabled={page === 0} onClick={() => setPage(page - 1)}>
            Previous
          </button>
          <button className="btn btn-outline-secondary" disabled={page + 1 >= totalPages} onClick={() => setPage(page + 1)}>
            Next
          </button>
        </div>
      </div>
    </div>
  );
};