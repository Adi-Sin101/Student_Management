@echo off
echo ========================================
echo   PUSH TESTING BRANCH TO GITHUB
echo ========================================
echo.
echo Current directory:
cd
echo.
echo Current branch:
git branch
echo.
echo This will:
echo 1. Add all changes
echo 2. Commit changes
echo 3. Push testing branch to GitHub
echo.
pause

echo.
echo Step 1: Adding all changes...
git add .

echo.
echo Step 2: Committing changes...
git commit -m "build: downgrade to Spring Boot 3.2.2 and update documentation" -m "- Downgrade from Spring Boot 4.0.2 to 3.2.2 for test stability" -m "- Fix @DataJpaTest and @AutoConfigureMockMvc import issues" -m "- Replace all mvn commands with mvnw.cmd" -m "- Add comprehensive testing documentation and automated scripts"

echo.
echo Step 3: Pushing to GitHub...
git push -u origin testing/unit-integration-tests

echo.
echo ========================================
echo   DONE!
echo ========================================
echo.
echo Now you should see the testing branch on GitHub!
echo Go to: https://github.com/Adi-Sin101/Student_Management
echo.
pause
