import React, { useEffect, useState } from 'react';
import { api } from '../services/api';

export default function HistoryModal({ username, onClose }) {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchHistory() {
      const data = await api.getHistory(username);
      setHistory(data || []);
      setLoading(false);
    }
    fetchHistory();
  }, [username]);

  return (
    <div className="simple-modal-backdrop" onClick={onClose}>
      <div className="simple-modal-box" onClick={(e) => e.stopPropagation()}>
        <div className="simple-modal-header">
          <h3>Match History ({username})</h3>
          <button className="close-btn" onClick={onClose}>&times;</button>
        </div>

        {loading ? (
          <p style={{ textAlign: 'center', padding: 12 }}>Loading...</p>
        ) : history.length === 0 ? (
          <p style={{ textAlign: 'center', padding: 12, color: 'var(--text-muted)' }}>
            No matches played yet.
          </p>
        ) : (
          <table className="simple-table">
            <thead>
              <tr>
                <th>Time</th>
                <th>You</th>
                <th>AI</th>
                <th>Result</th>
              </tr>
            </thead>
            <tbody>
              {history.map((item, idx) => (
                <tr key={item.id || idx}>
                  <td style={{ color: 'var(--text-muted)' }}>
                    {item.playedAt ? new Date(item.playedAt).toLocaleTimeString() : 'Recent'}
                  </td>
                  <td style={{ textTransform: 'capitalize' }}>{item.playerMove}</td>
                  <td style={{ textTransform: 'capitalize' }}>{item.computerMove}</td>
                  <td style={{
                    fontWeight: 600,
                    color: item.result === 'WIN' ? 'var(--win-color)' : item.result === 'LOSS' ? 'var(--loss-color)' : 'var(--tie-color)'
                  }}>
                    {item.result}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
