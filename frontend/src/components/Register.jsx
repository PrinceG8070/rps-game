import React, { useState } from 'react';
import { api, playSoundEffect } from '../services/api';

export default function Register({ onRegisterSuccess, onSwitchToLogin }) {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!username.trim() || !password.trim()) {
      setError('Please fill in all fields');
      return;
    }

    if (password !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    setError('');
    setSuccess('');
    setLoading(true);
    playSoundEffect('click');

    try {
      await api.register(username.trim(), password, email.trim());
      setSuccess('Registration successful! Redirecting to login...');
      setTimeout(() => {
        onRegisterSuccess();
      }, 900);
    } catch (err) {
      setError(err.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="clean-card">
      <h2 className="form-title">Register</h2>
      <p className="form-desc">Create an account to track your scores</p>

      {error && <div className="alert-box alert-error">{error}</div>}
      {success && <div className="alert-box alert-success">{success}</div>}

      <form onSubmit={handleSubmit}>
        <div className="input-group">
          <label className="input-label" htmlFor="reg-username">Username</label>
          <input
            id="reg-username"
            type="text"
            className="normal-input"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </div>

        <div className="input-group">
          <label className="input-label" htmlFor="reg-email">Email (Optional)</label>
          <input
            id="reg-email"
            type="email"
            className="normal-input"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>

        <div className="input-group">
          <label className="input-label" htmlFor="reg-password">Password</label>
          <input
            id="reg-password"
            type="password"
            className="normal-input"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>

        <div className="input-group">
          <label className="input-label" htmlFor="reg-confirm">Confirm Password</label>
          <input
            id="reg-confirm"
            type="password"
            className="normal-input"
            placeholder="Confirm Password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
          />
        </div>

        <button type="submit" className="btn-submit" disabled={loading}>
          {loading ? 'Registering...' : 'Register'}
        </button>
      </form>

      <div className="switch-text">
        Already have an account?{' '}
        <span className="switch-link" onClick={() => { playSoundEffect('click'); onSwitchToLogin(); }}>
          Login
        </span>
      </div>
    </div>
  );
}
