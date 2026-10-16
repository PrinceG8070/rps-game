# Rock Paper Scissors - Full Stack Enterprise Application

This repository contains the full-stack Rock Paper Scissors application built according to the roadmap:
1. **Kotlin Backend**: Spring Boot 3.4.3 REST API with Gradle
2. **Gradle**: Upgraded to 8.14.6 with Java 24 support
3. **MySQL**: Local MySQL Server 8.0 on port 3306 (`rps_db`), user `root`, no password
4. **Tested REST APIs**: Comprehensive JUnit 5 unit and integration tests passing
5. **React Frontend**: Modern React 19 + Vite responsive web application with glassmorphism, dynamic animations, sound FX, and light/dark theme
6. **Flutter Mobile**: Cross-platform Flutter mobile client in `flutter_app/`
7. **Firebase**: Push notifications & Firebase Admin SDK integration in `firebase/`

---

## Quick Start

### 1. Start MySQL Server (Port 3306)
Double-click `start-mysql-admin.bat` (or open an elevated terminal and run `net start MySQL80`).

### 2. Start Kotlin Backend (Port 8080)
Double-click `start-backend.bat` or run:
```bash
cd backend
./gradlew bootRun
```
- Swagger API Docs: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- API Base: `http://localhost:8080/api`

### 3. Start React Frontend (Port 5173)
Double-click `start-frontend.bat` or run:
```bash
cd frontend
npm run dev
```
Open [http://localhost:5173](http://localhost:5173) in your browser.

---

## Tested REST Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register user account |
| `POST` | `/api/auth/login` | Authenticate user |
| `POST` | `/api/game/play` | Play round (ROCK, PAPER, SCISSORS) |
| `GET` | `/api/game/score` | Get user score |
| `POST` | `/api/game/reset` | Reset user score |
| `GET` | `/api/game/history` | Paginated match history |
| `GET` | `/api/game/leaderboard` | Top players ranking |
