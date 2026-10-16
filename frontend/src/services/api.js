// API Service for Kotlin Spring Boot + MySQL Backend
const BASE_URL = 'http://localhost:8080';

// Web Audio API Synthesizer
export const playSoundEffect = (type) => {
  try {
    const ctx = new (window.AudioContext || window.webkitAudioContext)();
    const osc = ctx.createOscillator();
    const gain = ctx.createGain();
    osc.connect(gain);
    gain.connect(ctx.destination);

    const now = ctx.currentTime;

    if (type === 'click') {
      osc.type = 'triangle';
      osc.frequency.setValueAtTime(440, now);
      osc.frequency.exponentialRampToValueAtTime(880, now + 0.08);
      gain.gain.setValueAtTime(0.15, now);
      gain.gain.exponentialRampToValueAtTime(0.01, now + 0.08);
      osc.start(now);
      osc.stop(now + 0.08);
    } else if (type === 'win') {
      osc.type = 'sine';
      osc.frequency.setValueAtTime(523.25, now); // C5
      osc.frequency.setValueAtTime(659.25, now + 0.1); // E5
      osc.frequency.setValueAtTime(783.99, now + 0.2); // G5
      osc.frequency.setValueAtTime(1046.50, now + 0.3); // C6
      gain.gain.setValueAtTime(0.2, now);
      gain.gain.exponentialRampToValueAtTime(0.01, now + 0.45);
      osc.start(now);
      osc.stop(now + 0.45);
    } else if (type === 'loss') {
      osc.type = 'sawtooth';
      osc.frequency.setValueAtTime(320, now);
      osc.frequency.exponentialRampToValueAtTime(140, now + 0.35);
      gain.gain.setValueAtTime(0.2, now);
      gain.gain.exponentialRampToValueAtTime(0.01, now + 0.35);
      osc.start(now);
      osc.stop(now + 0.35);
    } else if (type === 'tie') {
      osc.type = 'square';
      osc.frequency.setValueAtTime(350, now);
      osc.frequency.setValueAtTime(350, now + 0.12);
      gain.gain.setValueAtTime(0.1, now);
      gain.gain.exponentialRampToValueAtTime(0.01, now + 0.25);
      osc.start(now);
      osc.stop(now + 0.25);
    }
  } catch (e) {
    // AudioContext blocked or not supported
  }
};

// Helper to save a match round to local history storage
function recordLocalHistory(username, playerMove, computerMove, result) {
  try {
    const key = `rps_history_${username}`;
    const existing = JSON.parse(localStorage.getItem(key) || '[]');
    const newRound = {
      id: Date.now(),
      playerMove,
      computerMove,
      result, // 'WIN', 'LOSS', 'TIE'
      playedAt: new Date().toISOString()
    };
    const updated = [newRound, ...existing].slice(0, 50); // keep last 50
    localStorage.setItem(key, JSON.stringify(updated));
  } catch (e) {
    console.error('Failed to save local history', e);
  }
}

// Helper to update local leaderboard rankings
function updateLocalLeaderboard(username, result) {
  try {
    const key = 'rps_leaderboard_data';
    const current = JSON.parse(localStorage.getItem(key) || '{}');
    if (!current[username]) {
      current[username] = { username, wins: 0, losses: 0, ties: 0, totalGames: 0 };
    }
    const p = current[username];
    if (result === 'WIN' || result === 'you won') p.wins++;
    else if (result === 'LOSS' || result === 'you lose') p.losses++;
    else p.ties++;
    p.totalGames = p.wins + p.losses + p.ties;
    p.winRate = p.totalGames > 0 ? Math.round((p.wins / p.totalGames) * 100) : 0;
    current[username] = p;
    localStorage.setItem(key, JSON.stringify(current));
  } catch (e) {
    console.error('Failed to update local leaderboard', e);
  }
}

