# Complete Master Guide: Rock Paper Scissors Full-Stack System
### Kotlin Spring Boot &bull; MySQL 8.0 &bull; React 19 &bull; Flutter &bull; Firebase

This master guide covers the complete step-by-step operational instructions, architectural code mapping, database queries, and API logic for the entire project.

---

## Table of Contents
1. [Project Directory & File Map](#1-project-directory--file-map)
2. [Step-by-Step Process to Run the Entire System](#2-step-by-step-process-to-run-the-entire-system)
3. [Database Guide: How to View, Inspect & Query MySQL](#3-database-guide-how-to-view-inspect--query-mysql)
4. [Where the API Logic Lives (File-by-File Code Breakdown)](#4-where-the-api-logic-lives)
5. [Backend-to-Database Connection Code](#5-backend-to-database-connection-code)
6. [Frontend Architecture & Connection to Backend](#6-frontend-architecture--connection-to-backend)
7. [Mobile (Flutter) & Firebase Code Locations](#7-mobile-flutter--firebase-code-locations)
8. [Quick Troubleshooting & Verification Commands](#8-quick-troubleshooting--verification-commands)

---

## 1. Project Directory & File Map

```text
web_dev/
│
├── start-mysql-admin.bat            # 1-Click Administrator UAC script to start MySQL80 service
├── start-backend.bat                # 1-Click launcher for Kotlin Spring Boot backend (port 8080)
├── start-frontend.bat               # 1-Click launcher for React 19 Vite dev server (port 5173)
├── database_schema.sql              # Raw MySQL 8.0 DDL & seed data script
├── README.md                        # Quickstart documentation
│
├── backend/                         # Step 1: Kotlin + Spring Boot Enterprise Backend
│   ├── build.gradle.kts             # Gradle build file (Java 24 + Kotlin 2.1 + Spring Boot 3.4.3)
│   ├── gradlew.bat                  # Gradle Wrapper executable (upgraded to v8.14.6)
│   ├── gradle/wrapper/
│   │   └── gradle-wrapper.properties
│   └── src/
│       ├── main/
│       │   ├── kotlin/com/example/rps/
│       │   │   ├── Application.kt                   # Spring Boot entry point (@SpringBootApplication)
│       │   │   ├── config/                          # CORS, Security, Swagger & Firebase configs
│       │   │   ├── controller/                      # REST Controllers (API endpoints)
│       │   │   ├── service/                         # Business logic & game rule engines
│       │   │   ├── model/                           # JPA Database Entities (tables)
│       │   │   ├── repository/                      # Spring Data JPA interfaces (SQL queries)
│       │   │   ├── dto/                             # Request/Response data transfer objects
│       │   │   └── exception/                       # Global error handling
│       │   └── resources/
│       │       └── application.properties           # Database connection & server port settings
│       └── test/                                    # Unit & integration tests
│
├── frontend/                        # Step 5: Modern React 19 + Vite Web Application
│   ├── package.json                 # Dependencies: React 19, Vite, Canvas-Confetti, Lucide
│   ├── vite.config.js               # Proxy setup (/api -> http://localhost:8080)
│   ├── index.html                   # HTML template with Google Fonts (Outfit & Plus Jakarta Sans)
│   ├── public/photos/               # Rock, Paper, Scissors assets, bulb switch, reset icons
│   └── src/
│       ├── main.jsx                 # React root render
│       ├── App.jsx                  # Main routing, theme management & session state
│       ├── index.css                # Glassmorphism design system, dark/light theme, animations
│       ├── services/
│       │   └── api.js               # REST client & Web Audio API sound synthesizer
│       └── components/
│           ├── Navbar.jsx           # Brand header, online indicator & bulb theme switch
│           ├── Login.jsx            # User login form with guest quick-play
│           ├── Register.jsx         # User registration form
│           ├── Game.jsx             # Main interactive arena, animated showdown & scoreboard
│           ├── LeaderboardModal.jsx # Global top-players leaderboard popup
│           └── HistoryModal.jsx     # Match-by-match round history popup
│
├── flutter_app/                     # Step 6: Flutter Cross-Platform Mobile Client
│   ├── pubspec.yaml                 # Flutter dependencies & assets
│   └── lib/
│       ├── main.dart                # Mobile game arena UI & theme
│       └── api_service.dart         # HTTP client connecting mobile to Kotlin backend
│
└── firebase/                        # Step 7: Push Notifications & Firebase Admin
    └── setup_guide.md               # Guide to configure Firebase Cloud Messaging (FCM)
```

---

## 2. Step-by-Step Process to Run the Entire System

### Step 1: Start the MySQL Database Service
1. Open Windows Explorer in `web_dev`.
2. Right-click or double-click **`start-mysql-admin.bat`**.
3. Click **"Yes"** on the Windows UAC Administrator prompt.
4. The script runs `net start MySQL80` and ensures MySQL is listening on port `3306`.

### Step 2: Start the Kotlin Spring Boot Backend
1. Double-click **`start-backend.bat`** (or open terminal in `backend/` and run `./gradlew bootRun`).
2. When launched:
   - It connects to MySQL at `localhost:3306`.
   - It automatically creates the `rps_db` database if it doesn't exist.
   - Hibernate auto-generates all 4 tables (`users`, `game_scores`, `game_rounds`, `login_logs`).
   - The server starts on **`http://localhost:8080`**.
   - Interactive Swagger API documentation becomes available at:  
     👉 **`http://localhost:8080/swagger-ui.html`**

### Step 3: Start the React Frontend
1. Double-click **`start-frontend.bat`** (or open terminal in `frontend/` and run `npm run dev`).
2. The Vite dev server starts instantly on **`http://localhost:5173`**.
3. Open `http://localhost:5173` in any browser:
   - You can click **"🎮 Quick Play as Guest"** to start playing immediately.
   - Or click **"Register here"** to create a persistent account.
   - Click the **lightbulb switch** in the upper right to toggle between Dark and Light mode.
   - Click **"🏆 Top Players"** to view the live database leaderboard.
   - Click **"📜 History"** to review your past match rounds.

---

## 3. Database Guide: How to View, Inspect & Query MySQL

### Option A: Using the MySQL Command Line Client
1. Open PowerShell or Command Prompt.
2. Connect to MySQL as `root`:
   ```bash
   & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root
   ```
   *(If prompted for a password and yours is empty, simply press Enter)*.

3. Select the application database:
   ```sql
   USE rps_db;
   ```

4. View all tables created by the backend:
   ```sql
   SHOW TABLES;
   ```
   *Output:*
   ```text
   +------------------+
   | Tables_in_rps_db |
   +------------------+
   | game_rounds      |
   | game_scores      |
   | login_logs       |
   | users            |
   +------------------+
   ```

5. Query registered users:
   ```sql
   SELECT id, username, email, role, created_at FROM users;
   ```

6. Query live player scores and win counts:
   ```sql
   SELECT u.username, s.wins, s.losses, s.ties, s.updated_at 
   FROM game_scores s 
   JOIN users u ON s.user_id = u.id 
   ORDER BY s.wins DESC;
   ```

7. Query recent game match history:
   ```sql
   SELECT u.username, r.player_move, r.computer_move, r.result, r.played_at 
   FROM game_rounds r 
   JOIN users u ON r.user_id = u.id 
   ORDER BY r.played_at DESC 
   LIMIT 10;
   ```

8. Query login audit activity:
   ```sql
   SELECT * FROM login_logs ORDER BY timestamp DESC LIMIT 10;
   ```

---

### Option B: Using MySQL Workbench (GUI)
1. Launch **MySQL Workbench 8.0** from your Start Menu.
2. Click on your local instance (`localhost:3306`, user: `root`).
3. In the left **Schemas** sidebar, expand **`rps_db`** -> **Tables**.
4. Right-click any table (`game_scores`, `users`, `game_rounds`) and select **Select Rows - Limit 1000**.

---

## 4. Where the API Logic Lives

All REST endpoints, request validators, and gameplay algorithms live in the Kotlin backend:

### 4.1 Authentication & User Management
- **Controller File**: [`backend/src/main/kotlin/com/example/rps/controller/AuthController.kt`](file:///c:/Users/Mr.Gupta/OneDrive/Apps/Desktop/web_dev/backend/src/main/kotlin/com/example/rps/controller/AuthController.kt)
  - `POST /api/auth/register`: Validates user inputs, hashes password with BCrypt, initializes a blank `GameScore` record with 0 wins/losses/ties.
  - `POST /api/auth/login`: Verifies BCrypt password match, generates a JWT session token, and records a row in `login_logs` with IP address and timestamp.
  - `GET /api/auth/logs`: Returns paginated login audit history.
- **Service Implementation**: [`backend/src/main/kotlin/com/example/rps/service/AuthService.kt`](file:///c:/Users/Mr.Gupta/OneDrive/Apps/Desktop/web_dev/backend/src/main/kotlin/com/example/rps/service/AuthService.kt)

### 4.2 Game Mechanics & AI Move Resolver
- **Controller File**: [`backend/src/main/kotlin/com/example/rps/controller/GameController.kt`](file:///c:/Users/Mr.Gupta/OneDrive/Apps/Desktop/web_dev/backend/src/main/kotlin/com/example/rps/controller/GameController.kt)
  - `POST /api/game/play`: Submits player move, calculates computer move, determines winner, saves match record, updates score table.
  - `GET /api/game/score?username={name}`: Retrieves current win/loss/tie tally.
  - `POST /api/game/reset`: Resets user score to zero in the database.
  - `GET /api/game/history?username={name}`: Returns paginated list of past game rounds.
  - `GET /api/game/leaderboard`: Returns top players sorted by total wins.

- **Game Rule Engine Logic** in [`backend/src/main/kotlin/com/example/rps/service/GameService.kt`](file:///c:/Users/Mr.Gupta/OneDrive/Apps/Desktop/web_dev/backend/src/main/kotlin/com/example/rps/service/GameService.kt):
  ```kotlin
  // Computer AI move generator:
  val computerMove = Move.values().random()

  // Outcome decision:
  val result = when {
      playerMove == computerMove -> GameResult.TIE
      (playerMove == Move.ROCK && computerMove == Move.SCISSORS) ||
      (playerMove == Move.PAPER && computerMove == Move.ROCK) ||
      (playerMove == Move.SCISSORS && computerMove == Move.PAPER) -> GameResult.WIN
      else -> GameResult.LOSS
  }

  // Database persistence:
  gameRoundRepository.save(GameRound(user = user, playerMove = playerMove, computerMove = computerMove, result = result))
  // Update scoreboard entity:
  when (result) {
      GameResult.WIN -> score.wins++
      GameResult.LOSS -> score.losses++
      GameResult.TIE -> score.ties++
  }
  gameScoreRepository.save(score)
  ```

---

## 5. Backend-to-Database Connection Code

### 5.1 Configuration File
Located at: [`backend/src/main/resources/application.properties`](file:///c:/Users/Mr.Gupta/OneDrive/Apps/Desktop/web_dev/backend/src/main/resources/application.properties)

```properties
# MySQL Connection String with Auto Database Creation
spring.datasource.url=jdbc:mysql://localhost:3306/rps_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate ORM Table Generation
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.open-in-view=false
```

### 5.2 Kotlin JPA Entity Classes (Database Tables)
All entities reside in [`backend/src/main/kotlin/com/example/rps/model/`](file:///c:/Users/Mr.Gupta/OneDrive/Apps/Desktop/web_dev/backend/src/main/kotlin/com/example/rps/model/):
- **`User.kt`**: Maps to table `users`. Annotations: `@Entity`, `@Table(name = "users")`.
- **`GameScore.kt`**: Maps to table `game_scores`. Has a `@OneToOne` relationship with `User`.
- **`GameRound.kt`**: Maps to table `game_rounds`. Has a `@ManyToOne` relationship with `User`.
- **`LoginLog.kt`**: Maps to table `login_logs`. Records security audit entries.

### 5.3 Spring Data JPA Repositories (SQL Interface)
Located at: [`backend/src/main/kotlin/com/example/rps/repository/Repositories.kt`](file:///c:/Users/Mr.Gupta/OneDrive/Apps/Desktop/web_dev/backend/src/main/kotlin/com/example/rps/repository/Repositories.kt)

Spring Data JPA generates real SQL queries at runtime without requiring manual SQL strings:
```kotlin
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): Optional<User>
    fun existsByUsername(username: String): Boolean
}

interface GameScoreRepository : JpaRepository<GameScore, Long> {
    fun findByUser(user: User): Optional<GameScore>
    fun findByUserUsername(username: String): Optional<GameScore>
    
    // Custom query for leaderboard ranking
    @Query("SELECT s FROM GameScore s ORDER BY s.wins DESC")
    fun findTopPlayers(pageable: Pageable): List<GameScore>
}

interface GameRoundRepository : JpaRepository<GameRound, Long> {
    fun findByUserUsernameOrderByPlayedAtDesc(username: String, pageable: Pageable): Page<GameRound>
}
```

---

## 6. Frontend Architecture & Connection to Backend

### 6.1 API Bridge Layer
Located at: [`frontend/src/services/api.js`](file:///c:/Users/Mr.Gupta/OneDrive/Apps/Desktop/web_dev/frontend/src/services/api.js)

1. **`healthCheck()`**: Automatically checks if `http://localhost:8080/v3/api-docs` is reachable to update the green **"Spring Boot Online"** pill in the navigation header.
2. **`playMove(username, move)`**: Dispatches an HTTP `POST` to `/api/game/play` with `{ username, playerMove: 'ROCK' }`.
3. **`getLeaderboard()`**: Dispatches HTTP `GET` to `/api/game/leaderboard` to populate the Top Players modal.
4. **`playSoundEffect(type)`**: Synthesizes custom 8-bit arcade audio tones using the browser's native **Web Audio API** (`win` arpeggio, `loss` sawtone, `tie` chime, `click` blip) with no external audio file dependencies.

### 6.2 UI Components Breakdown
- [`App.jsx`](file:///c:/Users/Mr.Gupta/OneDrive/Apps/Desktop/web_dev/frontend/src/App.jsx): Main router & session state coordinator.
- [`Navbar.jsx`](file:///c:/Users/Mr.Gupta/OneDrive/Apps/Desktop/web_dev/frontend/src/components/Navbar.jsx): Controls the interactive theme toggle bulb (`bulb-on.webp` vs `bulb-off.webp`), online status, and logout.
- [`Game.jsx`](file:///c:/Users/Mr.Gupta/OneDrive/Apps/Desktop/web_dev/frontend/src/components/Game.jsx): Handles move animations, hand shaking, battle reveal, winner confetti explosion, and live scoreboard update.
- [`index.css`](file:///c:/Users/Mr.Gupta/OneDrive/Apps/Desktop/web_dev/frontend/src/index.css): Responsive layout, glassmorphic frosted glass panels, glowing borders, and dark/light color tokens.

---

## 7. Mobile (Flutter) & Firebase Code Locations

### 7.1 Flutter Mobile Client
- **Configuration**: [`flutter_app/pubspec.yaml`](file:///c:/Users/Mr.Gupta/OneDrive/Apps/Desktop/web_dev/flutter_app/pubspec.yaml)
- **UI Screen**: [`flutter_app/lib/main.dart`](file:///c:/Users/Mr.Gupta/OneDrive/Apps/Desktop/web_dev/flutter_app/lib/main.dart)
- **Backend API Service**: [`flutter_app/lib/api_service.dart`](file:///c:/Users/Mr.Gupta/OneDrive/Apps/Desktop/web_dev/flutter_app/lib/api_service.dart)
  - Uses `http://10.0.2.2:8080/api` (standard Android Emulator loopback to access Windows host).

### 7.2 Firebase Push Notification Integration
- **Setup Guide**: [`firebase/setup_guide.md`](file:///c:/Users/Mr.Gupta/OneDrive/Apps/Desktop/web_dev/firebase/setup_guide.md)
- **Backend Service**: [`backend/src/main/kotlin/com/example/rps/service/FirebaseService.kt`](file:///c:/Users/Mr.Gupta/OneDrive/Apps/Desktop/web_dev/backend/src/main/kotlin/com/example/rps/service/FirebaseService.kt)
- **Push Controller**: [`backend/src/main/kotlin/com/example/rps/controller/NotificationController.kt`](file:///c:/Users/Mr.Gupta/OneDrive/Apps/Desktop/web_dev/backend/src/main/kotlin/com/example/rps/controller/NotificationController.kt)

---

## 8. Quick Troubleshooting & Verification Commands

| Action | Command / Method |
|---|---|
| **Check MySQL Port 3306** | `Test-NetConnection -ComputerName localhost -Port 3306` in PowerShell |
| **Start MySQL Service** | Double-click `start-mysql-admin.bat` or run `net start MySQL80` as Admin |
| **Verify Backend Build** | `cd backend && ./gradlew test` |
| **Launch Backend Server** | Double-click `start-backend.bat` or run `cd backend && ./gradlew bootRun` |
| **View Swagger REST Docs** | Open browser to `http://localhost:8080/swagger-ui.html` |
| **Run Frontend Dev Server** | Double-click `start-frontend.bat` or run `cd frontend && npm run dev` |
| **Open Application** | Open browser to `http://localhost:5173` |
