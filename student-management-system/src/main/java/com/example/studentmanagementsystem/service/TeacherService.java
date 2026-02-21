package com.example.studentmanagementsystem.service;

import com.example.studentmanagementsystem.dto.TeacherDto;
import com.example.studentmanagementsystem.entity.Department;
import com.example.studentmanagementsystem.entity.Student;
import com.example.studentmanagementsystem.entity.Teacher;
import com.example.studentmanagementsystem.repository.DepartmentRepository;
import com.example.studentmanagementsystem.repository.StudentRepository;
import com.example.studentmanagementsystem.repository.TeacherRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public TeacherService(TeacherRepository teacherRepository,
                          StudentRepository studentRepository,
                          DepartmentRepository departmentRepository,
                          PasswordEncoder passwordEncoder) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));
    }

    public Teacher getTeacherByEmail(String email) {
        return teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher not found with email: " + email));
    }

    public Teacher createTeacher(TeacherDto dto) {
        if (teacherRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Teacher teacher = new Teacher();
        teacher.setName(dto.getName());
        teacher.setEmail(dto.getEmail());
        teacher.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            teacher.setDepartment(department);
        }

        return teacherRepository.save(teacher);
    }

    public Teacher updateTeacher(Long id, TeacherDto dto, String currentUserEmail) {
        Teacher teacher = getTeacherById(id);
        Teacher currentTeacher = getTeacherByEmail(currentUserEmail);

        // Teachers can only edit themselves, not other teachers
        if (!teacher.getId().equals(currentTeacher.getId())) {
            throw new RuntimeException("You can only edit your own profile");
        }

        teacher.setName(dto.getName());

        // Only update email if it's not already taken by another user
        if (!teacher.getEmail().equals(dto.getEmail())) {
            if (teacherRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            teacher.setEmail(dto.getEmail());
        }

        // Only update password if provided
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            teacher.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            teacher.setDepartment(department);
        }

        return teacherRepository.save(teacher);
    }

    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new RuntimeException("Teacher not found with id: " + id);
        }
        teacherRepository.deleteById(id);
    }

    public Teacher assignStudents(Long teacherId, Set<Long> studentIds, String currentUserEmail) {
        Teacher teacher = getTeacherById(teacherId);
        Teacher currentTeacher = getTeacherByEmail(currentUserEmail);

        // Teachers can only assign students to themselves
        if (!teacher.getId().equals(currentTeacher.getId())) {
            throw new RuntimeException("You can only manage your own students");
        }

        // Teachers must be assigned to a department
        if (teacher.getDepartment() == null) {
            throw new RuntimeException("You must be assigned to a department first");
        }

        // Clear existing assignments
        teacher.getStudents().clear();

        // Add new assignments - only from same department
        for (Long studentId : studentIds) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

            // Verify student is in same department
            if (student.getDepartment() == null ||
                !student.getDepartment().getId().equals(teacher.getDepartment().getId())) {
                throw new RuntimeException("You can only assign students from your department: " +
                    teacher.getDepartment().getName());
            }

            teacher.addStudent(student);
        }

        return teacherRepository.save(teacher);
    }

    public List<Student> getStudentsInSameDepartment(String teacherEmail) {
        Teacher teacher = getTeacherByEmail(teacherEmail);
        if (teacher.getDepartment() == null) {
            return List.of();
        }
        return studentRepository.findAll().stream()
                .filter(s -> s.getDepartment() != null &&
                           s.getDepartment().getId().equals(teacher.getDepartment().getId()))
                .toList();
    }

    public Teacher assignDepartment(Long teacherId, Long departmentId) {
        Teacher teacher = getTeacherById(teacherId);
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        teacher.setDepartment(department);
        return teacherRepository.save(teacher);
    }
}
