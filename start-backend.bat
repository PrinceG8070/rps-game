@echo off
echo Starting Kotlin Spring Boot Backend with Gradle...
cd backend
call gradlew.bat bootRun --no-daemon
pause
