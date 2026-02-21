package com.example.studentmanagementsystem.controller;

import com.example.studentmanagementsystem.dto.StudentCreateDto;
import com.example.studentmanagementsystem.dto.TeacherDto;
import com.example.studentmanagementsystem.service.DepartmentService;
import com.example.studentmanagementsystem.service.StudentService;
import com.example.studentmanagementsystem.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final DepartmentService departmentService;

    public RegistrationController(StudentService studentService,
                                   TeacherService teacherService,
                                   DepartmentService departmentService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public String showRegistrationChoice() {
        return "register/choice";
    }

    // Student Registration
    @GetMapping("/student")
    public String showStudentRegistrationForm(Model model) {
        model.addAttribute("studentDto", new StudentCreateDto());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "register/student";
    }

    @PostMapping("/student")
    public String registerStudent(@Valid @ModelAttribute("studentDto") StudentCreateDto dto,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "register/student";
        }

        try {
            studentService.createStudent(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                "Registration successful! You can now login with your email and password.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "register/student";
        }
    }

    // Teacher Registration
    @GetMapping("/teacher")
    public String showTeacherRegistrationForm(Model model) {
        model.addAttribute("teacherDto", new TeacherDto());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "register/teacher";
    }

    @PostMapping("/teacher")
    public String registerTeacher(@Valid @ModelAttribute("teacherDto") TeacherDto dto,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "register/teacher";
        }

        try {
            teacherService.createTeacher(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                "Registration successful! You can now login with your email and password.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "register/teacher";
        }
    }
}
