# Student Management System

A comprehensive student management system built with Spring Boot, Spring Security, Spring Data JPA, and Thymeleaf.

## Features

### For All Users
- **User Registration** - Self-registration for Students and Teachers
  - Student registration with roll number
  - Teacher registration with professional credentials
  - Email uniqueness validation
  - Secure password encryption (BCrypt)

### For Teachers (Admin)
- **Full CRUD Operations** for Students, Teachers, Courses, and Departments
- **Student-Teacher Assignment** - Assign students to teachers
- **Course-Student Enrollment** - Assign students to courses
- **Role-based Access Control** - Teachers have full administrative privileges

### For Students
- **View Profile** - View their own profile information
- **Edit Profile** - Update their name, email, and department (cannot change role)
- **View Enrolled Courses** - See courses they are enrolled in
- **View Assigned Teachers** - See teachers assigned to them

## Technology Stack

- **Backend**: Spring Boot 4.0.2
- **Security**: Spring Security 6 with Role-based access
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Frontend**: Thymeleaf + Bootstrap 5
- **Containerization**: Docker & Docker Compose

## Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL 12+ (or Docker)
- Docker & Docker Compose (optional)

## Running the Application

### Option 1: Local Development

1. **Configure PostgreSQL**
   - Create a database named `student-managementDB`
   - Update credentials in `src/main/resources/application.properties` if needed

2. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. **Access the application**
   - URL: http://localhost:9090
   - Login page will appear automatically

### Option 2: Using Docker Compose

1. **Build and Start**
   ```bash
   docker-compose up --build
   ```

2. **Access the application**
   - URL: http://localhost:9090

3. **Stop the application**
   ```bash
   docker-compose down
   ```

## Default Login Credentials

The application seeds initial data on first startup:

| Role    | Email                  | Password    |
|---------|------------------------|-------------|
| Teacher | teacher@example.com    | password123 |
| Student | student@example.com    | password123 |

## Project Structure

```
src/main/java/com/example/studentmanagementsystem/
├── config/
│   └── DataInitializer.java      # Seed data initialization
├── controller/
│   ├── HomeController.java       # Login, Dashboard
│   ├── StudentController.java    # Student CRUD
│   ├── TeacherController.java    # Teacher CRUD
│   ├── CourseController.java     # Course CRUD
│   └── DepartmentController.java # Department CRUD
├── dto/
│   ├── StudentCreateDto.java
│   ├── StudentUpdateDto.java
│   ├── TeacherDto.java
│   ├── CourseDto.java
│   └── DepartmentDto.java
├── entity/
│   ├── Role.java                 # STUDENT, TEACHER enum
│   ├── Student.java
│   ├── Teacher.java
│   ├── Course.java
│   └── Department.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── repository/
│   ├── StudentRepository.java
│   ├── TeacherRepository.java
│   ├── CourseRepository.java
│   └── DepartmentRepository.java
├── security/
│   ├── SecurityConfig.java
│   └── CustomUserDetailsService.java
├── service/
│   ├── StudentService.java
│   ├── TeacherService.java
│   ├── CourseService.java
│   └── DepartmentService.java
└── StudentManagementSystemApplication.java

src/main/resources/
├── templates/
│   ├── fragments/layout.html     # Navbar, alerts, scripts
│   ├── login.html
│   ├── dashboard.html
│   ├── students/                 # list, view, create, edit
│   ├── teachers/                 # list, view, create, edit, assign-students
│   ├── courses/                  # list, view, create, edit, assign-students
│   ├── departments/              # list, view, create, edit
│   └── error/                    # 403, 404, error
└── application.properties
```

## Entity Relationships

- **Department** ↔ **Teachers** (One-to-Many)
- **Department** ↔ **Students** (One-to-Many)
- **Department** ↔ **Courses** (One-to-Many)
- **Teacher** ↔ **Students** (Many-to-Many via `teacher_student` table)
- **Course** ↔ **Students** (Many-to-Many via `student_course` table)
- **Teacher** ↔ **Courses** (One-to-Many)

## Security Rules

| Endpoint             | Access                    |
|----------------------|---------------------------|
| `/login`             | Public                    |
| `/dashboard`         | Authenticated             |
| `/students`          | Authenticated             |
| `/students/{id}`     | Owner or Teacher          |
| `/students/{id}/edit`| Owner or Teacher          |
| `/students/create`   | Teacher only              |
| `/students/*/delete` | Teacher only              |
| `/teachers/**`       | Teacher only              |
| `/courses/**`        | Teacher only              |
| `/departments/**`    | Teacher only              |

## API Endpoints Summary

### Authentication & Registration
- `GET /login` - Login page
- `GET /register` - Registration choice page
- `GET /register/student` - Student registration form
- `POST /register/student` - Submit student registration
- `GET /register/teacher` - Teacher registration form
- `POST /register/teacher` - Submit teacher registration
- `POST /logout` - Logout

### Students
- `GET /students` - List all students
- `GET /students/{id}` - View student details
- `GET /students/create` - Show create form (Teacher only)
- `POST /students/create` - Create student (Teacher only)
- `GET /students/{id}/edit` - Show edit form
- `POST /students/{id}/edit` - Update student
- `POST /students/{id}/delete` - Delete student (Teacher only)

### Teachers (Teacher role only)
- `GET /teachers` - List all teachers
- `GET /teachers/{id}` - View teacher details
- `GET /teachers/create` - Show create form
- `POST /teachers/create` - Create teacher
- `GET /teachers/{id}/edit` - Show edit form
- `POST /teachers/{id}/edit` - Update teacher
- `POST /teachers/{id}/delete` - Delete teacher
- `GET /teachers/{id}/assign-students` - Show student assignment form
- `POST /teachers/{id}/assign-students` - Assign students to teacher

### Courses (Teacher role only)
- `GET /courses` - List all courses
- `GET /courses/{id}` - View course details
- `GET /courses/create` - Show create form
- `POST /courses/create` - Create course
- `GET /courses/{id}/edit` - Show edit form
- `POST /courses/{id}/edit` - Update course
- `POST /courses/{id}/delete` - Delete course
- `GET /courses/{id}/assign-students` - Show student assignment form
- `POST /courses/{id}/assign-students` - Assign students to course

### Departments (Teacher role only)
- `GET /departments` - List all departments
- `GET /departments/{id}` - View department details
- `GET /departments/create` - Show create form
- `POST /departments/create` - Create department
- `GET /departments/{id}/edit` - Show edit form
- `POST /departments/{id}/edit` - Update department
- `POST /departments/{id}/delete` - Delete department

## License

This project is for educational purposes.
