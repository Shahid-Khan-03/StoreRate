import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ownerService } from '../services/api';

export const OwnerDashboard = () => {
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await ownerService.dashboard();
      setDashboard(response.data);
    } catch (err) {
      setError('Failed to load owner dashboard');
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
        <h1>Store Owner Dashboard</h1>
        <Link to="/owner/ratings" className="btn btn-primary">View Ratings</Link>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="row mb-4">
        <div className="col-md-6 mb-3">
          <div className="card h-100">
            <div className="card-body">
              <h6 className="text-uppercase text-muted">Average Store Rating</h6>
              <h2>{dashboard ? dashboard.averageRating?.toFixed(1) : '0.0'}</h2>
            </div>
          </div>
        </div>
        <div className="col-md-6 mb-3">
          <div className="card h-100">
            <div className="card-body">
              <h6 className="text-uppercase text-muted">Total Ratings Received</h6>
              <h2>{dashboard?.totalRatings || 0}</h2>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
