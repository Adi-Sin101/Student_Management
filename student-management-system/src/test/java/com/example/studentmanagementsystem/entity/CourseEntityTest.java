package com.example.studentmanagementsystem.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Course entity
 * 
 * Testing Strategy:
 * - Test entity creation and initialization
 * - Verify getters and setters
 * - Test relationship mappings
 * - Validate business logic in helper methods
 * 
 * Pattern: AAA (Arrange-Act-Assert)
 */
@DisplayName("Course Entity Tests")
class CourseEntityTest {

    private Course course;
    private Department department;
    private Teacher teacher;
    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        // Arrange - Create test data
        department = new Department("Computer Science");
        department.setId(1L);

        teacher = new Teacher("Dr. Smith", "smith@university.edu", "password123");
        teacher.setId(1L);
        teacher.setDepartment(department);

        student1 = new Student("Alice Johnson", "CS2021001", "alice@university.edu", "password123");
        student1.setId(1L);

        student2 = new Student("Bob Williams", "CS2021002", "bob@university.edu", "password123");
        student2.setId(2L);

        course = new Course("Data Structures", "Introduction to Data Structures and Algorithms");
        course.setId(1L);
        course.setDepartment(department);
        course.setTeacher(teacher);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create course with default constructor")
        void whenCreateCourseWithDefaultConstructor_thenFieldsAreInitialized() {
            // Act
            Course newCourse = new Course();

            // Assert
            assertThat(newCourse).isNotNull();
            assertThat(newCourse.getStudents()).isNotNull();
            assertThat(newCourse.getStudents()).isEmpty();
        }

