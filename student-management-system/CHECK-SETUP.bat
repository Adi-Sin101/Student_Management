@echo off
title System Check - Student Management System
color 0B

echo ================================================
echo   ENVIRONMENT DIAGNOSTIC CHECK
echo ================================================
echo.

REM Check current directory
echo [1] Current Directory:
cd
echo.

REM Check if mvnw.cmd exists
echo [2] Checking for Maven Wrapper:
if exist mvnw.cmd (
    echo    ✓ mvnw.cmd found
) else (
    echo    ✗ mvnw.cmd NOT found - you're in the wrong directory!
)
echo.

REM Check if pom.xml exists
echo [3] Checking for pom.xml:
if exist pom.xml (
    echo    ✓ pom.xml found
) else (
    echo    ✗ pom.xml NOT found - you're in the wrong directory!
)
echo.

REM Check Java version
echo [4] Checking Java Installation:
java -version 2>&1 | findstr /i "version"
if %ERRORLEVEL% EQU 0 (
    echo    ✓ Java is installed
) else (
    echo    ✗ Java NOT found - please install Java 17+
)
echo.

REM Check Git
echo [5] Checking Git Installation:
git --version 2>nul
if %ERRORLEVEL% EQU 0 (
    echo    ✓ Git is installed
) else (
    echo    ✗ Git NOT found - may need to install
)
echo.

REM Check current Git branch
echo [6] Current Git Branch:
git branch 2>nul | findstr /C:"*"
echo.

REM Check if src folder exists
echo [7] Checking Project Structure:
if exist src\main\java (
    echo    ✓ src\main\java found
) else (
    echo    ✗ src\main\java NOT found
)
if exist src\test\java (
    echo    ✓ src\test\java found
) else (
    echo    ✗ src\test\java NOT found
)
echo.

REM List batch files
echo [8] Available Batch Files:
dir /b *.bat
echo.

echo ================================================
echo   DIAGNOSTIC COMPLETE
echo ================================================
echo.
echo If everything shows ✓, you can run tests.
echo If you see ✗, fix those issues first.
echo.
echo To run tests, double-click: TEST-RUNNER.bat
echo.
pause
