package com.example.studentmanagementsystem.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseService Unit Tests")
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private CourseService courseService;

    private Department testDepartment;
    private Teacher testTeacher;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        testDepartment = new Department("Computer Science");
        testDepartment.setId(1L);

        testTeacher = new Teacher("Dr. Smith", "smith@test.com", "password");
        testTeacher.setId(1L);
        testTeacher.setDepartment(testDepartment);

        testCourse = new Course("Data Structures", "Intro to DS");
        testCourse.setId(1L);
        testCourse.setDepartment(testDepartment);
        testCourse.setTeacher(testTeacher);
    }

    @Test
    @DisplayName("getAllCourses should return all courses")
    void getAllCourses_shouldReturnAllCourses() {
        Course course2 = new Course("Algorithms", "Intro to Algorithms");
        when(courseRepository.findAll()).thenReturn(Arrays.asList(testCourse, course2));

        List<Course> result = courseService.getAllCourses();

        assertThat(result).hasSize(2);
        verify(courseRepository).findAll();
    }

    @Test
    @DisplayName("getCourseById should return course when found")
    void getCourseById_whenFound_shouldReturnCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        Course result = courseService.getCourseById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Data Structures");
    }

    @Test
    @DisplayName("getCourseById should throw when not found")
    void getCourseById_whenNotFound_shouldThrow() {
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getCourseById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Course not found");
    }

    @Test
    @DisplayName("getCoursesByDepartment should return courses for department")
    void getCoursesByDepartment_shouldReturnCourses() {
        when(courseRepository.findByDepartmentId(1L)).thenReturn(List.of(testCourse));

        List<Course> result = courseService.getCoursesByDepartment(1L);

        assertThat(result).hasSize(1);
        verify(courseRepository).findByDepartmentId(1L);
    }

    @Test
    @DisplayName("getCoursesByTeacher should return courses for teacher")
    void getCoursesByTeacher_shouldReturnCourses() {
        when(courseRepository.findByTeacherId(1L)).thenReturn(List.of(testCourse));

        List<Course> result = courseService.getCoursesByTeacher(1L);

        assertThat(result).hasSize(1);
        verify(courseRepository).findByTeacherId(1L);
    }

    @Test
    @DisplayName("createCourse should create course with teacher's department")
    void createCourse_shouldCreateWithTeacherDepartment() {
        CourseDto dto = new CourseDto();
        dto.setName("New Course");
        dto.setDescription("New Description");

        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> {
            Course c = inv.getArgument(0);
            c.setId(2L);
            return c;
        });

        Course result = courseService.createCourse(dto, "smith@test.com");

        assertThat(result.getName()).isEqualTo("New Course");
        assertThat(result.getDepartment()).isEqualTo(testDepartment);
        assertThat(result.getTeacher()).isEqualTo(testTeacher);
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    @DisplayName("createCourse should throw when teacher not found")
    void createCourse_whenTeacherNotFound_shouldThrow() {
        CourseDto dto = new CourseDto();
        dto.setName("New Course");
        when(teacherRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.createCourse(dto, "unknown@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Teacher not found");
    }

    @Test
    @DisplayName("createCourse should throw when teacher has no department")
    void createCourse_whenTeacherHasNoDepartment_shouldThrow() {
        CourseDto dto = new CourseDto();
        dto.setName("New Course");
        testTeacher.setDepartment(null);
        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));

        assertThatThrownBy(() -> courseService.createCourse(dto, "smith@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("department");
    }

    @Test
    @DisplayName("updateCourse should update course successfully")
    void updateCourse_shouldUpdateCourse() {
        CourseDto dto = new CourseDto();
        dto.setName("Updated Name");
        dto.setDescription("Updated Description");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        Course result = courseService.updateCourse(1L, dto, "smith@test.com");

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    @DisplayName("updateCourse should throw when course is in different department")
    void updateCourse_whenDifferentDepartment_shouldThrow() {
        CourseDto dto = new CourseDto();
        dto.setName("Updated");

        Department otherDept = new Department("Mathematics");
        otherDept.setId(2L);
        testCourse.setDepartment(otherDept);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));

        assertThatThrownBy(() -> courseService.updateCourse(1L, dto, "smith@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("your department");
    }

    @Test
    @DisplayName("deleteCourse should delete course in same department")
    void deleteCourse_shouldDeleteCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));

        courseService.deleteCourse(1L, "smith@test.com");

        verify(courseRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteCourse should throw when course is in different department")
    void deleteCourse_whenDifferentDepartment_shouldThrow() {
        Department otherDept = new Department("Mathematics");
        otherDept.setId(2L);
        testCourse.setDepartment(otherDept);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));

        assertThatThrownBy(() -> courseService.deleteCourse(1L, "smith@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("your department");
    }

    @Test
    @DisplayName("getCoursesInTeacherDepartment should return courses")
    void getCoursesInTeacherDepartment_shouldReturnCourses() {
        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));
        when(courseRepository.findByDepartmentId(1L)).thenReturn(List.of(testCourse));

        List<Course> result = courseService.getCoursesInTeacherDepartment("smith@test.com");

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getCoursesInTeacherDepartment should return empty when teacher has no department")
    void getCoursesInTeacherDepartment_whenNoDepartment_shouldReturnEmpty() {
        testTeacher.setDepartment(null);
        when(teacherRepository.findByEmail("smith@test.com")).thenReturn(Optional.of(testTeacher));

        List<Course> result = courseService.getCoursesInTeacherDepartment("smith@test.com");

        assertThat(result).isEmpty();
    }
}
