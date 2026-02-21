@echo off
echo ================================================
echo   STEP 2: RUN TESTS
echo ================================================
echo.
echo Current directory:
cd
echo.
echo Checking for Maven Wrapper...
if exist mvnw.cmd (
    echo ✓ Maven Wrapper found!
) else (
    echo ✗ ERROR: mvnw.cmd not found!
    echo Make sure you are in the correct directory.
    pause
    exit /b 1
)
echo.
echo Running all tests with Maven Wrapper...
echo This may take 3-5 minutes on first run...
echo Please wait - downloading dependencies if needed...
echo.
echo Press Ctrl+C to cancel if needed.
echo.

call mvnw.cmd clean test

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ================================================
    echo   ✓ TEST EXECUTION COMPLETE!
    echo ================================================
    echo.
    echo Tests finished successfully!
) else (
    echo.
    echo ================================================
    echo   ✗ TEST EXECUTION FAILED!
    echo ================================================
    echo.
    echo Error code: %ERRORLEVEL%
    echo Check the output above for error details.
)
echo.
echo Press any key to close this window...
pause > nul
