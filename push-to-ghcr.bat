@echo off
title Build and Push to GitHub Container Registry (ghcr.io)
echo ========================================================
echo   Docker Build & Push to GitHub Container Registry
echo ========================================================
echo.

set USERNAME=princeg8070

echo To push to ghcr.io from local Docker CLI, you need a GitHub Personal Access Token (classic) with 'write:packages' permission.
echo.
set /p TOKEN="Enter your GitHub Personal Access Token (PAT): "

echo Logging in to ghcr.io...
echo %TOKEN% | docker login ghcr.io -u %USERNAME% --password-stdin

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to login to ghcr.io. Please check your token.
    pause
    exit /b
)

echo.
echo Building Backend image...
docker build -t ghcr.io/%USERNAME%/rps-backend:latest ./backend

echo Building Frontend image...
docker build -t ghcr.io/%USERNAME%/rps-frontend:latest ./frontend

echo.
echo Pushing Backend image to ghcr.io...
docker push ghcr.io/%USERNAME%/rps-backend:latest

echo Pushing Frontend image to ghcr.io...
docker push ghcr.io/%USERNAME%/rps-frontend:latest

echo.
echo ========================================================
echo SUCCESS! Docker images pushed to GitHub Container Registry:
echo https://github.com/PrinceG8070?tab=packages
echo ========================================================
pause
