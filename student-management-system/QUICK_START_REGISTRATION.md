# QUICK START: Access Registration Feature

## The Issue
The application code was updated with registration feature, but your running instance is still using the OLD compiled code. You need to **recompile and restart** the application.

## The Solution (Copy-Paste These Commands)

### For PowerShell (Recommended):

```powershell
# 1. Kill old Java process
taskkill /F /IM java.exe 2>$null; Start-Sleep -Seconds 2

# 2. Go to project folder
cd "E:\3-2\LAB\SOFTWARE\lectures\lab3\student-management-system"

# 3. Clean rebuild
.\mvnw.cmd clean package -DskipTests

# 4. Run the new application
java -jar target\student-management-system-0.0.1-SNAPSHOT.jar
```

### Step-by-Step:

1. **Open PowerShell** and paste the commands above
2. **Wait 30-60 seconds** until you see:
   ```
   Tomcat initialized with port 9090
   ```
3. **Open your browser** and go to:
   ```
   http://localhost:9090/login
   ```
4. **Look for "Register here"** link below the Sign In button
5. **Click it** and you'll see the registration role choice page

## What You Should See

### Login Page
- Email/Password login form
- Link that says "Don't have an account? Register here"
- Demo credentials hint at bottom

### After Clicking "Register here"
- Two role cards: "Student" and "Teacher"
- Each card is clickable
- Link back to login

### After Clicking "Student" or "Teacher"
- Registration form with fields:
  - Full Name
  - Roll Number (for student only)
  - Email
  - Password
  - Department (optional dropdown)
- Error/Success messages

## Direct URLs (If Links Don't Work)

Try these URLs directly in your browser:
- Registration choice: `http://localhost:9090/register`
- Student registration: `http://localhost:9090/register/student`
- Teacher registration: `http://localhost:9090/register/teacher`

## If It Still Doesn't Work

1. **Clear browser cache**: Ctrl+Shift+Delete
2. **Try incognito mode**: Ctrl+Shift+N
3. **Check application is running**: Look for "Tomcat initialized with port 9090" in console
4. **Check browser console**: Press F12, look for error messages

## Test Registration Data

### Register as Student:
- Name: John Doe
- Roll: TEST001
- Email: john.doe@student.edu
- Password: test123456
- Department: Computer Science

### Register as Teacher:
- Name: Dr. Jane Smith
- Email: jane.smith@university.edu
- Password: test123456
- Department: Computer Science

After registration, you can login with your registered email and password.

---

**The key is: Your new code is written, but the application needs to be RECOMPILED and RESTARTED to use it.**

Run the commands above and it should work! ✅