        @Test
        @DisplayName("Should create course with parameterized constructor")
        void whenCreateCourseWithParameters_thenFieldsAreSet() {
            // Act
            Course newCourse = new Course("Database Systems", "Advanced Database Management");

            // Assert
            assertThat(newCourse).isNotNull();
            assertThat(newCourse.getName()).isEqualTo("Database Systems");
            assertThat(newCourse.getDescription()).isEqualTo("Advanced Database Management");
            assertThat(newCourse.getStudents()).isNotNull();
            assertThat(newCourse.getStudents()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get ID")
        void whenSetId_thenGetIdReturnsCorrectValue() {
            // Arrange
            Long expectedId = 99L;

            // Act
            course.setId(expectedId);

            // Assert
            assertThat(course.getId()).isEqualTo(expectedId);
        }

        @Test
        @DisplayName("Should set and get name")
        void whenSetName_thenGetNameReturnsCorrectValue() {
            // Arrange
            String expectedName = "Machine Learning";

            // Act
            course.setName(expectedName);

            // Assert
            assertThat(course.getName()).isEqualTo(expectedName);
        }

        @Test
        @DisplayName("Should set and get description")
        void whenSetDescription_thenGetDescriptionReturnsCorrectValue() {
            // Arrange
            String expectedDescription = "Introduction to ML algorithms";

            // Act
            course.setDescription(expectedDescription);

            // Assert
            assertThat(course.getDescription()).isEqualTo(expectedDescription);
        }

        @Test
        @DisplayName("Should set and get department")
        void whenSetDepartment_thenGetDepartmentReturnsCorrectValue() {
            // Arrange
            Department newDepartment = new Department("Mathematics");
            newDepartment.setId(2L);

            // Act
            course.setDepartment(newDepartment);

            // Assert
            assertThat(course.getDepartment()).isEqualTo(newDepartment);
            assertThat(course.getDepartment().getName()).isEqualTo("Mathematics");
        }

        @Test
        @DisplayName("Should set and get teacher")
        void whenSetTeacher_thenGetTeacherReturnsCorrectValue() {
            // Arrange
            Teacher newTeacher = new Teacher("Prof. Johnson", "johnson@university.edu", "pass456");
            newTeacher.setId(2L);

            // Act
            course.setTeacher(newTeacher);

            // Assert
            assertThat(course.getTeacher()).isEqualTo(newTeacher);
            assertThat(course.getTeacher().getName()).isEqualTo("Prof. Johnson");
        }

        @Test
        @DisplayName("Should set and get students collection")
        void whenSetStudents_thenGetStudentsReturnsCorrectCollection() {
            // Arrange
            HashSet<Student> students = new HashSet<>();
            students.add(student1);
            students.add(student2);

            // Act
            course.setStudents(students);

            // Assert
            assertThat(course.getStudents()).hasSize(2);
            assertThat(course.getStudents()).contains(student1, student2);
        }
    }

    @Nested
    @DisplayName("Relationship Tests")
    class RelationshipTests {

        @Test
        @DisplayName("Should maintain bidirectional relationship with department")
        void whenSetDepartment_thenBidirectionalRelationshipIsEstablished() {
            // Arrange
            Department dept = new Department("Physics");
            dept.setId(3L);

            // Act
            course.setDepartment(dept);

            // Assert
            assertThat(course.getDepartment()).isNotNull();
            assertThat(course.getDepartment()).isEqualTo(dept);
            assertThat(course.getDepartment().getName()).isEqualTo("Physics");
        }

        @Test
        @DisplayName("Should maintain bidirectional relationship with teacher")
        void whenSetTeacher_thenBidirectionalRelationshipIsEstablished() {
            // Arrange
            Teacher newTeacher = new Teacher("Dr. Brown", "brown@university.edu", "pass789");
            newTeacher.setId(3L);

            // Act
            course.setTeacher(newTeacher);

            // Assert
            assertThat(course.getTeacher()).isNotNull();
            assertThat(course.getTeacher()).isEqualTo(newTeacher);
        }

        @Test
        @DisplayName("Should support many-to-many relationship with students")
        void whenAddStudents_thenManyToManyRelationshipWorks() {
            // Arrange & Act
            HashSet<Student> students = new HashSet<>();
            students.add(student1);
            students.add(student2);
            course.setStudents(students);

            // Assert
            assertThat(course.getStudents()).hasSize(2);
            assertThat(course.getStudents()).containsExactlyInAnyOrder(student1, student2);
        }

        @Test
        @DisplayName("Should handle null relationships gracefully")
        void whenSetNullRelationships_thenNoExceptionThrown() {
            // Act
            course.setDepartment(null);
            course.setTeacher(null);
            course.setStudents(new HashSet<>());

            // Assert
            assertThat(course.getDepartment()).isNull();
            assertThat(course.getTeacher()).isNull();
            assertThat(course.getStudents()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should initialize with empty student collection")
        void whenCourseCreated_thenStudentsCollectionIsEmpty() {
            // Arrange & Act
            Course newCourse = new Course("New Course", "Description");

            // Assert
            assertThat(newCourse.getStudents()).isNotNull();
            assertThat(newCourse.getStudents()).isEmpty();
        }

        @Test
        @DisplayName("Should allow multiple students to enroll")
        void whenMultipleStudentsAdded_thenAllArePresent() {
            // Arrange
            HashSet<Student> students = new HashSet<>();
            for (int i = 1; i <= 5; i++) {
                Student student = new Student("Student" + i, "CS202100" + i, 
                    "student" + i + "@university.edu", "pass" + i);
                student.setId((long) i);
                students.add(student);
            }

            // Act
            course.setStudents(students);

            // Assert
            assertThat(course.getStudents()).hasSize(5);
        }

        @Test
        @DisplayName("Should prevent duplicate students using Set")
        void whenDuplicateStudentAdded_thenSetPreventsIt() {
            // Arrange
            HashSet<Student> students = new HashSet<>();
            students.add(student1);
            students.add(student1); // Adding same student twice

            // Act
            course.setStudents(students);

            // Assert
            assertThat(course.getStudents()).hasSize(1); // Set prevents duplicates
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle null name gracefully")
        void whenNameIsNull_thenNoExceptionThrown() {
            // Act & Assert
            course.setName(null);
            assertThat(course.getName()).isNull();
        }

        @Test
        @DisplayName("Should handle null description gracefully")
        void whenDescriptionIsNull_thenNoExceptionThrown() {
            // Act & Assert
            course.setDescription(null);
            assertThat(course.getDescription()).isNull();
        }

        @Test
        @DisplayName("Should handle empty string name")
        void whenNameIsEmpty_thenValueIsStored() {
            // Arrange
            String emptyName = "";

            // Act
            course.setName(emptyName);

            // Assert
            assertThat(course.getName()).isEmpty();
        }

        @Test
        @DisplayName("Should handle very long description")
        void whenDescriptionIsVeryLong_thenValueIsStored() {
            // Arrange
            String longDescription = "A".repeat(1000);

            // Act
            course.setDescription(longDescription);

            // Assert
            assertThat(course.getDescription()).hasSize(1000);
        }
    }

    @Nested
    @DisplayName("Object State Tests")
    class ObjectStateTests {

        @Test
        @DisplayName("Should maintain consistent state after multiple updates")
        void whenMultipleUpdates_thenStateRemainsConsistent() {
            // Act
            course.setName("Updated Name");
            course.setDescription("Updated Description");
            course.setDepartment(department);
            course.setTeacher(teacher);

            HashSet<Student> students = new HashSet<>();
            students.add(student1);
            course.setStudents(students);

            // Assert
            assertThat(course.getName()).isEqualTo("Updated Name");
            assertThat(course.getDescription()).isEqualTo("Updated Description");
            assertThat(course.getDepartment()).isEqualTo(department);
            assertThat(course.getTeacher()).isEqualTo(teacher);
            assertThat(course.getStudents()).hasSize(1);
            assertThat(course.getStudents()).contains(student1);
        }

        @Test
        @DisplayName("Should create new independent instance")
        void whenCreateNewCourse_thenIndependentFromOthers() {
            // Act
            Course course1 = new Course("Course 1", "Description 1");
            Course course2 = new Course("Course 2", "Description 2");

            // Assert
            assertThat(course1).isNotEqualTo(course2);
            assertThat(course1.getName()).isNotEqualTo(course2.getName());
        }
    }
}
