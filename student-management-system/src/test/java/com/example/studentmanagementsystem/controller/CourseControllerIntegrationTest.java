package com.example.studentmanagementsystem.controller;

import com.example.studentmanagementsystem.dto.CourseDto;
import com.example.studentmanagementsystem.entity.Course;
import com.example.studentmanagementsystem.entity.Department;
import com.example.studentmanagementsystem.entity.Teacher;
import com.example.studentmanagementsystem.repository.CourseRepository;
import com.example.studentmanagementsystem.repository.DepartmentRepository;
import com.example.studentmanagementsystem.repository.StudentRepository;
import com.example.studentmanagementsystem.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for CourseController
 *
 * @SpringBootTest loads full application context
 * @AutoConfigureMockMvc configures MockMvc for testing controllers
 * @Transactional ensures each test runs in a transaction that rolls back
 *
 * Testing Strategy:
 * - Test HTTP endpoints with real Spring Security
 * - Verify request/response handling
 * - Test authorization rules
 * - Validate form submissions
 * - Test error handling
 *
 * Pattern: AAA (Arrange-Act-Assert)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("CourseController Integration Tests")
class CourseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    private Department testDepartment;
    private Teacher testTeacher;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        // Clean database - order matters due to FK constraints
        courseRepository.deleteAll();
        studentRepository.deleteAll();
        teacherRepository.deleteAll();
        departmentRepository.deleteAll();

        // Create test data
        testDepartment = new Department("Computer Science");
        testDepartment = departmentRepository.save(testDepartment);

        testTeacher = new Teacher("Dr. Smith", "smith@university.edu", 
                "$2a$10$X5wFuQoXe7KkJ8...");  // Encrypted password
        testTeacher.setDepartment(testDepartment);
        testTeacher = teacherRepository.save(testTeacher);

        testCourse = new Course("Data Structures", "Introduction to Data Structures");
        testCourse.setDepartment(testDepartment);
        testCourse.setTeacher(testTeacher);
        testCourse = courseRepository.save(testCourse);
    }

    @Nested
    @DisplayName("GET /courses - List Courses")
    class ListCourses {

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should return courses list page with courses")
        void whenListCourses_withTeacher_thenReturnCoursesPage() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/courses"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("courses/list"))
                    .andExpect(model().attributeExists("courses"))
                    .andExpect(model().attribute("courses", hasSize(1)))
                    .andExpect(model().attribute("courses", 
                            hasItem(hasProperty("name", is("Data Structures")))));
        }

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should return empty list when no courses exist")
        void whenListCourses_withNoCourses_thenReturnEmptyList() throws Exception {
            // Arrange
            courseRepository.deleteAll();

            // Act & Assert
            mockMvc.perform(get("/courses"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("courses/list"))
                    .andExpect(model().attributeExists("courses"))
                    .andExpect(model().attribute("courses", hasSize(0)));
        }

        @Test
        @DisplayName("Should redirect to login when not authenticated")
        void whenListCourses_withoutAuth_thenRedirectToLogin() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/courses"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @WithMockUser(username = "student@university.edu", roles = {"STUDENT"})
        @DisplayName("Should deny access for students")
        void whenListCourses_withStudent_thenAccessDenied() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/courses"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /courses/{id} - View Course")
    class ViewCourse {

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should return course view page")
        void whenViewCourse_withValidId_thenReturnCoursePage() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/courses/" + testCourse.getId()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("courses/view"))
                    .andExpect(model().attributeExists("course"))
                    .andExpect(model().attribute("course", 
                            hasProperty("name", is("Data Structures"))))
                    .andExpect(model().attribute("course", 
                            hasProperty("description", is("Introduction to Data Structures"))));
        }

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should handle non-existent course")
        void whenViewCourse_withInvalidId_thenHandleError() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/courses/999"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @DisplayName("Should redirect to login when not authenticated")
        void whenViewCourse_withoutAuth_thenRedirectToLogin() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/courses/" + testCourse.getId()))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("GET /courses/create - Create Course Form")
    class CreateCourseForm {

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should show create course form")
        void whenShowCreateForm_withTeacher_thenReturnForm() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/courses/create"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("courses/create"))
                    .andExpect(model().attributeExists("courseDto"))
                    .andExpect(model().attributeExists("departments"));
        }

        @Test
        @WithMockUser(username = "student@university.edu", roles = {"STUDENT"})
        @DisplayName("Should deny access for students")
        void whenShowCreateForm_withStudent_thenAccessDenied() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/courses/create"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should redirect to login when not authenticated")
        void whenShowCreateForm_withoutAuth_thenRedirectToLogin() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/courses/create"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("POST /courses/create - Create Course")
    class CreateCourse {

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should create course successfully with valid data")
        void whenCreateCourse_withValidData_thenCourseCreated() throws Exception {
            // Arrange
            long initialCount = courseRepository.count();

            // Act & Assert
            mockMvc.perform(post("/courses/create")
                            .with(csrf())
                            .param("name", "Database Systems")
                            .param("description", "Introduction to Databases")
                            .param("departmentId", testDepartment.getId().toString()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("/courses/*"));

            // Verify
            assertThat(courseRepository.count()).isEqualTo(initialCount + 1);
            assertThat(courseRepository.findAll())
                    .extracting(Course::getName)
                    .contains("Database Systems");
        }

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should reject course with missing name")
        void whenCreateCourse_withMissingName_thenValidationFails() throws Exception {
            // Arrange
            long initialCount = courseRepository.count();

            // Act & Assert
            mockMvc.perform(post("/courses/create")
                            .with(csrf())
                            .param("name", "")
                            .param("description", "Some description")
                            .param("departmentId", testDepartment.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("courses/create"))
                    .andExpect(model().attributeHasFieldErrors("courseDto", "name"));

            // Verify - No course created
            assertThat(courseRepository.count()).isEqualTo(initialCount);
        }

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should handle null department gracefully")
        void whenCreateCourse_withNullDepartment_thenCreateCourse() throws Exception {
            // Arrange
            long initialCount = courseRepository.count();

            // Act & Assert
            mockMvc.perform(post("/courses/create")
                            .with(csrf())
                            .param("name", "Generic Course")
                            .param("description", "Course without department"))
                    .andExpect(status().is3xxRedirection());

            // Verify
            assertThat(courseRepository.count()).isEqualTo(initialCount + 1);
        }

        @Test
        @WithMockUser(username = "student@university.edu", roles = {"STUDENT"})
        @DisplayName("Should deny access for students")
        void whenCreateCourse_withStudent_thenAccessDenied() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/courses/create")
                            .with(csrf())
                            .param("name", "Unauthorized Course")
                            .param("description", "This should not be created"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should require CSRF token")
        void whenCreateCourse_withoutCsrf_thenForbidden() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/courses/create")
                            .param("name", "Test Course")
                            .param("description", "Test Description"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /courses/{id}/edit - Edit Course Form")
    class EditCourseForm {

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should show edit course form")
        void whenShowEditForm_withValidId_thenReturnForm() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/courses/" + testCourse.getId() + "/edit"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("courses/edit"))
                    .andExpect(model().attributeExists("course"))
                    .andExpect(model().attributeExists("courseDto"))
                    .andExpect(model().attributeExists("departments"))
                    .andExpect(model().attribute("course", 
                            hasProperty("name", is("Data Structures"))));
        }

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should handle non-existent course")
        void whenShowEditForm_withInvalidId_thenHandleError() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/courses/999/edit"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @WithMockUser(username = "student@university.edu", roles = {"STUDENT"})
        @DisplayName("Should deny access for students")
        void whenShowEditForm_withStudent_thenAccessDenied() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/courses/" + testCourse.getId() + "/edit"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("POST /courses/{id}/edit - Update Course")
    class UpdateCourse {

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should update course successfully")
        void whenUpdateCourse_withValidData_thenCourseUpdated() throws Exception {
            // Act
            mockMvc.perform(post("/courses/" + testCourse.getId() + "/edit")
                            .with(csrf())
                            .param("name", "Advanced Data Structures")
                            .param("description", "Advanced topics in DS")
                            .param("departmentId", testDepartment.getId().toString()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/courses/" + testCourse.getId()));

            // Assert
            Course updatedCourse = courseRepository.findById(testCourse.getId()).orElseThrow();
            assertThat(updatedCourse.getName()).isEqualTo("Advanced Data Structures");
            assertThat(updatedCourse.getDescription()).isEqualTo("Advanced topics in DS");
        }

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should reject update with invalid data")
        void whenUpdateCourse_withInvalidData_thenValidationFails() throws Exception {
            // Arrange
            String originalName = testCourse.getName();

            // Act
            mockMvc.perform(post("/courses/" + testCourse.getId() + "/edit")
                            .with(csrf())
                            .param("name", "")  // Invalid: empty name
                            .param("description", "Some description"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("courses/edit"))
                    .andExpect(model().attributeHasFieldErrors("courseDto", "name"));

            // Assert - Course not updated
            Course unchangedCourse = courseRepository.findById(testCourse.getId()).orElseThrow();
            assertThat(unchangedCourse.getName()).isEqualTo(originalName);
        }

        @Test
        @WithMockUser(username = "student@university.edu", roles = {"STUDENT"})
        @DisplayName("Should deny access for students")
        void whenUpdateCourse_withStudent_thenAccessDenied() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/courses/" + testCourse.getId() + "/edit")
                            .with(csrf())
                            .param("name", "Hacked Name")
                            .param("description", "Unauthorized update"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("POST /courses/{id}/delete - Delete Course")
    class DeleteCourse {

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should delete course successfully")
        void whenDeleteCourse_withValidId_thenCourseDeleted() throws Exception {
            // Arrange
            Long courseId = testCourse.getId();

            // Act
            mockMvc.perform(post("/courses/" + courseId + "/delete")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/courses"));

            // Assert
            assertThat(courseRepository.findById(courseId)).isEmpty();
        }

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should handle deletion of non-existent course")
        void whenDeleteCourse_withInvalidId_thenHandleError() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/courses/999/delete")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @WithMockUser(username = "student@university.edu", roles = {"STUDENT"})
        @DisplayName("Should deny access for students")
        void whenDeleteCourse_withStudent_thenAccessDenied() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/courses/" + testCourse.getId() + "/delete")
                            .with(csrf()))
                    .andExpect(status().isForbidden());

            // Verify course still exists
            assertThat(courseRepository.findById(testCourse.getId())).isPresent();
        }

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should require CSRF token")
        void whenDeleteCourse_withoutCsrf_thenForbidden() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/courses/" + testCourse.getId() + "/delete"))
                    .andExpect(status().isForbidden());

            // Verify course still exists
            assertThat(courseRepository.findById(testCourse.getId())).isPresent();
        }
    }

    @Nested
    @DisplayName("Authorization Tests")
    class AuthorizationTests {

        @Test
        @DisplayName("Should require authentication for all endpoints")
        void whenAccessEndpoints_withoutAuth_thenRedirectToLogin() throws Exception {
            mockMvc.perform(get("/courses")).andExpect(status().is3xxRedirection());
            mockMvc.perform(get("/courses/" + testCourse.getId())).andExpect(status().is3xxRedirection());
            mockMvc.perform(get("/courses/create")).andExpect(status().is3xxRedirection());
            mockMvc.perform(get("/courses/" + testCourse.getId() + "/edit")).andExpect(status().is3xxRedirection());
        }

        @Test
        @WithMockUser(username = "student@university.edu", roles = {"STUDENT"})
        @DisplayName("Should deny students access to course management")
        void whenAccessCourseManagement_withStudent_thenAccessDenied() throws Exception {
            mockMvc.perform(get("/courses")).andExpect(status().isForbidden());
            mockMvc.perform(get("/courses/create")).andExpect(status().isForbidden());
            mockMvc.perform(post("/courses/create").with(csrf())).andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should allow teachers full access to course management")
        void whenAccessCourseManagement_withTeacher_thenAllowed() throws Exception {
            mockMvc.perform(get("/courses")).andExpect(status().isOk());
            mockMvc.perform(get("/courses/" + testCourse.getId())).andExpect(status().isOk());
            mockMvc.perform(get("/courses/create")).andExpect(status().isOk());
            mockMvc.perform(get("/courses/" + testCourse.getId() + "/edit")).andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should handle database errors gracefully")
        void whenDatabaseError_thenHandleGracefully() throws Exception {
            // Act & Assert - Try to create course with invalid department ID
            mockMvc.perform(post("/courses/create")
                            .with(csrf())
                            .param("name", "Test Course")
                            .param("description", "Test Description")
                            .param("departmentId", "99999"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @WithMockUser(username = "smith@university.edu", roles = {"TEACHER"})
        @DisplayName("Should validate required fields")
        void whenRequiredFieldsMissing_thenShowValidationErrors() throws Exception {
            mockMvc.perform(post("/courses/create")
                            .with(csrf())
                            .param("name", "")
                            .param("description", ""))
                    .andExpect(status().isOk())
                    .andExpect(view().name("courses/create"))
                    .andExpect(model().attributeHasErrors("courseDto"));
        }
    }
}
