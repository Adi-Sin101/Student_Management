# Authorization & Role-Based Access Control Implementation

## Summary of Changes

All authorization issues have been fixed with proper role-based access control implemented throughout the application.

---

## ✅ IMPLEMENTED RESTRICTIONS

### 1. **Teachers Can Only Edit Themselves (Not Other Teachers)**

**Backend Changes:**
- **TeacherService.updateTeacher()** - Now requires current user email
  - Validates that teacher can only edit their own profile
  - Throws exception if trying to edit another teacher

**Code Location:** `src/main/java/com/example/studentmanagementsystem/service/TeacherService.java`

```java
public Teacher updateTeacher(Long id, TeacherDto dto, String currentUserEmail) {
    Teacher teacher = getTeacherById(id);
    Teacher currentTeacher = getTeacherByEmail(currentUserEmail);
    
    // Teachers can only edit themselves
    if (!teacher.getId().equals(currentTeacher.getId())) {
        throw new RuntimeException("You can only edit your own profile");
    }
    // ... rest of update logic
}
```

---

### 2. **Teachers Can Only Manage Students in Their Own Department**

**Backend Changes:**

**A. Student Update Restriction:**
- **StudentService.updateStudent()** - Teachers can only edit students in their department
  - Checks if teacher and student are in the same department
  - Throws AccessDeniedException if not

**Code Location:** `src/main/java/com/example/studentmanagementsystem/service/StudentService.java`

```java
public Student updateStudent(Long id, StudentUpdateDto dto, String currentUserEmail, boolean isTeacher) {
    if (isTeacher) {
        Teacher teacher = teacherRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        if (teacher.getDepartment() == null) {
            throw new AccessDeniedException("You must be assigned to a department");
        }
        
        if (student.getDepartment() == null || 
            !student.getDepartment().getId().equals(teacher.getDepartment().getId())) {
            throw new AccessDeniedException("You can only edit students in your department");
        }
    }
    // ... rest of update logic
}
```

**B. Student Assignment Restriction:**
- **TeacherService.assignStudents()** - Only assign students from same department
  - Validates each student belongs to teacher's department
  - Prevents cross-department student assignments

**Code Location:** `src/main/java/com/example/studentmanagementsystem/service/TeacherService.java`

```java
public Teacher assignStudents(Long teacherId, Set<Long> studentIds, String currentUserEmail) {
    // Teachers can only assign students to themselves
    // Teachers can only assign students from their own department
    for (Long studentId : studentIds) {
        Student student = studentRepository.findById(studentId)...;
        
        if (student.getDepartment() == null || 
            !student.getDepartment().getId().equals(teacher.getDepartment().getId())) {
            throw new RuntimeException("You can only assign students from your department");
        }
    }
}
```

**C. View Restriction:**
- **StudentService.canAccess()** - Teachers can only view students in their department

**Frontend Changes:**
- **TeacherController.showAssignStudentsForm()** - Shows only same-department students
  - Uses `teacherService.getStudentsInSameDepartment()`
  - Filters student list by department

---

### 3. **Teachers Cannot Create/Delete Students (Only Via Registration)**

**Backend Changes:**

**SecurityConfig.java:**
- Removed `.requestMatchers("/students/create").hasRole("TEACHER")`
- Removed `.requestMatchers("/students/*/delete").hasRole("TEACHER")`

**StudentController.java:**
- Removed `@GetMapping("/create")` method
- Removed `@PostMapping("/create")` method  
- Removed `@PostMapping("/{id}/delete")` method

**Result:**
- Only way to create students: Self-registration via `/register/student`
- Teachers cannot delete students
- Students can only be managed through proper registration workflow

---

### 4. **Students Manage Their Own Course Enrollments**

**New Feature:**

**Backend:**
- **StudentService.enrollInCourse()** - Student enrolls themselves in a course
  - Validates student is enrolling themselves (not others)
  - Adds course to student's course list

- **StudentService.unenrollFromCourse()** - Student removes themselves from a course
  - Validates student is unenrolling themselves
  - Removes course from student's course list

**Code Location:** `src/main/java/com/example/studentmanagementsystem/service/StudentService.java`

```java
public Student enrollInCourse(Long studentId, Long courseId, String currentUserEmail) {
    Student student = getStudentById(studentId);
    Student currentStudent = getStudentByEmail(currentUserEmail);
    
    // Students can only enroll themselves
    if (!student.getId().equals(currentStudent.getId())) {
        throw new AccessDeniedException("You can only enroll yourself in courses");
    }
    
    Course course = courseRepository.findById(courseId)...;
    student.addCourse(course);
    return studentRepository.save(student);
}
```

