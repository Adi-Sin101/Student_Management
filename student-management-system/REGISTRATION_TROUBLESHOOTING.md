# Registration Feature Troubleshooting Guide

## Issue: Registration Page Not Accessible

### Root Causes & Solutions

#### 1. Application Not Recompiled
**Problem**: You created new controller files but the application is still running old code
**Solution**: 
```bash
# Kill all Java processes
taskkill /F /IM java.exe

# Clean rebuild
cd E:\3-2\LAB\SOFTWARE\lectures\lab3\student-management-system
.\mvnw.cmd clean compile
.\mvnw.cmd package -DskipTests

# Run the new jar
java -jar target\student-management-system-0.0.1-SNAPSHOT.jar
```

#### 2. Old Process Still Listening on Port
**Problem**: Previous application instance still running on 9090
**Solution**:
```powershell
# Check what's listening on port 9090
netstat -ano | findstr 9090

# Kill all Java processes
Get-Process java* | Stop-Process -Force

# Wait and restart
Start-Sleep -Seconds 3
java -jar target\student-management-system-0.0.1-SNAPSHOT.jar
```

#### 3. Port 9090 Conflict
**Problem**: Another application using port 9090
**Solution**: Change port in `application.properties`
```properties
server.port=8080
```

#### 4. Controller Not Recognized
**Problem**: RegistrationController not being picked up by Spring
**Verification**:
- Check file exists: `src/main/java/com/example/studentmanagementsystem/controller/RegistrationController.java`
- Check @Controller and @RequestMapping annotations
- Check class is in correct package: `com.example.studentmanagementsystem.controller`

### Step-by-Step Fix

**Step 1: Verify Files Exist**
```
✓ src/main/java/com/example/studentmanagementsystem/controller/RegistrationController.java
✓ src/main/resources/templates/register/choice.html
✓ src/main/resources/templates/register/student.html
✓ src/main/resources/templates/register/teacher.html
```

**Step 2: Kill Old Process**
```powershell
taskkill /F /IM java.exe 2>$null
Start-Sleep -Seconds 3
```

**Step 3: Clean Rebuild**
```bash
cd E:\3-2\LAB\SOFTWARE\lectures\lab3\student-management-system
.\mvnw.cmd clean package -DskipTests
```

**Step 4: Run Application**
```bash
java -jar target\student-management-system-0.0.1-SNAPSHOT.jar
```

**Step 5: Wait for Startup**
- Wait 30-60 seconds for application to fully start
- Watch for message: "Tomcat initialized with port"

**Step 6: Test Registration**
```
Browser URL: http://localhost:9090/login
Click: "Register here" button
OR Direct: http://localhost:9090/register
```

### Expected Behavior

✅ When accessing `/register`:
- You should see a page with two role cards (Student/Teacher)
- Each card is clickable

✅ When clicking "Student" or "Teacher":
- Redirects to respective registration form
- Form appears with fields for name, email, password, department

✅ When clicking "Register here" on login page:
- Redirects to `/register`
- Shows role choice cards

### Debug: Check Application Logs

Look for these messages in console output:
```
INFO ... Started StudentManagementSystemApplication
INFO ... Tomcat initialized with port 9090
INFO ... DispatcherServlet 'dispatcherServlet' registered with completeSet...
```

If you see these, the application is running correctly.

### URL Mapping Verification

The application should have these routes:
```
GET /register              → Shows role choice (register/choice.html)
GET /register/student      → Shows student form (register/student.html)
POST /register/student     → Processes student registration
GET /register/teacher      → Shows teacher form (register/teacher.html)
POST /register/teacher     → Processes teacher registration
```

### Final Check

1. Open http://localhost:9090 in browser
2. You should be redirected to http://localhost:9090/login
3. On login page, look for "Register here" link
4. Click it and you should be taken to http://localhost:9090/register
5. See two role cards (Student/Teacher)

If you still can't access registration:
- Try hard refresh: Ctrl+Shift+Del (clear cache) or Ctrl+F5
- Try incognito/private browsing mode
- Check browser console (F12) for errors
- Check Java console for stack traces

### Application Restart Command (Complete)

```powershell
# Complete restart procedure
taskkill /F /IM java.exe 2>$null
Start-Sleep -Seconds 5
cd "E:\3-2\LAB\SOFTWARE\lectures\lab3\student-management-system"
.\mvnw.cmd clean package -DskipTests -q
java -jar target\student-management-system-0.0.1-SNAPSHOT.jar
```

Wait about 1 minute for full startup, then visit:
**http://localhost:9090/login**
