import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { adminService } from '../services/api';

export const UserDetailsPage = () => {
  const { id } = useParams();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchUser();
  }, [id]);

  const fetchUser = async () => {
    setLoading(true);
    try {
      const response = await adminService.getUser(id);
      setUser(response.data);
    } catch (err) {
      setError('Failed to load user details');
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
        <h1>User Details</h1>
        <Link to="/admin/users" className="btn btn-secondary">Back to Users</Link>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      {user && (
        <div className="card">
          <div className="card-body">
            <h4>{user.name}</h4>
            <p><strong>Email:</strong> {user.email}</p>
            <p><strong>Address:</strong> {user.address || 'N/A'}</p>
            <p><strong>Role:</strong> {user.role || 'N/A'}</p>
            {user.role === 'STORE_OWNER' && (
              <p><strong>Average Store Rating:</strong> {user.ownerAverageRating == null ? 'N/A' : user.ownerAverageRating.toFixed(1)}</p>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
