import React, { useState } from 'react';
import { userService } from '../services/api';
import { useAuth } from '../utils/useAuth';

export const Account = () => {
  const { user } = useAuth();
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');

    const passwordPattern = /^(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,16}$/;
    if (!passwordPattern.test(newPassword)) {
      setError('New password must be 8-16 chars, include an uppercase letter and a special character');
      return;
    }

    try {
      await userService.updatePassword(currentPassword, newPassword);
      setCurrentPassword('');
      setNewPassword('');
      setMessage('Password updated successfully');
    } catch (err) {
      setError(err.response?.data?.messages?.[0] || 'Failed to update password');
    }
  };

  return (
    <div className="container mt-5">
      <h1>Account</h1>
      <p className="text-muted">{user?.name || user?.email}</p>
      {message && <div className="alert alert-success">{message}</div>}
      {error && <div className="alert alert-danger">{error}</div>}
      <div className="row">
        <div className="col-md-6">
          <div className="card">
            <div className="card-body">
              <h4>Update Password</h4>
              <form onSubmit={handleSubmit}>
                <div className="mb-3">
                  <label className="form-label" htmlFor="currentPassword">Current Password</label>
                  <input className="form-control" id="currentPassword" type="password" value={currentPassword} onChange={(e) => setCurrentPassword(e.target.value)} required />
                </div>
                <div className="mb-3">
                  <label className="form-label" htmlFor="newPassword">New Password</label>
                  <input className="form-control" id="newPassword" type="password" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} required />
                </div>
                <button className="btn btn-primary" type="submit">Update Password</button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
