import 'dart:convert';
import 'package:http/http.dart' as http;

class ApiService {
  // Use 10.0.2.2 for Android emulator to reach localhost on Windows host
  static const String baseUrl = 'http://10.0.2.2:8080/api';

  static Future<Map<String, dynamic>> login(String username, String password) async {
    final response = await http.post(
      Uri.parse('$baseUrl/auth/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'username': username, 'password': password}),
    );
    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    }
    throw Exception('Login failed: ${response.body}');
  }

  static Future<Map<String, dynamic>> playMove(String username, String move) async {
    final response = await http.post(
      Uri.parse('$baseUrl/game/play'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'username': username,
        'playerMove': move.toUpperCase(),
      }),
    );
    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    }
    throw Exception('Game play failed: ${response.body}');
  }

  static Future<Map<String, dynamic>> getScore(String username) async {
    final response = await http.get(Uri.parse('$baseUrl/game/score?username=$username'));
    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return data['data'] ?? {'wins': 0, 'losses': 0, 'ties': 0};
    }
    return {'wins': 0, 'losses': 0, 'ties': 0};
  }

  static Future<void> resetScore(String username) async {
    await http.post(
      Uri.parse('$baseUrl/game/reset'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'username': username}),
    );
  }
}
