import React, { useState } from 'react';
import { api, playSoundEffect } from '../services/api';

export default function Login({ onLoginSuccess, onSwitchToRegister }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!username.trim() || !password.trim()) {
      setError('Please enter username and password');
      return;
    }

    setError('');
    setLoading(true);
    playSoundEffect('click');

    try {
      const data = await api.login(username.trim(), password);
      onLoginSuccess({
        username: data.username || username,
        token: data.token
      });
    } catch (err) {
      setError(err.message || 'Invalid credentials');
    } finally {
      setLoading(false);
    }
  };

  const handleGuestLogin = () => {
    playSoundEffect('click');
    onLoginSuccess({
      username: 'Player1',
      token: 'guest-token'
    });
  };

  return (
    <div className="clean-card">
      <h2 className="form-title">Login</h2>
      <p className="form-desc">Sign in to play Rock Paper Scissors</p>

      {error && <div className="alert-box alert-error">{error}</div>}

      <form onSubmit={handleSubmit}>
        <div className="input-group">
          <label className="input-label" htmlFor="username">Username</label>
          <input
            id="username"
            type="text"
            className="normal-input"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="off"
            required
          />
        </div>

        <div className="input-group">
          <label className="input-label" htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            className="normal-input"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>

        <button type="submit" className="btn-submit" disabled={loading}>
          {loading ? 'Logging in...' : 'Login'}
        </button>
      </form>

      <button
        type="button"
        className="btn-secondary"
        onClick={handleGuestLogin}
      >
        Play as Guest
      </button>

      <div className="switch-text">
        Don&apos;t have an account?{' '}
        <span className="switch-link" onClick={() => { playSoundEffect('click'); onSwitchToRegister(); }}>
          Register
        </span>
      </div>
    </div>
  );
}
