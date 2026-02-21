package com.example.studentmanagementsystem.service;

import com.example.studentmanagementsystem.dto.TeacherDto;
import com.example.studentmanagementsystem.entity.Department;
import com.example.studentmanagementsystem.entity.Student;
import com.example.studentmanagementsystem.entity.Teacher;
import com.example.studentmanagementsystem.repository.DepartmentRepository;
import com.example.studentmanagementsystem.repository.StudentRepository;
import com.example.studentmanagementsystem.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeacherService Unit Tests")
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TeacherService teacherService;

    private Department testDepartment;
    private Teacher testTeacher;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        testDepartment = new Department("Computer Science");
        testDepartment.setId(1L);

        testTeacher = new Teacher("Dr. Smith", "smith@test.com", "encoded");
        testTeacher.setId(1L);
        testTeacher.setDepartment(testDepartment);

        testStudent = new Student("John Doe", "CS001", "john@test.com", "encoded");
        testStudent.setId(1L);
        testStudent.setDepartment(testDepartment);
    }

    @Test
    @DisplayName("getAllTeachers should return all teachers")
    void getAllTeachers_shouldReturnAll() {
        when(teacherRepository.findAll()).thenReturn(List.of(testTeacher));

        List<Teacher> result = teacherService.getAllTeachers();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getTeacherById should return teacher when found")
    void getTeacherById_whenFound_shouldReturn() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(testTeacher));

        Teacher result = teacherService.getTeacherById(1L);

        assertThat(result.getName()).isEqualTo("Dr. Smith");
    }

    @Test
    @DisplayName("getTeacherById should throw when not found")
    void getTeacherById_whenNotFound_shouldThrow() {
        when(teacherRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teacherService.getTeacherById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Teacher not found");
    }

    @Test
    @DisplayName("getTeacherByEmail should return teacher when found")
    void getTeacherByEmail_whenFound_shouldReturn() {
        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));

        Teacher result = teacherService.getTeacherByEmail("smith@test.com");

        assertThat(result.getName()).isEqualTo("Dr. Smith");
    }

    @Test
    @DisplayName("createTeacher should create successfully")
    void createTeacher_shouldCreate() {
        TeacherDto dto = new TeacherDto();
        dto.setName("Dr. Jones");
        dto.setEmail("jones@test.com");
        dto.setPassword("password123");
        dto.setDepartmentId(1L);

        when(teacherRepository.existsByEmail("jones@test.com")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPw");
        when(teacherRepository.save(any(Teacher.class))).thenAnswer(inv -> {
            Teacher t = inv.getArgument(0);
            t.setId(2L);
            return t;
        });

        Teacher result = teacherService.createTeacher(dto);

        assertThat(result.getName()).isEqualTo("Dr. Jones");
        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    @DisplayName("createTeacher should throw when email exists")
    void createTeacher_whenEmailExists_shouldThrow() {
        TeacherDto dto = new TeacherDto();
        dto.setName("Dr. Jones");
        dto.setEmail("smith@test.com");
        dto.setPassword("password");

        when(teacherRepository.existsByEmail("smith@test.com")).thenReturn(true);

        assertThatThrownBy(() -> teacherService.createTeacher(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    @DisplayName("updateTeacher should update own profile")
    void updateTeacher_shouldUpdateOwnProfile() {
        TeacherDto dto = new TeacherDto();
        dto.setName("Dr. Smith Updated");
        dto.setEmail("smith@test.com");

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(testTeacher));
        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));
        when(teacherRepository.save(any(Teacher.class))).thenAnswer(inv -> inv.getArgument(0));

        Teacher result = teacherService.updateTeacher(1L, dto, "smith@test.com");

        assertThat(result.getName()).isEqualTo("Dr. Smith Updated");
    }

    @Test
    @DisplayName("updateTeacher should throw when editing another teacher")
    void updateTeacher_whenEditingOther_shouldThrow() {
        TeacherDto dto = new TeacherDto();
        dto.setName("Hacked");
        dto.setEmail("hacked@test.com");

        Teacher otherTeacher = new Teacher("Dr. Other", "other@test.com", "encoded");
        otherTeacher.setId(2L);

        when(teacherRepository.findById(2L)).thenReturn(Optional.of(otherTeacher));
        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));

        assertThatThrownBy(() -> teacherService.updateTeacher(2L, dto, "smith@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("only edit your own profile");
    }

    @Test
    @DisplayName("deleteTeacher should delete when exists")
    void deleteTeacher_shouldDelete() {
        when(teacherRepository.existsById(1L)).thenReturn(true);

        teacherService.deleteTeacher(1L);

        verify(teacherRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteTeacher should throw when not found")
    void deleteTeacher_whenNotFound_shouldThrow() {
        when(teacherRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> teacherService.deleteTeacher(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Teacher not found");
    }

    @Test
    @DisplayName("assignStudents should assign students from same department")
    void assignStudents_shouldAssign() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(testTeacher));
        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(teacherRepository.save(any(Teacher.class))).thenAnswer(inv -> inv.getArgument(0));

        Teacher result = teacherService.assignStudents(1L, Set.of(1L), "smith@test.com");

        assertThat(result.getStudents()).isNotEmpty();
    }

    @Test
    @DisplayName("assignStudents should throw when assigning to another teacher")
    void assignStudents_whenOtherTeacher_shouldThrow() {
        Teacher otherTeacher = new Teacher("Dr. Other", "other@test.com", "encoded");
        otherTeacher.setId(2L);

        when(teacherRepository.findById(2L)).thenReturn(Optional.of(otherTeacher));
        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));

        assertThatThrownBy(() -> teacherService.assignStudents(2L, Set.of(1L), "smith@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("your own students");
    }

    @Test
    @DisplayName("getStudentsInSameDepartment should return students")
    void getStudentsInSameDepartment_shouldReturn() {
        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));
        when(studentRepository.findAll()).thenReturn(List.of(testStudent));

        List<Student> result = teacherService.getStudentsInSameDepartment("smith@test.com");

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getStudentsInSameDepartment should return empty when no department")
    void getStudentsInSameDepartment_whenNoDept_shouldReturnEmpty() {
        testTeacher.setDepartment(null);
        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));

        List<Student> result = teacherService.getStudentsInSameDepartment("smith@test.com");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("assignDepartment should assign department to teacher")
    void assignDepartment_shouldAssign() {
        Department newDept = new Department("Physics");
        newDept.setId(2L);

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(testTeacher));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(newDept));
        when(teacherRepository.save(any(Teacher.class))).thenAnswer(inv -> inv.getArgument(0));

        Teacher result = teacherService.assignDepartment(1L, 2L);

        assertThat(result.getDepartment().getName()).isEqualTo("Physics");
    }
}
