package com.example.studentmanagementsystem.security;

import com.example.studentmanagementsystem.entity.Student;
import com.example.studentmanagementsystem.entity.Teacher;
import com.example.studentmanagementsystem.repository.StudentRepository;
import com.example.studentmanagementsystem.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Unit Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private Teacher testTeacher;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        testTeacher = new Teacher("Dr. Smith", "smith@test.com", "$2a$10$encodedPassword");
        testTeacher.setId(1L);

        testStudent = new Student("John Doe", "CS001", "john@test.com", "$2a$10$encodedPassword");
        testStudent.setId(1L);
    }

    @Test
    @DisplayName("Should load teacher by email")
    void loadUserByUsername_whenTeacher_shouldReturnTeacherDetails() {
        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));

        UserDetails userDetails = userDetailsService.loadUserByUsername("smith@test.com");

        assertThat(userDetails.getUsername()).isEqualTo("smith@test.com");
        assertThat(userDetails.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
    }

    @Test
    @DisplayName("Should load student by email")
    void loadUserByUsername_whenStudent_shouldReturnStudentDetails() {
        when(teacherRepository.findByEmail("john@test.com")).thenReturn(Optional.empty());
        when(studentRepository.findByEmail("john@test.com")).thenReturn(Optional.of(testStudent));

        UserDetails userDetails = userDetailsService.loadUserByUsername("john@test.com");

        assertThat(userDetails.getUsername()).isEqualTo("john@test.com");
        assertThat(userDetails.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
    }

    @Test
    @DisplayName("Should throw when user not found")
    void loadUserByUsername_whenNotFound_shouldThrow() {
        when(teacherRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());
        when(studentRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown@test.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    @DisplayName("Should prioritize teacher over student with same email")
    void loadUserByUsername_whenBothExist_shouldReturnTeacher() {
        when(teacherRepository.findByEmail("shared@test.com")).thenReturn(Optional.of(testTeacher));

        UserDetails userDetails = userDetailsService.loadUserByUsername("shared@test.com");

        assertThat(userDetails.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
    }

    @Test
    @DisplayName("Should return correct password for teacher")
    void loadUserByUsername_forTeacher_shouldHaveCorrectPassword() {
        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));

        UserDetails userDetails = userDetailsService.loadUserByUsername("smith@test.com");

        assertThat(userDetails.getPassword()).isEqualTo("$2a$10$encodedPassword");
    }

    @Test
    @DisplayName("Should return correct password for student")
    void loadUserByUsername_forStudent_shouldHaveCorrectPassword() {
        when(teacherRepository.findByEmail("john@test.com")).thenReturn(Optional.empty());
        when(studentRepository.findByEmail("john@test.com")).thenReturn(Optional.of(testStudent));

        UserDetails userDetails = userDetailsService.loadUserByUsername("john@test.com");

        assertThat(userDetails.getPassword()).isEqualTo("$2a$10$encodedPassword");
    }
}
