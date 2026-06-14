import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../utils/useAuth';

export const UserDashboard = () => {
  const { user } = useAuth();

  return (
    <div className="container mt-5">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h1>User Dashboard</h1>
          <p className="text-muted mb-0">Welcome back, {user?.name || user?.email}.</p>
        </div>
        <Link to="/stores" className="btn btn-primary">Browse Stores</Link>
      </div>

      <div className="row">
        <div className="col-md-6 mb-3">
          <div className="card h-100">
            <div className="card-body">
              <h5 className="card-title">Find Stores</h5>
              <p className="card-text">Search for stores and view community ratings.</p>
              <Link to="/stores" className="btn btn-outline-primary">Open Stores</Link>
            </div>
          </div>
        </div>
        <div className="col-md-6 mb-3">
          <div className="card h-100">
            <div className="card-body">
              <h5 className="card-title">Your Account</h5>
              <p className="card-text">Manage your password and account details.</p>
              <Link to="/account" className="btn btn-outline-secondary">Account</Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
