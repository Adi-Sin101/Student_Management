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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService Unit Tests")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentService studentService;

    private Department testDepartment;
    private Student testStudent;
    private Teacher testTeacher;

    @BeforeEach
    void setUp() {
        testDepartment = new Department("Computer Science");
        testDepartment.setId(1L);

        testStudent = new Student("John Doe", "CS001", "john@test.com", "encoded");
        testStudent.setId(1L);
        testStudent.setDepartment(testDepartment);

        testTeacher = new Teacher("Dr. Smith", "smith@test.com", "encoded");
        testTeacher.setId(1L);
        testTeacher.setDepartment(testDepartment);
    }

    @Test
    @DisplayName("getAllStudents should return all students")
    void getAllStudents_shouldReturnAll() {
        when(studentRepository.findAll()).thenReturn(List.of(testStudent));

        List<Student> result = studentService.getAllStudents();

        assertThat(result).hasSize(1);
        verify(studentRepository).findAll();
    }

    @Test
    @DisplayName("getStudentById should return student when found")
    void getStudentById_whenFound_shouldReturn() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));

        Student result = studentService.getStudentById(1L);

        assertThat(result.getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("getStudentById should throw when not found")
    void getStudentById_whenNotFound_shouldThrow() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    @DisplayName("getStudentByEmail should return student when found")
    void getStudentByEmail_whenFound_shouldReturn() {
        when(studentRepository.findByEmail("john@test.com")).thenReturn(Optional.of(testStudent));

        Student result = studentService.getStudentByEmail("john@test.com");

        assertThat(result.getRoll()).isEqualTo("CS001");
    }

    @Test
    @DisplayName("createStudent should create successfully")
    void createStudent_shouldCreate() {
        StudentCreateDto dto = new StudentCreateDto();
        dto.setName("Jane Doe");
        dto.setRoll("CS002");
        dto.setEmail("jane@test.com");
        dto.setPassword("password123");
        dto.setDepartmentId(1L);

        when(studentRepository.existsByEmail("jane@test.com")).thenReturn(false);
        when(studentRepository.existsByRoll("CS002")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> {
            Student s = inv.getArgument(0);
            s.setId(2L);
            return s;
        });

        Student result = studentService.createStudent(dto);

        assertThat(result.getName()).isEqualTo("Jane Doe");
        assertThat(result.getDepartment()).isEqualTo(testDepartment);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("createStudent should throw when email exists")
    void createStudent_whenEmailExists_shouldThrow() {
        StudentCreateDto dto = new StudentCreateDto();
        dto.setName("Jane");
        dto.setRoll("CS002");
        dto.setEmail("john@test.com");
        dto.setPassword("password");

        when(studentRepository.existsByEmail("john@test.com")).thenReturn(true);

        assertThatThrownBy(() -> studentService.createStudent(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    @DisplayName("createStudent should throw when roll exists")
    void createStudent_whenRollExists_shouldThrow() {
        StudentCreateDto dto = new StudentCreateDto();
        dto.setName("Jane");
        dto.setRoll("CS001");
        dto.setEmail("jane@test.com");
        dto.setPassword("password");

        when(studentRepository.existsByEmail("jane@test.com")).thenReturn(false);
        when(studentRepository.existsByRoll("CS001")).thenReturn(true);

        assertThatThrownBy(() -> studentService.createStudent(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Roll number already exists");
    }

    @Test
    @DisplayName("updateStudent should allow student to update own profile")
    void updateStudent_asOwnStudent_shouldUpdate() {
        StudentUpdateDto dto = new StudentUpdateDto();
        dto.setName("John Updated");
        dto.setEmail("john@test.com");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(studentRepository.findByEmail("john@test.com")).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        Student result = studentService.updateStudent(1L, dto, "john@test.com", false);

        assertThat(result.getName()).isEqualTo("John Updated");
    }

    @Test
    @DisplayName("updateStudent should deny student updating another student")
    void updateStudent_asOtherStudent_shouldThrow() {
        StudentUpdateDto dto = new StudentUpdateDto();
        dto.setName("Hacked");
        dto.setEmail("hacker@test.com");

        Student otherStudent = new Student("Other", "CS099", "other@test.com", "encoded");
        otherStudent.setId(2L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(studentRepository.findByEmail("other@test.com")).thenReturn(Optional.of(otherStudent));

        assertThatThrownBy(() -> studentService.updateStudent(1L, dto, "other@test.com", false))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("updateStudent should allow teacher in same department")
    void updateStudent_asTeacherSameDept_shouldUpdate() {
        StudentUpdateDto dto = new StudentUpdateDto();
        dto.setName("Updated by Teacher");
        dto.setEmail("john@test.com");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        Student result = studentService.updateStudent(1L, dto, "smith@test.com", true);

        assertThat(result.getName()).isEqualTo("Updated by Teacher");
    }

    @Test
    @DisplayName("deleteStudent should delete when exists")
    void deleteStudent_shouldDelete() {
        when(studentRepository.existsById(1L)).thenReturn(true);

        studentService.deleteStudent(1L);

        verify(studentRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteStudent should throw when not found")
    void deleteStudent_whenNotFound_shouldThrow() {
        when(studentRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> studentService.deleteStudent(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    @DisplayName("canAccess should return true for student accessing own profile")
    void canAccess_asOwnStudent_shouldReturnTrue() {
        when(studentRepository.findByEmail("john@test.com")).thenReturn(Optional.of(testStudent));

        boolean result = studentService.canAccess(1L, "john@test.com", false);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("canAccess should return false for student accessing other profile")
    void canAccess_asOtherStudent_shouldReturnFalse() {
        Student other = new Student("Other", "CS002", "other@test.com", "encoded");
        other.setId(2L);
        when(studentRepository.findByEmail("other@test.com")).thenReturn(Optional.of(other));

        boolean result = studentService.canAccess(1L, "other@test.com", false);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("canAccess should return true for teacher in same department")
    void canAccess_asTeacherSameDept_shouldReturnTrue() {
        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));

        boolean result = studentService.canAccess(1L, "smith@test.com", true);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("enrollInCourse should enroll student successfully")
    void enrollInCourse_shouldEnroll() {
        Course course = new Course("DS", "Data Structures");
        course.setId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(studentRepository.findByEmail("john@test.com")).thenReturn(Optional.of(testStudent));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        Student result = studentService.enrollInCourse(1L, 1L, "john@test.com");

        assertThat(result.getCourses()).contains(course);
    }

    @Test
    @DisplayName("enrollInCourse should deny enrolling another student")
    void enrollInCourse_asOtherStudent_shouldThrow() {
        Student other = new Student("Other", "CS002", "other@test.com", "encoded");
        other.setId(2L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(studentRepository.findByEmail("other@test.com")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> studentService.enrollInCourse(1L, 1L, "other@test.com"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("unenrollFromCourse should unenroll student successfully")
    void unenrollFromCourse_shouldUnenroll() {
        Course course = new Course("DS", "Data Structures");
        course.setId(1L);
        testStudent.addCourse(course);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(studentRepository.findByEmail("john@test.com")).thenReturn(Optional.of(testStudent));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        Student result = studentService.unenrollFromCourse(1L, 1L, "john@test.com");

        assertThat(result.getCourses()).doesNotContain(course);
    }
}
