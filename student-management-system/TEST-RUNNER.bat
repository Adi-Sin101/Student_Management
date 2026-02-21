@echo off
title Running Tests - Student Management System
color 0A

echo ================================================
echo   MAVEN WRAPPER TEST RUNNER
echo ================================================
echo.
echo Current Directory: %CD%
echo.

REM Check if we're in the right directory
if not exist "mvnw.cmd" (
    echo ERROR: mvnw.cmd not found!
    echo.
    echo You are currently in: %CD%
    echo.
    echo Please navigate to:
    echo E:\3-2\LAB\SOFTWARE\lectures\lab3\student-management-system
    echo.
    echo Then run this file again.
    echo.
    pause
    exit /b 1
)

echo ✓ Found Maven Wrapper (mvnw.cmd)
echo.
echo Starting test execution...
echo This will take 3-5 minutes on first run.
echo.
echo ================================================
echo.

REM Run the tests using call to capture errors
call mvnw.cmd clean test

REM Check result
if %ERRORLEVEL% EQU 0 (
    color 0A
    echo.
    echo ================================================
    echo   SUCCESS! All tests completed
    echo ================================================
    echo.
    echo Scroll up to see detailed test results.
    echo Look for lines like:
    echo   [INFO] Tests run: XXX, Failures: 0, Errors: 0
    echo.
) else (
    color 0C
    echo.
    echo ================================================
    echo   TESTS FAILED OR ERROR OCCURRED
    echo ================================================
    echo.
    echo Error Code: %ERRORLEVEL%
    echo.
    echo Common issues:
    echo   1. Java not installed (need Java 17+)
    echo   2. Internet connection issue (downloading dependencies)
    echo   3. Test compilation errors
    echo.
    echo Scroll up to see the error details.
    echo.
)

echo.
echo ================================================
echo   Press any key to close this window
echo ================================================
pause > nul
