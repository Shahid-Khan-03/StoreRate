import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { adminService } from '../services/api';

export const AdminDashboard = () => {
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      const response = await adminService.dashboard();
      setDashboard(response.data);
    } catch (err) {
      setError('Failed to fetch dashboard');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="container mt-5"><div className="spinner-border"></div></div>;
  }

  return (
    <div className="container mt-5">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1>Admin Dashboard</h1>
        <div>
          <Link to="/admin/users" className="btn btn-outline-secondary me-2">Users</Link>
          <Link to="/admin/stores" className="btn btn-outline-secondary me-2">Stores</Link>
          <Link to="/admin/users/new" className="btn btn-primary me-2">Add User</Link>
          <Link to="/admin/stores/new" className="btn btn-primary">Add Store</Link>
        </div>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="row mb-4">
        <div className="col-md-4 mb-3">
          <div className="card h-100">
            <div className="card-body">
              <h6 className="text-uppercase text-muted">Total Users</h6>
              <h2>{dashboard?.totalUsers || 0}</h2>
            </div>
          </div>
        </div>
        <div className="col-md-4 mb-3">
          <div className="card h-100">
            <div className="card-body">
              <h6 className="text-uppercase text-muted">Total Stores</h6>
              <h2>{dashboard?.totalStores || 0}</h2>
            </div>
          </div>
        </div>
        <div className="col-md-4 mb-3">
          <div className="card h-100">
            <div className="card-body">
              <h6 className="text-uppercase text-muted">Total Ratings</h6>
              <h2>{dashboard?.totalRatings || 0}</h2>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
