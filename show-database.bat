@echo off
title Rock Paper Scissors - MySQL Database Inspector
echo ===================================================
echo   Rock Paper Scissors - MySQL Database Inspector
echo ===================================================
echo.

set MYSQL_EXE="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

if not exist %MYSQL_EXE% (
    echo [ERROR] mysql.exe not found in standard MySQL 8.0 path.
    pause
    exit /b
)

echo Connecting to MySQL database 'rps_db' on localhost:3306...
echo.

%MYSQL_EXE% -u root -e "USE rps_db; SELECT '--- TABLES IN DATABASE ---' AS Info; SHOW TABLES; SELECT '--- REGISTERED USERS ---' AS Info; SELECT id, username, email, role, created_at FROM users; SELECT '--- SCOREBOARD ---' AS Info; SELECT * FROM game_scores; SELECT '--- RECENT ROUNDS ---' AS Info; SELECT id, user_id, player_move, computer_move, result, played_at FROM game_rounds ORDER BY played_at DESC LIMIT 10;" 2>nul

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [NOTICE] Could not connect to MySQL Server.
    echo 1. Ensure MySQL80 service is started (double-click 'start-mysql-admin.bat').
    echo 2. Ensure your Spring Boot backend has run at least once to auto-create the tables.
    echo.
    echo Opening interactive MySQL CLI prompt...
    %MYSQL_EXE% -u root
) else (
    echo.
    echo ===================================================
    echo To run custom SQL queries, entering interactive shell:
    echo (Type: USE rps_db; then any SELECT query, and exit by typing 'exit')
    echo ===================================================
    %MYSQL_EXE% -u root rps_db
)

pause
