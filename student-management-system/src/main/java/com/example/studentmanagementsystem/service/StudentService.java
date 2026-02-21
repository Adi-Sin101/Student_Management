package com.example.studentmanagementsystem.service;

import com.example.studentmanagementsystem.dto.StudentCreateDto;
import com.example.studentmanagementsystem.dto.StudentUpdateDto;
import com.example.studentmanagementsystem.entity.Course;
import com.example.studentmanagementsystem.entity.Department;
import com.example.studentmanagementsystem.entity.Student;
import com.example.studentmanagementsystem.entity.Teacher;
import com.example.studentmanagementsystem.repository.CourseRepository;
import com.example.studentmanagementsystem.repository.DepartmentRepository;
import com.example.studentmanagementsystem.repository.StudentRepository;
import com.example.studentmanagementsystem.repository.TeacherRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository,
                          DepartmentRepository departmentRepository,
                          TeacherRepository teacherRepository,
                          CourseRepository courseRepository,
                          PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.departmentRepository = departmentRepository;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    public Student getStudentByEmail(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found with email: " + email));
    }

    public Student createStudent(StudentCreateDto dto) {
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (studentRepository.existsByRoll(dto.getRoll())) {
            throw new RuntimeException("Roll number already exists");
        }

        Student student = new Student();
        student.setName(dto.getName());
        student.setRoll(dto.getRoll());
        student.setEmail(dto.getEmail());
        student.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            student.setDepartment(department);
        }

        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, StudentUpdateDto dto, String currentUserEmail, boolean isTeacher) {
        Student student = getStudentById(id);

        // Check authorization: student can only update themselves
        if (!isTeacher) {
            Student currentStudent = getStudentByEmail(currentUserEmail);
            if (!currentStudent.getId().equals(id)) {
                throw new AccessDeniedException("You can only update your own profile");
            }
        } else {
            // Teachers can only update students in their own department
            Teacher teacher = teacherRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            if (teacher.getDepartment() == null) {
                throw new AccessDeniedException("You must be assigned to a department to edit students");
            }

            if (student.getDepartment() == null ||
                !student.getDepartment().getId().equals(teacher.getDepartment().getId())) {
                throw new AccessDeniedException("You can only edit students in your department: " +
                    teacher.getDepartment().getName());
            }
        }

        student.setName(dto.getName());

        // Only update email if it's not already taken by another user
        if (!student.getEmail().equals(dto.getEmail())) {
            if (studentRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            student.setEmail(dto.getEmail());
        }

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            student.setDepartment(department);
        }

        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    public boolean canAccess(Long studentId, String currentUserEmail, boolean isTeacher) {
        if (isTeacher) {
            // Teachers can only access students in their department
            Teacher teacher = teacherRepository.findByEmail(currentUserEmail).orElse(null);
            if (teacher == null || teacher.getDepartment() == null) {
                return false;
            }
            Student student = studentRepository.findById(studentId).orElse(null);
            return student != null && student.getDepartment() != null &&
                   student.getDepartment().getId().equals(teacher.getDepartment().getId());
        }
        Student currentStudent = studentRepository.findByEmail(currentUserEmail).orElse(null);
        return currentStudent != null && currentStudent.getId().equals(studentId);
    }

    public Student enrollInCourse(Long studentId, Long courseId, String currentUserEmail) {
        Student student = getStudentById(studentId);
        Student currentStudent = getStudentByEmail(currentUserEmail);

        // Students can only enroll themselves
        if (!student.getId().equals(currentStudent.getId())) {
            throw new AccessDeniedException("You can only enroll yourself in courses");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        student.addCourse(course);
        return studentRepository.save(student);
    }

    public Student unenrollFromCourse(Long studentId, Long courseId, String currentUserEmail) {
        Student student = getStudentById(studentId);
        Student currentStudent = getStudentByEmail(currentUserEmail);

        // Students can only unenroll themselves
        if (!student.getId().equals(currentStudent.getId())) {
            throw new AccessDeniedException("You can only unenroll yourself from courses");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        student.removeCourse(course);
        return studentRepository.save(student);
    }
}
