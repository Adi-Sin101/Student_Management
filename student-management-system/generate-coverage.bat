@echo off
echo ================================================
echo   GENERATE CODE COVERAGE REPORT
echo ================================================
echo.

echo Running tests and generating JaCoCo coverage report...
echo This may take 3-5 minutes...
echo.

.\mvnw.cmd clean test jacoco:report

echo.
echo Opening coverage report in browser...
start target\site\jacoco\index.html

echo.
echo ================================================
echo   COVERAGE REPORT GENERATED!
echo ================================================
echo.
echo The coverage report should open in your browser.
echo Report location: target\site\jacoco\index.html
echo.
pause
