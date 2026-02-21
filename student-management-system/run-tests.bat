@echo off
echo ================================================
echo   STEP 2: RUN TESTS
echo ================================================
echo.

echo Running all tests with Maven Wrapper...
echo This may take 3-5 minutes...
echo.

.\mvnw.cmd clean test

echo.
echo ================================================
echo   TEST EXECUTION COMPLETE!
echo ================================================
echo.
echo Check the output above for test results.
echo If tests passed: You'll see [INFO] BUILD SUCCESS
echo If tests failed: Review the error messages
echo.
pause
