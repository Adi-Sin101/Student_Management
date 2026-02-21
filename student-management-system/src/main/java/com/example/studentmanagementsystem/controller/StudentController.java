package com.example.studentmanagementsystem.controller;

import com.example.studentmanagementsystem.dto.StudentCreateDto;
import com.example.studentmanagementsystem.dto.StudentUpdateDto;
import com.example.studentmanagementsystem.entity.Student;
import com.example.studentmanagementsystem.service.CourseService;
import com.example.studentmanagementsystem.service.DepartmentService;
import com.example.studentmanagementsystem.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final DepartmentService departmentService;
    private final CourseService courseService;

    public StudentController(StudentService studentService,
                            DepartmentService departmentService,
                            CourseService courseService) {
        this.studentService = studentService;
        this.departmentService = departmentService;
        this.courseService = courseService;
    }

    @GetMapping
    public String listStudents(Model model, Authentication authentication) {
        boolean isTeacher = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER"));
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("isTeacher", isTeacher);
        return "students/list";
    }

    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Long id, Model model, Authentication authentication) {
        boolean isTeacher = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER"));
        String currentEmail = authentication.getName();

        try {
            if (!studentService.canAccess(id, currentEmail, isTeacher)) {
                return "redirect:/students?error=access_denied";
            }

            model.addAttribute("student", studentService.getStudentById(id));
            model.addAttribute("isTeacher", isTeacher);
            return "students/view";
        } catch (RuntimeException e) {
            return "redirect:/students?error=not_found";
        }
    }

    // REMOVED: Teachers cannot create students - only via registration
    // REMOVED: Teachers cannot delete students

    // Students manage their own course enrollments
    @GetMapping("/{id}/courses")
    public String manageCourses(@PathVariable Long id, Model model, Authentication authentication) {
        boolean isTeacher = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER"));
        String currentEmail = authentication.getName();

        // Only the student themselves can manage their courses
        if (isTeacher || !studentService.canAccess(id, currentEmail, false)) {
            return "redirect:/students/" + id + "?error=access_denied";
        }

        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        model.addAttribute("allCourses", courseService.getAllCourses());
        model.addAttribute("enrolledCourseIds", student.getCourses().stream().map(c -> c.getId()).toList());
        return "students/manage-courses";
    }

    @PostMapping("/{studentId}/courses/{courseId}/enroll")
    public String enrollInCourse(@PathVariable Long studentId,
                                 @PathVariable Long courseId,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            studentService.enrollInCourse(studentId, courseId, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Successfully enrolled in course!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/students/" + studentId;
    }

    @PostMapping("/{studentId}/courses/{courseId}/unenroll")
    public String unenrollFromCourse(@PathVariable Long studentId,
                                    @PathVariable Long courseId,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            studentService.unenrollFromCourse(studentId, courseId, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Successfully unenrolled from course!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/students/" + studentId;
    }
}