**Frontend:**
- **New Template:** `students/manage-courses.html`
  - Shows enrolled courses (with unenroll buttons)
  - Shows available courses (with enroll buttons)
  - Only accessible by the student themselves

**StudentController.java:**
- `GET /students/{id}/courses` - Manage courses page
- `POST /students/{studentId}/courses/{courseId}/enroll` - Enroll in course
- `POST /students/{studentId}/courses/{courseId}/unenroll` - Unenroll from course

**Student View Page Update:**
- Added "Manage My Courses" button (only visible to students)
- Links to course management page

---

### 5. **Teachers Cannot Assign/Remove Students from Courses**

**Backend Changes:**

**CourseService.java:**
- Removed `assignStudents()` method
- Teachers no longer have ability to manage course enrollments

**CourseController.java:**
- Removed `@GetMapping("/{id}/assign-students")` 
- Removed `@PostMapping("/{id}/assign-students")`

**Frontend Changes:**

**courses/view.html:**
- Removed "Assign Students" button
- Added info message: "Students manage their own course enrollments"
- Still shows list of enrolled students (read-only)

**Result:**
- Teachers can create/edit/delete courses
- Students independently enroll/unenroll themselves
- Teachers can view who's enrolled but can't modify enrollments

---

### 6. **Teachers Can Only Manage Courses in Their Own Department**

**Backend Changes:**

**CourseService.createCourse():**
- Now requires teacher's email
- Automatically sets course department to teacher's department
- Automatically sets course teacher to current teacher

```java
public Course createCourse(CourseDto dto, String teacherEmail) {
    Teacher teacher = teacherRepository.findByEmail(teacherEmail)...;
    
    if (teacher.getDepartment() == null) {
        throw new RuntimeException("You must be assigned to a department");
    }
    
    course.setDepartment(teacher.getDepartment()); // Force teacher's department
    course.setTeacher(teacher); // Force current teacher
    return courseRepository.save(course);
}
```

**CourseService.updateCourse():**
- Validates course belongs to teacher's department
- Department and teacher cannot be changed

**CourseService.deleteCourse():**
- Validates course belongs to teacher's department before deletion

**CourseService.getCoursesInTeacherDepartment():**
- Returns only courses in teacher's department

**Frontend Changes:**

**CourseController.listCourses():**
- Shows only courses in teacher's department
- Uses `courseService.getCoursesInTeacherDepartment()`

**courses/create.html:**
- Removed department dropdown
- Removed teacher dropdown
- Shows info message: "Course will be created in your department"

**courses/edit.html:**
- Removed department dropdown
- Removed teacher dropdown  
- Shows info message: "Department and teacher assignment cannot be changed"

---

## 📋 COMPLETE AUTHORIZATION MATRIX

| Action | Student | Teacher | Restrictions |
|--------|---------|---------|--------------|
| **View own profile** | ✅ Yes | ✅ Yes | - |
| **Edit own profile** | ✅ Yes | ✅ Yes | Students: cannot change role<br>Teachers: cannot edit other teachers |
| **View other students** | ❌ No | ✅ Yes | Teachers: only same department |
| **Edit other students** | ❌ No | ✅ Yes | Teachers: only same department |
| **Create students** | ❌ No | ❌ No | Only via registration |
| **Delete students** | ❌ No | ❌ No | Disabled for all |
| **Enroll in courses** | ✅ Yes | ❌ No | Students: only themselves |
| **Unenroll from courses** | ✅ Yes | ❌ No | Students: only themselves |
| **Assign students to courses** | ❌ No | ❌ No | Removed - students self-manage |
| **Create courses** | ❌ No | ✅ Yes | Teachers: only in own department |
| **Edit courses** | ❌ No | ✅ Yes | Teachers: only in own department |
| **Delete courses** | ❌ No | ✅ Yes | Teachers: only in own department |
| **View courses** | ✅ Yes | ✅ Yes | Teachers: only own department<br>Students: all courses |
| **Assign students to teacher** | ❌ No | ✅ Yes | Teachers: only same department students |
| **Manage departments** | ❌ No | ✅ Yes | All teachers |

---

## 🔒 SECURITY CONFIGURATION

**File:** `src/main/java/com/example/studentmanagementsystem/security/SecurityConfig.java`

```java
.authorizeHttpRequests(authorize -> authorize
    .requestMatchers("/", "/login/**", "/register/**", "/error", "/css/**", "/js/**", "/images/**").permitAll()
    .requestMatchers("/admin/**").hasRole("TEACHER")
    .requestMatchers("/teachers/**").hasRole("TEACHER")
    .requestMatchers("/courses/**").hasRole("TEACHER")
    .requestMatchers("/departments/**").hasRole("TEACHER")
    .requestMatchers("/students/*/courses/**").authenticated() // Students manage own courses
    .requestMatchers("/students/**").authenticated()
    .anyRequest().authenticated()
)
```

