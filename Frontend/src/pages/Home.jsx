import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../utils/useAuth';

export const Home = () => {
  const { user } = useAuth();

  return (
    <div className="container mt-5">
      <div className="jumbotron">
        <h1 className="display-4">Welcome to StoreRate ⭐</h1>
        <p className="lead">
          Discover and rate your favorite stores. Share your experience with the community.
        </p>
        <hr className="my-4" />
        {!user ? (
          <div>
            <p>Get started by creating an account or logging in.</p>
            <Link className="btn btn-primary btn-lg me-2" to="/register" role="button">
              Register
            </Link>
            <Link className="btn btn-secondary btn-lg" to="/login" role="button">
              Login
            </Link>
          </div>
        ) : (
          <div>
            <p>Ready to explore stores and leave your ratings?</p>
            <Link className="btn btn-primary btn-lg" to="/stores" role="button">
              Browse Stores
            </Link>
          </div>
        )}
      </div>

      <div className="row mt-5">
        <div className="col-md-4 mb-4">
          <div className="card">
            <div className="card-body">
              <h5 className="card-title">🔍 Discover</h5>
              <p className="card-text">
                Search and discover new stores in your area with detailed information.
              </p>
            </div>
          </div>
        </div>
        <div className="col-md-4 mb-4">
          <div className="card">
            <div className="card-body">
              <h5 className="card-title">⭐ Rate</h5>
              <p className="card-text">
                Share your experience by rating stores and leaving detailed reviews.
              </p>
            </div>
          </div>
        </div>
        <div className="col-md-4 mb-4">
          <div className="card">
            <div className="card-body">
              <h5 className="card-title">👥 Connect</h5>
              <p className="card-text">
                Connect with other users and read reviews from the community.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
