import 'package:flutter/material.dart';
import 'api_service.dart';

void main() {
  runApp(const RpsApp());
}

class RpsApp extends StatelessWidget {
  const RpsApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Rock Paper Scissors Mobile',
      theme: ThemeData.dark().copyWith(
        primaryColor: const Color(0xFF6366F1),
        scaffoldBackgroundColor: const Color(0xFF0F172A),
      ),
      home: const GameScreen(),
    );
  }
}

class GameScreen extends StatefulWidget {
  const GameScreen({super.key});

  @override
  State<GameScreen> createState() => _GameScreenState();
}

class _GameScreenState extends State<GameScreen> {
  final String username = 'Player1';
  int wins = 0;
  int losses = 0;
  int ties = 0;
  String resultMessage = 'Choose your move!';
  String? playerPick;
  String? computerPick;
  bool loading = false;

  void play(String move) async {
    setState(() {
      loading = true;
      playerPick = move;
    });

    try {
      final res = await ApiService.playMove(username, move);
      setState(() {
        computerPick = res['computerMove'];
        final resType = res['result'];
        if (resType == 'WIN') {
          wins++;
          resultMessage = '🏆 You Win!';
        } else if (resType == 'LOSS') {
          losses++;
          resultMessage = '💥 You Lose!';
        } else {
          ties++;
          resultMessage = '🤝 It\'s a Tie!';
        }
      });
    } catch (e) {
      setState(() {
        resultMessage = 'Error connecting to Kotlin backend';
      });
    } finally {
      setState(() {
        loading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('RPS Arena'),
        backgroundColor: const Color(0xFF1E1B4B),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () async {
              await ApiService.resetScore(username);
              setState(() {
                wins = 0;
                losses = 0;
                ties = 0;
                resultMessage = 'Scores reset!';
              });
            },
          )
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          children: [
            Text(
              resultMessage,
              style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 20),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                _combatant('You', playerPick),
                const Text('VS', style: TextStyle(fontSize: 24, fontWeight: FontWeight.w900, color: Color(0xFF6366F1))),
                _combatant('Computer', computerPick),
              ],
            ),
            const Spacer(),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                _scoreCard('Wins', wins, Colors.green),
                _scoreCard('Losses', losses, Colors.red),
                _scoreCard('Ties', ties, Colors.orange),
              ],
            ),
            const Spacer(),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                _moveButton('Rock', '✊', () => play('rock')),
                _moveButton('Paper', '✋', () => play('paper')),
                _moveButton('Scissors', '✌️', () => play('scissors')),
              ],
            ),
            const SizedBox(height: 30),
          ],
        ),
      ),
    );
  }

  Widget _combatant(String title, String? move) {
    return Column(
      children: [
        Text(title, style: const TextStyle(color: Colors.grey)),
        const SizedBox(height: 8),
        Container(
          width: 80,
          height: 80,
          decoration: BoxDecoration(
            color: const Color(0xFF1E293B),
            shape: BoxShape.circle,
            border: Border.all(color: Colors.white24),
          ),
          alignment: Alignment.center,
          child: Text(
            move == 'rock' ? '✊' : move == 'paper' ? '✋' : move == 'scissors' ? '✌️' : '❓',
            style: const TextStyle(fontSize: 36),
          ),
        )
      ],
    );
  }

  Widget _scoreCard(String label, int val, Color col) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
      decoration: BoxDecoration(
        color: const Color(0xFF1E293B),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        children: [
          Text('$val', style: TextStyle(fontSize: 26, fontWeight: FontWeight.bold, color: col)),
          Text(label, style: const TextStyle(color: Colors.grey, fontSize: 12)),
        ],
      ),
    );
  }

  Widget _moveButton(String label, String emoji, VoidCallback onTap) {
    return ElevatedButton(
      style: ElevatedButton.styleFrom(
        backgroundColor: const Color(0xFF6366F1),
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      ),
      onPressed: loading ? null : onTap,
      child: Column(
        children: [
          Text(emoji, style: const TextStyle(fontSize: 32)),
          const SizedBox(height: 6),
          Text(label, style: const TextStyle(fontWeight: FontWeight.bold)),
        ],
      ),
    );
  }
}
