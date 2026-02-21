package com.example.studentmanagementsystem.controller;

import com.example.studentmanagementsystem.entity.Department;
import com.example.studentmanagementsystem.entity.Student;
import com.example.studentmanagementsystem.entity.Teacher;
import com.example.studentmanagementsystem.repository.DepartmentRepository;
import com.example.studentmanagementsystem.repository.StudentRepository;
import com.example.studentmanagementsystem.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for StudentController
 *
 * @SpringBootTest: Loads full application context
 * @AutoConfigureMockMvc: Configures MockMvc for testing MVC controllers
 * @Transactional: Ensures test data is rolled back after each test
 *
 * Testing Strategy:
 * - Test HTTP endpoints with real Spring context
 * - Verify status codes (200, 302, 403, 404)
 * - Validate request/response bodies
 * - Test security and authorization
 * - Verify validation errors
 * - Test exception handling
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("StudentController Integration Tests")
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department testDepartment;
    private Student testStudent;
    private Teacher testTeacher;

    @BeforeEach
    void setUp() {
        // @Transactional will handle rollback, no need for explicit deleteAll()

        // Create test department (find or create to avoid unique constraint)
        testDepartment = departmentRepository.findByName("Computer Science")
                .orElseGet(() -> {
                    Department dept = new Department();
                    dept.setName("Computer Science");
                    return departmentRepository.save(dept);
                });

        // Create test student
        testStudent = new Student();
        testStudent.setName("John Doe");
        testStudent.setEmail("john.doe@test.com");
        testStudent.setRoll("CS001");
        testStudent.setPassword("$2a$10$EncodedPassword"); // BCrypt encoded
        testStudent.setDepartment(testDepartment);
        testStudent = studentRepository.save(testStudent);

        // Create test teacher
        testTeacher = new Teacher();
        testTeacher.setName("Dr. Smith");
        testTeacher.setEmail("smith@test.com");
        testTeacher.setPassword("$2a$10$EncodedPassword");
        testTeacher.setDepartment(testDepartment);
        testTeacher = teacherRepository.save(testTeacher);
    }

    // ==================== GET /students - List All Students ====================

    @Test
    @DisplayName("GET /students - Should redirect to login when not authenticated")
    void whenListStudents_withoutAuthentication_thenRedirectToLogin() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/students"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("GET /students - Should return 200 and student list for authenticated user")
    @WithMockUser(username = "john.doe@test.com", roles = "STUDENT")
    void whenListStudents_withAuthentication_thenReturnStudentList() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/students"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("students/list"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attribute("students", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(model().attributeExists("isTeacher"));
    }

    @Test
    @DisplayName("GET /students - Should set isTeacher flag correctly for teacher")
    @WithMockUser(username = "smith@test.com", roles = "TEACHER")
    void whenListStudents_asTeacher_thenIsTeacherFlagIsTrue() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/students"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("isTeacher", true));
    }

    @Test
    @DisplayName("GET /students - Should set isTeacher flag correctly for student")
    @WithMockUser(username = "john.doe@test.com", roles = "STUDENT")
    void whenListStudents_asStudent_thenIsTeacherFlagIsFalse() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/students"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("isTeacher", false));
    }

    // ==================== GET /students/{id} - View Student ====================

    @Test
    @DisplayName("GET /students/{id} - Should allow student to view own profile")
    @WithMockUser(username = "john.doe@test.com", roles = "STUDENT")
    void whenViewStudent_asStudentViewingOwnProfile_thenReturn200() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/students/{id}", testStudent.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("students/view"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attribute("student", hasProperty("email", is("john.doe@test.com"))))
                .andExpect(model().attribute("student", hasProperty("name", is("John Doe"))));
    }

    @Test
    @DisplayName("GET /students/{id} - Should deny student viewing another student's profile")
    @WithMockUser(username = "other.student@test.com", roles = "STUDENT")
    void whenViewStudent_asStudentViewingOtherProfile_thenRedirectWithError() throws Exception {
        // Arrange: Create another student
        Student otherStudent = new Student();
        otherStudent.setName("Other Student");
        otherStudent.setEmail("other.student@test.com");
        otherStudent.setRoll("CS002");
        otherStudent.setPassword("$2a$10$EncodedPassword");
        otherStudent.setDepartment(testDepartment);
        otherStudent = studentRepository.save(otherStudent);

        // Act & Assert
        mockMvc.perform(get("/students/{id}", testStudent.getId()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students?error=access_denied"));
    }

    @Test
    @DisplayName("GET /students/{id} - Should allow teacher to view student in same department")
    @WithMockUser(username = "smith@test.com", roles = "TEACHER")
    void whenViewStudent_asTeacherInSameDepartment_thenReturn200() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/students/{id}", testStudent.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("students/view"))
                .andExpect(model().attributeExists("student"));
    }

    @Test
    @DisplayName("GET /students/{id} - Should redirect with error for non-existent student ID")
    @WithMockUser(username = "john.doe@test.com", roles = "STUDENT")
    void whenViewStudent_withNonExistentId_thenRedirectWithError() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/students/{id}", 999L))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/students?error=*"));
    }

    // ==================== POST /students/{studentId}/courses/{courseId}/enroll ====================

    @Test
    @DisplayName("POST /students/{studentId}/courses/{courseId}/enroll - Should redirect to login when not authenticated")
    void whenEnrollInCourse_withoutAuthentication_thenRedirectToLogin() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/students/{studentId}/courses/{courseId}/enroll", testStudent.getId(), 1L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("POST /students/{studentId}/courses/{courseId}/enroll - Should require CSRF token")
    @WithMockUser(username = "john.doe@test.com", roles = "STUDENT")
    void whenEnrollInCourse_withoutCsrf_thenReturn403() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/students/{studentId}/courses/{courseId}/enroll", testStudent.getId(), 1L))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // ==================== POST /students/{studentId}/courses/{courseId}/unenroll ====================

    @Test
    @DisplayName("POST /students/{studentId}/courses/{courseId}/unenroll - Should redirect to login when not authenticated")
    void whenUnenrollFromCourse_withoutAuthentication_thenRedirectToLogin() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/students/{studentId}/courses/{courseId}/unenroll", testStudent.getId(), 1L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("POST /students/{studentId}/courses/{courseId}/unenroll - Should require CSRF token")
    @WithMockUser(username = "john.doe@test.com", roles = "STUDENT")
    void whenUnenrollFromCourse_withoutCsrf_thenReturn403() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/students/{studentId}/courses/{courseId}/unenroll", testStudent.getId(), 1L))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // ==================== GET /students/{id}/courses - Manage Courses ====================

    @Test
    @DisplayName("GET /students/{id}/courses - Should allow student to access own course management")
    @WithMockUser(username = "john.doe@test.com", roles = "STUDENT")
    void whenManageCourses_asStudentManagingOwnCourses_thenReturn200() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/students/{id}/courses", testStudent.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("students/manage-courses"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeExists("allCourses"))
                .andExpect(model().attributeExists("enrolledCourseIds"));
    }

    @Test
    @DisplayName("GET /students/{id}/courses - Should deny teacher access")
    @WithMockUser(username = "smith@test.com", roles = "TEACHER")
    void whenManageCourses_asTeacher_thenRedirectWithError() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/students/{id}/courses", testStudent.getId()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/students/" + testStudent.getId() + "?error=*"));
    }

    @Test
    @DisplayName("GET /students/{id}/courses - Should deny other students access")
    @WithMockUser(username = "other.student@test.com", roles = "STUDENT")
    void whenManageCourses_asOtherStudent_thenRedirectWithError() throws Exception {
        // Arrange: Create another student
        Student otherStudent = new Student();
        otherStudent.setName("Other Student");
        otherStudent.setEmail("other.student@test.com");
        otherStudent.setRoll("CS002");
        otherStudent.setPassword("$2a$10$EncodedPassword");
        otherStudent.setDepartment(testDepartment);
        otherStudent = studentRepository.save(otherStudent);

        // Act & Assert
        mockMvc.perform(get("/students/{id}/courses", testStudent.getId()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/students/" + testStudent.getId() + "?error=*"));
    }

    // ==================== Security Tests ====================

    @Test
    @DisplayName("Should enforce authentication on all student endpoints")
    void whenAccessAnyEndpoint_withoutAuthentication_thenRedirectToLogin() throws Exception {
        // List students
        mockMvc.perform(get("/students"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        // View student
        mockMvc.perform(get("/students/{id}", testStudent.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        // Manage courses
        mockMvc.perform(get("/students/{id}/courses", testStudent.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Should enforce CSRF protection on POST requests")
    @WithMockUser(username = "john.doe@test.com", roles = "STUDENT")
    void whenPostRequest_withoutCsrf_thenReturn403() throws Exception {
        // Enroll without CSRF
        mockMvc.perform(post("/students/{studentId}/courses/{courseId}/enroll", testStudent.getId(), 1L))
                .andExpect(status().isForbidden());

        // Unenroll without CSRF
        mockMvc.perform(post("/students/{studentId}/courses/{courseId}/unenroll", testStudent.getId(), 1L))
                .andExpect(status().isForbidden());
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("Should handle invalid student ID gracefully")
    @WithMockUser(username = "john.doe@test.com", roles = "STUDENT")
    void whenAccessStudent_withInvalidId_thenHandleGracefully() throws Exception {
        // Act & Assert - negative ID should redirect with error
        mockMvc.perform(get("/students/{id}", -1L))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle SQL injection attempts safely")
    @WithMockUser(username = "john.doe@test.com", roles = "STUDENT")
    void whenAccessStudent_withSqlInjectionAttempt_thenHandleSafely() throws Exception {
        // Act & Assert - Spring MVC should convert path variable safely, resulting in 400 Bad Request
        mockMvc.perform(get("/students/{id}", "1' OR '1'='1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ==================== Response Content Tests ====================

    @Test
    @DisplayName("Should return correct student data in model")
    @WithMockUser(username = "john.doe@test.com", roles = "STUDENT")
    void whenViewStudent_thenReturnCorrectStudentData() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/students/{id}", testStudent.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("student", hasProperty("id", is(testStudent.getId()))))
                .andExpect(model().attribute("student", hasProperty("name", is("John Doe"))))
                .andExpect(model().attribute("student", hasProperty("email", is("john.doe@test.com"))))
                .andExpect(model().attribute("student", hasProperty("roll", is("CS001"))));
    }

    @Test
    @DisplayName("Should include all required model attributes for course management")
    @WithMockUser(username = "john.doe@test.com", roles = "STUDENT")
    void whenManageCourses_thenIncludeAllRequiredAttributes() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/students/{id}/courses", testStudent.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeExists("allCourses"))
                .andExpect(model().attributeExists("enrolledCourseIds"))
                .andExpect(model().attribute("student", hasProperty("id", is(testStudent.getId()))));
    }
}
