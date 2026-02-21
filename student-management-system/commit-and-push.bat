@echo off
echo ================================================
echo   STEP 1: COMMIT AND PUSH CHANGES
echo ================================================
echo.

echo Checking current status...
git status
echo.

echo Adding all changes...
git add .
echo.

echo Committing changes...
git commit -m "build: downgrade to Spring Boot 3.2.2 and update documentation" -m "- Downgrade from Spring Boot 4.0.2 to 3.2.2 for test stability" -m "- Fix @DataJpaTest and @AutoConfigureMockMvc import issues" -m "- Replace all mvn commands with .\mvnw.cmd in documentation" -m "- Add MAVEN_FIX_AND_TESTING_GUIDE.md with comprehensive instructions" -m "- Update ACTION_PLAN.md with Maven wrapper commands"
echo.

echo Pushing to remote...
git push -u origin testing/unit-integration-tests
echo.

echo ================================================
echo   COMMIT AND PUSH COMPLETE!
echo ================================================
echo.
echo Next step: Run run-tests.bat to execute tests
echo.
pause
