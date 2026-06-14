import React, { useEffect, useState } from 'react';
import { ownerService } from '../services/api';

export const OwnerRatingsPage = () => {
  const [ratings, setRatings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchRatings();
  }, []);

  const fetchRatings = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await ownerService.getRatings();
      setRatings(response.data);
    } catch (err) {
      setError('Failed to load ratings');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="container mt-5"><div className="spinner-border"></div></div>;
  }

  return (
    <div className="container mt-5">
      <h1>Ratings for Your Store</h1>
      {error && <div className="alert alert-danger">{error}</div>}
      <div className="table-responsive mt-4">
        <table className="table table-hover align-middle">
          <thead>
            <tr>
              <th>User Name</th>
              <th>User Email</th>
              <th>Rating</th>
              <th>Rating Date</th>
            </tr>
          </thead>
          <tbody>
            {ratings.length > 0 ? (
              ratings.map((rating, index) => (
                <tr key={index}>
                  <td>{rating.userName}</td>
                  <td>{rating.userEmail}</td>
                  <td>{rating.rating}</td>
                  <td>{rating.ratingDate || '-'}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="4" className="text-center text-muted">No ratings yet.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};