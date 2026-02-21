package com.example.studentmanagementsystem.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Student Entity
 *
 * Testing Strategy:
 * - Test getters and setters
 * - Test constructors
 * - Test helper methods
 * - Test entity relationships
 * - Test equals() and hashCode() if implemented
 *
 * Note: These are POJO tests, not database tests
 */
@DisplayName("Student Entity Tests")
class StudentEntityTest {

    private Student student;
    private Department department;
    private Course course;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        student = new Student();
        department = new Department();
        department.setId(1L);
        department.setName("Computer Science");

        course = new Course();
        course.setId(1L);
        course.setName("Data Structures");
        course.setStudents(new HashSet<>());

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setName("Dr. Smith");
    }

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("Should create student with no-args constructor")
    void whenCreateStudentWithNoArgsConstructor_thenStudentIsCreated() {
        // Act
        Student newStudent = new Student();

        // Assert
        assertThat(newStudent).isNotNull();
        assertThat(newStudent.getId()).isNull();
        assertThat(newStudent.getName()).isNull();
        assertThat(newStudent.getCourses()).isNotNull(); // Should be initialized
        assertThat(newStudent.getTeachers()).isNotNull(); // Should be initialized
    }

    @Test
    @DisplayName("Should create student with parameterized constructor")
    void whenCreateStudentWithParameterizedConstructor_thenFieldsAreSet() {
        // Act
        Student newStudent = new Student("John Doe", "CS001", "john@test.com", "password123");

        // Assert
        assertThat(newStudent).isNotNull();
        assertThat(newStudent.getName()).isEqualTo("John Doe");
        assertThat(newStudent.getRoll()).isEqualTo("CS001");
        assertThat(newStudent.getEmail()).isEqualTo("john@test.com");
        assertThat(newStudent.getPassword()).isEqualTo("password123");
    }

    // ==================== Getter and Setter Tests ====================

    @Test
    @DisplayName("Should set and get ID correctly")
    void whenSetId_thenGetIdReturnsCorrectValue() {
        // Act
        student.setId(1L);

        // Assert
        assertThat(student.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should set and get name correctly")
    void whenSetName_thenGetNameReturnsCorrectValue() {
        // Act
        student.setName("John Doe");

        // Assert
        assertThat(student.getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should set and get email correctly")
    void whenSetEmail_thenGetEmailReturnsCorrectValue() {
        // Act
        student.setEmail("john@test.com");

        // Assert
        assertThat(student.getEmail()).isEqualTo("john@test.com");
    }

    @Test
    @DisplayName("Should set and get roll correctly")
    void whenSetRoll_thenGetRollReturnsCorrectValue() {
        // Act
        student.setRoll("CS001");

        // Assert
        assertThat(student.getRoll()).isEqualTo("CS001");
    }

    @Test
    @DisplayName("Should set and get password correctly")
    void whenSetPassword_thenGetPasswordReturnsCorrectValue() {
        // Act
        student.setPassword("securePassword");

        // Assert
        assertThat(student.getPassword()).isEqualTo("securePassword");
    }

    @Test
    @DisplayName("Should set and get department correctly")
    void whenSetDepartment_thenGetDepartmentReturnsCorrectValue() {
        // Act
        student.setDepartment(department);

        // Assert
        assertThat(student.getDepartment()).isNotNull();
        assertThat(student.getDepartment()).isEqualTo(department);
        assertThat(student.getDepartment().getName()).isEqualTo("Computer Science");
    }

    @Test
    @DisplayName("Should get default role as STUDENT")
    void whenGetRole_thenDefaultRoleIsStudent() {
        // Assert
        assertThat(student.getRole()).isEqualTo(Role.STUDENT);
    }

    @Test
    @DisplayName("Should initialize courses as empty set")
    void whenGetCourses_thenReturnsEmptySet() {
        // Assert
        assertThat(student.getCourses()).isNotNull();
        assertThat(student.getCourses()).isEmpty();
    }

    @Test
    @DisplayName("Should initialize teachers as empty set")
    void whenGetTeachers_thenReturnsEmptySet() {
        // Assert
        assertThat(student.getTeachers()).isNotNull();
        assertThat(student.getTeachers()).isEmpty();
    }

    // ==================== Relationship Tests ====================

    @Test
    @DisplayName("Should set courses collection correctly")
    void whenSetCourses_thenGetCoursesReturnsCorrectValue() {
        // Arrange
        Set<Course> courses = new HashSet<>();
        courses.add(course);

        // Act
        student.setCourses(courses);

        // Assert
        assertThat(student.getCourses()).hasSize(1);
        assertThat(student.getCourses()).contains(course);
    }

    @Test
    @DisplayName("Should set teachers collection correctly")
    void whenSetTeachers_thenGetTeachersReturnsCorrectValue() {
        // Arrange
        Set<Teacher> teachers = new HashSet<>();
        teachers.add(teacher);

        // Act
        student.setTeachers(teachers);

        // Assert
        assertThat(student.getTeachers()).hasSize(1);
        assertThat(student.getTeachers()).contains(teacher);
    }

    // ==================== Helper Method Tests ====================

    @Test
    @DisplayName("Should add course successfully using helper method")
    void whenAddCourse_thenCourseIsAddedToStudent() {
        // Act
        student.addCourse(course);

        // Assert
        assertThat(student.getCourses()).hasSize(1);
        assertThat(student.getCourses()).contains(course);
        assertThat(course.getStudents()).contains(student); // Bidirectional relationship
    }

    @Test
    @DisplayName("Should remove course successfully using helper method")
    void whenRemoveCourse_thenCourseIsRemovedFromStudent() {
        // Arrange
        student.addCourse(course);
        assertThat(student.getCourses()).hasSize(1);

        // Act
        student.removeCourse(course);

        // Assert
        assertThat(student.getCourses()).isEmpty();
        assertThat(course.getStudents()).doesNotContain(student); // Bidirectional relationship
    }

    @Test
    @DisplayName("Should maintain bidirectional relationship when adding course")
    void whenAddCourse_thenBidirectionalRelationshipIsMaintained() {
        // Act
        student.addCourse(course);

        // Assert - Student side
        assertThat(student.getCourses()).contains(course);

        // Assert - Course side
        assertThat(course.getStudents()).contains(student);
    }

    @Test
    @DisplayName("Should maintain bidirectional relationship when removing course")
    void whenRemoveCourse_thenBidirectionalRelationshipIsMaintained() {
        // Arrange
        student.addCourse(course);

        // Act
        student.removeCourse(course);

        // Assert - Student side
        assertThat(student.getCourses()).doesNotContain(course);

        // Assert - Course side
        assertThat(course.getStudents()).doesNotContain(student);
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("Should handle null department gracefully")
    void whenSetNullDepartment_thenDepartmentIsNull() {
        // Act
        student.setDepartment(null);

        // Assert
        assertThat(student.getDepartment()).isNull();
    }

    @Test
    @DisplayName("Should handle empty name")
    void whenSetEmptyName_thenNameIsEmpty() {
        // Act
        student.setName("");

        // Assert
        assertThat(student.getName()).isEmpty();
    }

    @Test
    @DisplayName("Should handle adding multiple courses")
    void whenAddMultipleCourses_thenAllCoursesAreAdded() {
        // Arrange
        Course course1 = new Course();
        course1.setId(1L);
        course1.setName("Course 1");
        course1.setStudents(new HashSet<>());

        Course course2 = new Course();
        course2.setId(2L);
        course2.setName("Course 2");
        course2.setStudents(new HashSet<>());

        // Act
        student.addCourse(course1);
        student.addCourse(course2);

        // Assert
        assertThat(student.getCourses()).hasSize(2);
        assertThat(student.getCourses()).containsExactlyInAnyOrder(course1, course2);
    }

    @Test
    @DisplayName("Should handle removing non-existent course gracefully")
    void whenRemoveNonExistentCourse_thenNoExceptionIsThrown() {
        // Arrange
        Course nonExistentCourse = new Course();
        nonExistentCourse.setId(999L);
        nonExistentCourse.setStudents(new HashSet<>());

        // Act & Assert - Should not throw exception
        student.removeCourse(nonExistentCourse);
        assertThat(student.getCourses()).isEmpty();
    }

    // ==================== Validation Tests ====================

    @Test
    @DisplayName("Should allow setting long names")
    void whenSetLongName_thenNameIsSet() {
        // Arrange
        String longName = "A".repeat(100);

        // Act
        student.setName(longName);

        // Assert
        assertThat(student.getName()).hasSize(100);
    }

    @Test
    @DisplayName("Should allow setting valid email format")
    void whenSetValidEmail_thenEmailIsSet() {
        // Act
        student.setEmail("test.user+tag@example.co.uk");

        // Assert
        assertThat(student.getEmail()).isEqualTo("test.user+tag@example.co.uk");
    }

    @Test
    @DisplayName("Should update existing field values")
    void whenUpdateFieldValue_thenNewValueIsSet() {
        // Arrange
        student.setName("Initial Name");
        student.setEmail("initial@test.com");

        // Act
        student.setName("Updated Name");
        student.setEmail("updated@test.com");

        // Assert
        assertThat(student.getName()).isEqualTo("Updated Name");
        assertThat(student.getEmail()).isEqualTo("updated@test.com");
    }

    // ==================== State Tests ====================

    @Test
    @DisplayName("Should maintain independent state for multiple instances")
    void whenCreateMultipleInstances_thenStatesAreIndependent() {
        // Arrange
        Student student1 = new Student();
        Student student2 = new Student();

        // Act
        student1.setName("Student 1");
        student1.setEmail("student1@test.com");

        student2.setName("Student 2");
        student2.setEmail("student2@test.com");

        // Assert
        assertThat(student1.getName()).isEqualTo("Student 1");
        assertThat(student2.getName()).isEqualTo("Student 2");
        assertThat(student1.getEmail()).isNotEqualTo(student2.getEmail());
    }
}
