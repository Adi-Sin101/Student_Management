@echo off
echo ===== GIT STATUS =====
git status
echo.
echo ===== CURRENT BRANCH =====
git branch
echo.
echo ===== LAST COMMIT =====
git log --oneline -1
echo.
echo ===== REMOTE STATUS =====
git remote -v
pause