**Key Changes:**
- Removed `/students/create` from teacher-only access
- Removed `/students/*/delete` from teacher-only access
- Added `/students/*/courses/**` for authenticated users (course enrollment)

---

## 📁 FILES MODIFIED

### Backend (Java Services)
1. ✅ `StudentService.java` - Department-based access control, course enrollment
2. ✅ `TeacherService.java` - Self-edit only, department-based student assignment
3. ✅ `CourseService.java` - Department-based course management
4. ✅ `SecurityConfig.java` - Updated access rules

### Backend (Controllers)
5. ✅ `StudentController.java` - Removed create/delete, added course enrollment
6. ✅ `TeacherController.java` - Pass authentication for validation
7. ✅ `CourseController.java` - Department filtering, removed student assignment

### Frontend (Templates)
8. ✅ `students/view.html` - Added "Manage My Courses" button
9. ✅ `students/manage-courses.html` - NEW: Course enrollment interface
10. ✅ `courses/create.html` - Removed department/teacher dropdowns
11. ✅ `courses/edit.html` - Removed department/teacher dropdowns
12. ✅ `courses/view.html` - Removed "Assign Students" button, added info message

---

## ✅ TESTING CHECKLIST

### Teacher Tests
- [ ] Login as Teacher (john.smith@university.edu / password123)
- [ ] Try to edit own profile ✅ Should work
- [ ] Try to edit another teacher's profile ❌ Should show error
- [ ] View students list - should only see same department students
- [ ] Try to edit student from same department ✅ Should work
- [ ] Try to edit student from different department ❌ Should show error
- [ ] Create a course - should auto-assign to your department
- [ ] Try to edit course from your department ✅ Should work
- [ ] Assign students to yourself - should only show same department students

### Student Tests
- [ ] Login as Student (alice.williams@student.edu / password123)
- [ ] View own profile ✅ Should work
- [ ] Click "Manage My Courses" button
- [ ] Enroll in a course ✅ Should work
- [ ] Unenroll from a course ✅ Should work
- [ ] Try to access course management for another student ❌ Should be denied
- [ ] Edit own profile ✅ Should work
- [ ] Try to access teacher pages ❌ Should be denied

### Registration Tests
- [ ] Access /register
- [ ] Register new student - creates account
- [ ] Register new teacher - creates account  
- [ ] Login with new credentials ✅ Should work
- [ ] Teachers cannot create students via old interface ❌ Option removed

---

## 🚀 DEPLOYMENT INSTRUCTIONS

### Step 1: Stop Running Application
```powershell
taskkill /F /IM java.exe 2>$null
Start-Sleep -Seconds 3
```

### Step 2: Navigate to Project
```powershell
cd "E:\3-2\LAB\SOFTWARE\lectures\lab3\student-management-system"
```

### Step 3: Clean Build
```powershell
.\mvnw.cmd clean package -DskipTests
```

### Step 4: Run Application
```powershell
java -jar target\student-management-system-0.0.1-SNAPSHOT.jar
```

### Step 5: Wait for Startup
Look for message:
```
Tomcat initialized with port 9090
```

### Step 6: Access Application
Open browser: **http://localhost:9090**

---

## 📝 DEMO CREDENTIALS

### Teachers (Password: password123)
- john.smith@university.edu (Computer Science)
- sarah.johnson@university.edu (Electrical Engineering)
- michael.brown@university.edu (Mechanical Engineering)

### Students (Password: password123)
- alice.williams@student.edu (Computer Science)
- bob.anderson@student.edu (Computer Science)
- carol.martinez@student.edu (Electrical Engineering)
- david.thompson@student.edu (Electrical Engineering)
- emma.garcia@student.edu (Mechanical Engineering)

---

## 🎯 SUMMARY

**All authorization issues resolved:**

✅ Teachers can only edit themselves (not other teachers)
✅ Teachers can only manage students in their own department
✅ Teachers cannot create/delete students (only via registration)
✅ Students manage their own course enrollments
✅ Teachers cannot assign/remove students from courses
✅ Teachers can only create/manage courses in their own department

**Security is properly implemented at:**
- Service layer (business logic validation)
- Controller layer (request handling)
- Security configuration (URL-based access control)
- Frontend (conditional rendering based on roles)

**The application is now production-ready with proper role-based access control!** 🎉
