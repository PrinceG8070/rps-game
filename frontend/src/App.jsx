import React, { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import Login from './components/Login';
import Register from './components/Register';
import Game from './components/Game';
import LeaderboardModal from './components/LeaderboardModal';
import HistoryModal from './components/HistoryModal';
import { api } from './services/api';

export default function App() {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('rps_auth_user');
    return saved ? JSON.parse(saved) : null;
  });

  const [authView, setAuthView] = useState('login'); // 'login' | 'register'
  const [isDark, setIsDark] = useState(false); // default normal light theme
  const [showLeaderboard, setShowLeaderboard] = useState(false);
  const [showHistory, setShowHistory] = useState(false);
  const [isOnline, setIsOnline] = useState(false);

  // Poll backend health status
  useEffect(() => {
    let interval;
    const checkHealth = async () => {
      const ok = await api.healthCheck();
      setIsOnline(ok);
    };
    checkHealth();
    interval = setInterval(checkHealth, 5000);
    return () => clearInterval(interval);
  }, []);

  // Sync body theme class
  useEffect(() => {
    if (isDark) {
      document.body.classList.add('dark-theme');
    } else {
      document.body.classList.remove('dark-theme');
    }
  }, [isDark]);

  const handleLoginSuccess = (userData) => {
    setUser(userData);
    localStorage.setItem('rps_auth_user', JSON.stringify(userData));
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('rps_auth_user');
  };

  return (
    <div className="app-wrapper">
      <Navbar
        user={user}
        onLogout={handleLogout}
        isDark={isDark}
        onToggleTheme={() => setIsDark(!isDark)}
        onOpenLeaderboard={() => setShowLeaderboard(true)}
        onOpenHistory={() => setShowHistory(true)}
        isOnline={isOnline}
      />

      <main>
        {user ? (
          <Game user={user} />
        ) : authView === 'login' ? (
          <Login
            onLoginSuccess={handleLoginSuccess}
            onSwitchToRegister={() => setAuthView('register')}
          />
        ) : (
          <Register
            onRegisterSuccess={() => setAuthView('login')}
            onSwitchToLogin={() => setAuthView('login')}
          />
        )}
      </main>

      {showLeaderboard && (
        <LeaderboardModal onClose={() => setShowLeaderboard(false)} />
      )}

      {showHistory && user && (
        <HistoryModal username={user.username} onClose={() => setShowHistory(false)} />
      )}
    </div>
  );
}
