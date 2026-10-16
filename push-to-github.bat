@echo off
title Push to GitHub - Rock Paper Scissors
echo ========================================================
echo   Pushing Rock Paper Scissors to GitHub (rps-game)
echo ========================================================
echo.

git remote remove origin 2>nul
git remote add origin https://github.com/PrinceG8070/rps-game.git

echo Checking remote origin...
git remote -v
echo.
echo Pushing branch 'main' to GitHub...
git push -u origin main

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================================
    echo SUCCESS! Your project has been pushed to:
    echo https://github.com/PrinceG8070/rps-game
    echo ========================================================
) else (
    echo.
    echo [NOTICE] If you got 'Repository not found':
    echo 1. Go to https://github.com/new in your browser.
    echo 2. Set repository name to: rps-game
    echo 3. Click 'Create repository' (leave README unchecked).
    echo 4. Run this script again!
)

pause
