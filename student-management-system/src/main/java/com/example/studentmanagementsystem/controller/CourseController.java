package com.example.studentmanagementsystem.controller;

import com.example.studentmanagementsystem.dto.CourseDto;
import com.example.studentmanagementsystem.entity.Course;
import com.example.studentmanagementsystem.service.CourseService;
import com.example.studentmanagementsystem.service.DepartmentService;
import com.example.studentmanagementsystem.service.StudentService;
import com.example.studentmanagementsystem.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/courses")
@PreAuthorize("hasRole('TEACHER')")
public class CourseController {

    private final CourseService courseService;
    private final DepartmentService departmentService;
    private final TeacherService teacherService;
    private final StudentService studentService;

    public CourseController(CourseService courseService,
                            DepartmentService departmentService,
                            TeacherService teacherService,
                            StudentService studentService) {
        this.courseService = courseService;
        this.departmentService = departmentService;
        this.teacherService = teacherService;
        this.studentService = studentService;
    }

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "courses/list";
    }

    @GetMapping("/{id}")
    public String viewCourse(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("course", courseService.getCourseById(id));
            return "courses/view";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/courses";
        }
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("courseDto", new CourseDto());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "courses/create";
    }

    @PostMapping("/create")
    public String createCourse(@Valid @ModelAttribute("courseDto") CourseDto dto,
                               BindingResult result,
                               Authentication authentication,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "courses/create";
        }

        try {
            Course created = courseService.createCourse(dto, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Course created successfully in your department!");
            return "redirect:/courses/" + created.getId();
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "courses/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Course course = courseService.getCourseById(id);
            CourseDto dto = new CourseDto();
            dto.setId(course.getId());
            dto.setName(course.getName());
            dto.setDescription(course.getDescription());

            model.addAttribute("course", course);
            model.addAttribute("courseDto", dto);
            model.addAttribute("courseId", id);
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "courses/edit";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/courses";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateCourse(@PathVariable Long id,
                               @Valid @ModelAttribute("courseDto") CourseDto dto,
                               BindingResult result,
                               Authentication authentication,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("courseId", id);
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "courses/edit";
        }

        try {
            courseService.updateCourse(id, dto, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully!");
            return "redirect:/courses/" + id;
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("courseId", id);
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "courses/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteCourse(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/courses";
    }

    // REMOVED: Teachers cannot assign/remove students from courses
    // Students manage their own course enrollments
}
