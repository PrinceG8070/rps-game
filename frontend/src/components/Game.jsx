import React, { useState, useEffect } from 'react';
import { api, playSoundEffect } from '../services/api';

const MOVE_IMAGES = {
  rock: '/photos/Rock-removebg-preview.png',
  paper: '/photos/paper-removebg-preview.png',
  scissors: '/photos/scissor-vector-icon-removebg-preview.png'
};

export default function Game({ user }) {
  const [score, setScore] = useState({ wins: 0, losses: 0, ties: 0 });
  const [playerMove, setPlayerMove] = useState(null);
  const [computerMove, setComputerMove] = useState(null);
  const [result, setResult] = useState(null); // 'WIN' | 'LOSS' | 'TIE'
  const [message, setMessage] = useState('Choose your move to begin!');

  // Load score on user change
  useEffect(() => {
    let mounted = true;
    async function loadScore() {
      if (user?.username) {
        const s = await api.getScore(user.username);
        if (mounted && s) {
          setScore({
            wins: s.wins || 0,
            losses: s.losses || 0,
            ties: s.ties || 0
          });
        }
      }
    }
    loadScore();
    return () => { mounted = false; };
  }, [user]);

  const handlePlayMove = async (selectedMove) => {
    playSoundEffect('click');
    setPlayerMove(selectedMove);

    try {
      const outcome = await api.playMove(user.username, selectedMove);
      setComputerMove(outcome.computerMove);
      setResult(outcome.result);
      setMessage(outcome.message);

      if (outcome.result === 'WIN') {
        playSoundEffect('win');
      } else if (outcome.result === 'LOSS') {
        playSoundEffect('loss');
      } else {
        playSoundEffect('tie');
      }

      if (outcome.currentScore) {
        setScore({
          wins: outcome.currentScore.wins,
          losses: outcome.currentScore.losses,
          ties: outcome.currentScore.ties
        });
      }
    } catch (err) {
      setMessage('Error: ' + err.message);
    }
  };

  const handleResetScore = async () => {
    playSoundEffect('click');
    const zeroScore = await api.resetScore(user.username);
    setScore(zeroScore);
    setPlayerMove(null);
    setComputerMove(null);
    setResult(null);
    setMessage('Scores have been reset!');
  };

  return (
    <div className="clean-card">
      <h2 className="arena-heading">Rock Paper Scissors Arena</h2>
      <p className="arena-sub">Welcome, {user.username}! Pick a move:</p>

      {/* Choice Buttons */}
      <div className="choices-row">
        <button className="choice-button" onClick={() => handlePlayMove('rock')}>
          <img src={MOVE_IMAGES.rock} alt="Rock" />
          <span>Rock</span>
        </button>

        <button className="choice-button" onClick={() => handlePlayMove('paper')}>
          <img src={MOVE_IMAGES.paper} alt="Paper" />
          <span>Paper</span>
        </button>

        <button className="choice-button" onClick={() => handlePlayMove('scissors')}>
          <img src={MOVE_IMAGES.scissors} alt="Scissors" />
          <span>Scissors</span>
        </button>
      </div>

      {/* Match Result Display */}
      <div className="match-box">
        <div className="match-status-text">{message}</div>

        {playerMove && computerMove && (
          <div className="match-showdown">
            <div className="combatant-item">
              <span className="combatant-label">You</span>
              <div className="combatant-img-wrap">
                <img src={MOVE_IMAGES[playerMove]} alt="You" />
              </div>
            </div>

            <span className="vs-text">VS</span>

            <div className="combatant-item">
              <span className="combatant-label">Computer</span>
              <div className="combatant-img-wrap">
                <img src={MOVE_IMAGES[computerMove]} alt="Computer" />
              </div>
            </div>
          </div>
        )}

        {result && (
          <div>
            <span className={`result-badge ${
              result === 'WIN' ? 'badge-win' : result === 'LOSS' ? 'badge-loss' : 'badge-tie'
            }`}>
              {result === 'WIN' ? 'You Win!' : result === 'LOSS' ? 'You Lose!' : 'Tie!'}
            </span>
          </div>
        )}
      </div>

      {/* Scoreboard */}
      <div className="scoreboard-row">
        <div className="score-box score-wins">
          <span className="score-number">{score.wins}</span>
          <span className="score-tag">Wins</span>
        </div>
        <div className="score-box score-losses">
          <span className="score-number">{score.losses}</span>
          <span className="score-tag">Losses</span>
        </div>
        <div className="score-box score-ties">
          <span className="score-number">{score.ties}</span>
          <span className="score-tag">Ties</span>
        </div>
      </div>

      {/* Controls */}
      <div className="bottom-controls">
        <button className="reset-score-btn" onClick={handleResetScore}>
          <img src="/photos/resetbutton.png" alt="Reset" />
          Reset Score
        </button>
      </div>
    </div>
  );
}
