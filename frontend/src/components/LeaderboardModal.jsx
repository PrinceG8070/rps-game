import React, { useEffect, useState } from 'react';
import { api } from '../services/api';

export default function LeaderboardModal({ onClose }) {
  const [leaders, setLeaders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchLeaders() {
      const data = await api.getLeaderboard();
      setLeaders(data || []);
      setLoading(false);
    }
    fetchLeaders();
  }, []);

  return (
    <div className="simple-modal-backdrop" onClick={onClose}>
      <div className="simple-modal-box" onClick={(e) => e.stopPropagation()}>
        <div className="simple-modal-header">
          <h3>Top Players</h3>
          <button className="close-btn" onClick={onClose}>&times;</button>
        </div>

        {loading ? (
          <p style={{ textAlign: 'center', padding: 12 }}>Loading...</p>
        ) : leaders.length === 0 ? (
          <p style={{ textAlign: 'center', padding: 12, color: 'var(--text-muted)' }}>
            No player stats recorded yet.
          </p>
        ) : (
          <table className="simple-table">
            <thead>
              <tr>
                <th>Rank</th>
                <th>Player</th>
                <th>Wins</th>
                <th>Losses</th>
              </tr>
            </thead>
            <tbody>
              {leaders.map((player, idx) => (
                <tr key={player.username || idx}>
                  <td>#{idx + 1}</td>
                  <td>{player.username}</td>
                  <td style={{ color: 'var(--win-color)', fontWeight: 600 }}>{player.wins}</td>
                  <td style={{ color: 'var(--loss-color)' }}>{player.losses}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