export const api = {
  // Check if Spring Boot backend is active
  async healthCheck() {
    try {
      const res = await fetch(`${BASE_URL}/v3/api-docs`, { method: 'GET' });
      return res.ok;
    } catch {
      return false;
    }
  },

  // Register user
  async register(username, password, email) {
    try {
      const res = await fetch(`${BASE_URL}/api/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username,
          password,
          email: email || `${username}@example.com`
        })
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data.message || 'Registration failed');
      return data;
    } catch (err) {
      if (err.message.includes('Failed to fetch') || err.message.includes('NetworkError')) {
        const localUsers = JSON.parse(localStorage.getItem('rps_users') || '[]');
        if (localUsers.some(u => u.username.toLowerCase() === username.toLowerCase())) {
          throw new Error('Username already exists');
        }
        localUsers.push({ username, password });
        localStorage.setItem('rps_users', JSON.stringify(localUsers));
        return { username, token: 'offline-token' };
      }
      throw err;
    }
  },

  // Login user
  async login(username, password) {
    try {
      const res = await fetch(`${BASE_URL}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data.message || 'Invalid credentials');
      return data;
    } catch (err) {
      if (err.message.includes('Failed to fetch') || err.message.includes('NetworkError')) {
        const localUsers = JSON.parse(localStorage.getItem('rps_users') || '[]');
        const user = localUsers.find(u => u.username === username && u.password === password);
        if (user) {
          return { username, token: 'offline-token' };
        }
        if (username === 'demo' || username === 'admin' || username === 'Player1') {
          return { username, token: 'demo-token' };
        }
        throw new Error('Backend offline. Use "demo" or start Kotlin backend.');
      }
      throw err;
    }
  },

  // Play move
  async playMove(username, move) {
    const normalizedMove = move.toLowerCase(); // rock, paper, scissors
    try {
      const res = await fetch(`${BASE_URL}/api/game/play`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username,
          playerMove: normalizedMove
        })
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data.message || 'Failed to submit move');

      // Normalize result: "you won" -> "WIN", "you lose" -> "LOSS", "Tie" -> "TIE"
      let normResult = 'TIE';
      if (data.result === 'you won' || data.result === 'WIN') normResult = 'WIN';
      else if (data.result === 'you lose' || data.result === 'LOSS') normResult = 'LOSS';

      const currentScore = data.score || data.currentScore || { wins: 0, losses: 0, ties: 0 };

      // Also mirror to local history & leaderboard for resilience
      recordLocalHistory(username, normalizedMove, data.computerMove.toLowerCase(), normResult);
      updateLocalLeaderboard(username, normResult);

      return {
        playerMove: data.playerMove.toLowerCase(),
        computerMove: data.computerMove.toLowerCase(),
        result: normResult,
        message: data.message,
        currentScore: {
          wins: currentScore.wins,
          losses: currentScore.losses,
          ties: currentScore.ties
        }
      };
    } catch (err) {
      // Local fallback algorithm when backend/MySQL is not reachable
      const choices = ['rock', 'paper', 'scissors'];
      const computerMove = choices[Math.floor(Math.random() * choices.length)];
      let normResult = 'TIE';

      if (
        (normalizedMove === 'rock' && computerMove === 'scissors') ||
        (normalizedMove === 'paper' && computerMove === 'rock') ||
        (normalizedMove === 'scissors' && computerMove === 'paper')
      ) {
        normResult = 'WIN';
      } else if (normalizedMove !== computerMove) {
        normResult = 'LOSS';
      }

      // Update local scoreboard
      const scoreKey = `rps_score_${username}`;
      const localScore = JSON.parse(localStorage.getItem(scoreKey) || '{"wins":0,"losses":0,"ties":0}');
      if (normResult === 'WIN') localScore.wins++;
      else if (normResult === 'LOSS') localScore.losses++;
      else localScore.ties++;
      localStorage.setItem(scoreKey, JSON.stringify(localScore));

      // Always maintain history and leaderboard!
      recordLocalHistory(username, normalizedMove, computerMove, normResult);
      updateLocalLeaderboard(username, normResult);

      return {
        playerMove: normalizedMove,
        computerMove,
        result: normResult,
        message: normResult === 'WIN' ? 'You Win!' : normResult === 'LOSS' ? 'You Lose!' : 'Tie!',
        currentScore: localScore
      };
    }
  },

  // Get score
  async getScore(username) {
    try {
      const res = await fetch(`${BASE_URL}/api/game/score?username=${encodeURIComponent(username)}`);
      const data = await res.json();
      if (res.ok && data.data) {
        return data.data;
      }
    } catch {
      // fallback to local
    }
    return JSON.parse(localStorage.getItem(`rps_score_${username}`) || '{"wins":0,"losses":0,"ties":0}');
  },

  // Reset score
  async resetScore(username) {
    try {
      const res = await fetch(`${BASE_URL}/api/game/reset`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username })
      });
      const data = await res.json();
      if (res.ok && data.data) {
        return data.data;
      }
    } catch {
      // fallback
    }
    const zeroScore = { wins: 0, losses: 0, ties: 0 };
    localStorage.setItem(`rps_score_${username}`, JSON.stringify(zeroScore));
    return zeroScore;
  },

  // Get history
  async getHistory(username) {
    try {
      const res = await fetch(`${BASE_URL}/api/game/history?username=${encodeURIComponent(username)}&size=20`);
      const data = await res.json();
      // Handle Spring Data Page structure (data.data.content or data.content)
      const list = data?.data?.content || data?.data?.items || data?.content;
      if (Array.isArray(list) && list.length > 0) {
        return list.map(item => ({
          id: item.id,
          playerMove: item.playerMove,
          computerMove: item.computerMove,
          result: item.result === 'you won' ? 'WIN' : item.result === 'you lose' ? 'LOSS' : item.result,
          playedAt: item.playedAt
        }));
      }
    } catch {
      // fallback to local
    }
    // Return persistent local history
    return JSON.parse(localStorage.getItem(`rps_history_${username}`) || '[]');
  },

  // Get leaderboard
  async getLeaderboard() {
    try {
      const res = await fetch(`${BASE_URL}/api/game/leaderboard?limit=10`);
      const data = await res.json();
      const list = data?.data || data;
      if (Array.isArray(list) && list.length > 0) {
        return list;
      }
    } catch {
      // fallback to local
    }

    // Build and return local leaderboard
    try {
      const stored = JSON.parse(localStorage.getItem('rps_leaderboard_data') || '{}');
      const players = Object.values(stored);
      if (players.length > 0) {
        return players
          .sort((a, b) => b.wins - a.wins || a.losses - b.losses)
          .slice(0, 10);
      }
    } catch {
      // ignore
    }

    // Default seeded ranking if nothing played yet
    return [
      { username: 'ComputerAI', wins: 12, losses: 8, winRate: 60 },
      { username: 'Champion', wins: 10, losses: 4, winRate: 71 },
      { username: 'DemoPlayer', wins: 5, losses: 3, winRate: 62 }
    ];
  }
};
