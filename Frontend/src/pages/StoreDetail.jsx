import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ratingService, storeService } from '../services/api';
import { useAuth } from '../utils/useAuth';

export const StoreDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [store, setStore] = useState(null);
  const [ratings, setRatings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [score, setScore] = useState(5);
  const [comment, setComment] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchData();
  }, [id]);

  const fetchData = async () => {
    try {
      const storeRes = await storeService.getById(id);
      setStore(storeRes.data);
      const ratingsRes = await ratingService.getByStore(id);
      setRatings(ratingsRes.data);
    } catch (err) {
      setError('Failed to load store details');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmitRating = async (e) => {
    e.preventDefault();
    if (!user) {
      navigate('/login');
      return;
    }

    setSubmitting(true);
    try {
      await ratingService.create({
        rating: parseInt(score, 10),
        comment,
        storeId: parseInt(id, 10),
      });
      setComment('');
      setShowForm(false);
      fetchData();
    } catch (err) {
      setError(err.response?.data?.messages?.[0] || 'Failed to submit rating');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <div className="container mt-5"><div className="spinner-border"></div></div>;
  }

  if (!store) {
    return <div className="container mt-5"><p>Store not found</p></div>;
  }

  return (
    <div className="container mt-5">
      <button className="btn btn-secondary mb-3" onClick={() => navigate('/stores')}>Back</button>

      <h1>{store.name}</h1>
      <p className="text-muted">{store.address}</p>
      <p>{store.description}</p>
      {store.averageRating ? (
        <h5>{store.averageRating.toFixed(1)} ({store.ratingsCount} ratings)</h5>
      ) : (
        <p className="text-muted">No ratings yet</p>
      )}
      {user?.role === 'USER' && (
        <p>Your submitted rating: {store.userRating || 'Not submitted'}</p>
      )}

      {error && <div className="alert alert-danger mt-3">{error}</div>}

      <div className="mt-5">
        <h3>Ratings</h3>
        {user?.role === 'USER' && (
          <button
            className="btn btn-primary mb-3"
            onClick={() => {
              setScore(store.userRating || 5);
              setShowForm(!showForm);
            }}
          >
            {showForm ? 'Cancel' : store.userRating ? 'Modify Rating' : 'Submit Rating'}
          </button>
        )}

        {showForm && (
          <div className="card mb-4">
            <div className="card-body">
              <form onSubmit={handleSubmitRating}>
                <div className="mb-3">
                  <label htmlFor="score" className="form-label">Rating (1-5)</label>
                  <select className="form-select" id="score" value={score} onChange={(e) => setScore(e.target.value)}>
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="4">4</option>
                    <option value="5">5</option>
                  </select>
                </div>
                <div className="mb-3">
                  <label htmlFor="comment" className="form-label">Comment</label>
                  <textarea className="form-control" id="comment" rows="3" value={comment} onChange={(e) => setComment(e.target.value)}></textarea>
                </div>
                <button type="submit" className="btn btn-success" disabled={submitting}>
                  {submitting ? 'Submitting...' : 'Save Rating'}
                </button>
              </form>
            </div>
          </div>
        )}

        <div className="row">
          {ratings.length > 0 ? ratings.map((rating) => (
            <div key={rating.id} className="col-md-6 mb-3">
              <div className="card">
                <div className="card-body">
                  <h6 className="card-subtitle mb-2">{rating.userName} - {rating.rating}</h6>
                  {rating.comment && <p className="card-text">{rating.comment}</p>}
                </div>
              </div>
            </div>
          )) : (
            <p className="text-muted">No ratings yet.</p>
          )}
        </div>
      </div>
    </div>
  );
};
