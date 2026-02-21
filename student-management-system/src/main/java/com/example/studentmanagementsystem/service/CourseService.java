package com.example.studentmanagementsystem.service;

import com.example.studentmanagementsystem.dto.CourseDto;
import com.example.studentmanagementsystem.entity.Course;
import com.example.studentmanagementsystem.entity.Department;
import com.example.studentmanagementsystem.entity.Student;
import com.example.studentmanagementsystem.entity.Teacher;
import com.example.studentmanagementsystem.repository.CourseRepository;
import com.example.studentmanagementsystem.repository.DepartmentRepository;
import com.example.studentmanagementsystem.repository.StudentRepository;
import com.example.studentmanagementsystem.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public CourseService(CourseRepository courseRepository,
                         DepartmentRepository departmentRepository,
                         TeacherRepository teacherRepository,
                         StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }

    public List<Course> getCoursesByDepartment(Long departmentId) {
        return courseRepository.findByDepartmentId(departmentId);
    }

    public List<Course> getCoursesByTeacher(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }

    public Course createCourse(CourseDto dto, String teacherEmail) {
        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Teachers can only create courses in their own department
        if (teacher.getDepartment() == null) {
            throw new RuntimeException("You must be assigned to a department to create courses");
        }

        Course course = new Course();
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());

        // Force the department to be the teacher's department
        course.setDepartment(teacher.getDepartment());

        // Set the teacher to the current teacher
        course.setTeacher(teacher);

        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, CourseDto dto, String teacherEmail) {
        Course course = getCourseById(id);
        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Teachers can only update courses in their own department
        if (teacher.getDepartment() == null) {
            throw new RuntimeException("You must be assigned to a department");
        }

        if (course.getDepartment() == null ||
            !course.getDepartment().getId().equals(teacher.getDepartment().getId())) {
            throw new RuntimeException("You can only edit courses in your department");
        }

        course.setName(dto.getName());
        course.setDescription(dto.getDescription());

        // Department cannot be changed - stays in teacher's department
        course.setDepartment(teacher.getDepartment());
        course.setTeacher(teacher);

        return courseRepository.save(course);
    }

    public void deleteCourse(Long id, String teacherEmail) {
        Course course = getCourseById(id);
        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Teachers can only delete courses in their own department
        if (teacher.getDepartment() == null || course.getDepartment() == null ||
            !course.getDepartment().getId().equals(teacher.getDepartment().getId())) {
            throw new RuntimeException("You can only delete courses in your department");
        }

        courseRepository.deleteById(id);
    }

    // Remove this method - students manage their own enrollments
    // Teachers should not be able to assign/remove students from courses
    public List<Course> getCoursesInTeacherDepartment(String teacherEmail) {
        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        if (teacher.getDepartment() == null) {
            return List.of();
        }
        return courseRepository.findByDepartmentId(teacher.getDepartment().getId());
    }
}
