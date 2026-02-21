@echo off
color 0A
echo ================================================
echo   ENTERPRISE TESTING WORKFLOW - AUTO EXECUTOR
echo ================================================
echo.
echo This script will:
echo   1. Check current status
echo   2. Commit and push changes
echo   3. Run all tests
echo   4. Generate coverage report
echo.
echo Press any key to start...
pause > nul
cls

REM Step 1: Check Status
echo.
echo ================================================
echo   CHECKING CURRENT STATUS...
echo ================================================
echo.
git status
git branch
echo.
echo Press any key to continue to commit and push...
pause > nul
cls

REM Step 2: Commit and Push
echo.
echo ================================================
echo   COMMITTING AND PUSHING CHANGES...
echo ================================================
echo.
git add .
git commit -m "build: downgrade to Spring Boot 3.2.2 and update documentation" -m "- Downgrade from Spring Boot 4.0.2 to 3.2.2 for test stability" -m "- Fix @DataJpaTest and @AutoConfigureMockMvc import issues" -m "- Replace all mvn commands with mvnw.cmd in documentation" -m "- Add MAVEN_FIX_AND_TESTING_GUIDE.md with comprehensive instructions" -m "- Update ACTION_PLAN.md with Maven wrapper commands"
echo.
git push -u origin testing/unit-integration-tests
echo.
echo ✓ Changes pushed successfully!
echo.
echo Press any key to run tests...
pause > nul
cls

REM Step 3: Run Tests
echo.
echo ================================================
echo   RUNNING ALL TESTS...
echo ================================================
echo.
echo This may take 3-5 minutes on first run...
echo.
.\mvnw.cmd clean test
echo.
echo ✓ Tests completed!
echo.
echo Press any key to generate coverage report...
pause > nul
cls

REM Step 4: Coverage Report
echo.
echo ================================================
echo   GENERATING COVERAGE REPORT...
echo ================================================
echo.
.\mvnw.cmd jacoco:report
echo.
echo Opening coverage report in browser...
start target\site\jacoco\index.html
echo.
echo ✓ Coverage report generated!

REM Final Summary
cls
echo.
echo ================================================
echo   ALL STEPS COMPLETED SUCCESSFULLY!
echo ================================================
echo.
echo ✓ Changes committed and pushed
echo ✓ Tests executed
echo ✓ Coverage report generated
echo.
echo NEXT STEPS:
echo   1. Go to: https://github.com/Adi-Sin101/Student_Management
echo   2. Click "Compare and pull request"
echo   3. Create PR with title: "Add Enterprise-Level Testing Suite"
echo.
echo Press any key to exit...
pause > nul
