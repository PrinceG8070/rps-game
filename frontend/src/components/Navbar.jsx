import React from 'react';
import { playSoundEffect } from '../services/api';

export default function Navbar({
  user,
  onLogout,
  isDark,
  onToggleTheme,
  onOpenLeaderboard,
  onOpenHistory,
  isOnline
}) {
  return (
    <header className="simple-header">
      <div>
        <h1 className="brand-title">Rock Paper Scissors</h1>
        <p className="brand-sub">Web & Mobile Application</p>
      </div>

      <div className="header-right">
        {/* Connection status badge */}
        <span className={`status-badge ${isOnline ? 'online' : 'offline'}`}>
          {isOnline ? 'Online' : 'Local'}
        </span>

        {user && (
          <>
            <button
              className="btn-small"
              onClick={() => { playSoundEffect('click'); onOpenLeaderboard(); }}
            >
              Leaderboard
            </button>
            <button
              className="btn-small"
              onClick={() => { playSoundEffect('click'); onOpenHistory(); }}
            >
              History
            </button>
          </>
        )}

        {/* Theme Lightbulb Switch */}
        <button
          className="bulb-switch"
          onClick={() => { playSoundEffect('click'); onToggleTheme(); }}
          title={isDark ? "Switch to Light Mode" : "Switch to Dark Mode"}
        >
          <img
            src={isDark ? "/photos/bulb-on.webp" : "/photos/bulb-off.webp"}
            alt="Bulb"
          />
        </button>

        {user && (
          <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
            <span className="user-badge">{user.username}</span>
            <button
              className="btn-small"
              onClick={() => { playSoundEffect('click'); onLogout(); }}
            >
              Logout
            </button>
          </div>
        )}
      </div>
    </header>
  );
}
