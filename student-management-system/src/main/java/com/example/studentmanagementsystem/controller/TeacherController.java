package com.example.studentmanagementsystem.controller;

import com.example.studentmanagementsystem.dto.TeacherDto;
import com.example.studentmanagementsystem.entity.Teacher;
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
@RequestMapping("/teachers")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    private final TeacherService teacherService;
    private final DepartmentService departmentService;
    private final StudentService studentService;

    public TeacherController(TeacherService teacherService,
                             DepartmentService departmentService,
                             StudentService studentService) {
        this.teacherService = teacherService;
        this.departmentService = departmentService;
        this.studentService = studentService;
    }

    @GetMapping
    public String listTeachers(Model model) {
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "teachers/list";
    }

    @GetMapping("/{id}")
    public String viewTeacher(@PathVariable Long id, Model model) {
        model.addAttribute("teacher", teacherService.getTeacherById(id));
        return "teachers/view";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("teacherDto", new TeacherDto());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "teachers/create";
    }

    @PostMapping("/create")
    public String createTeacher(@Valid @ModelAttribute("teacherDto") TeacherDto dto,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "teachers/create";
        }

        try {
            teacherService.createTeacher(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher created successfully!");
            return "redirect:/teachers";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "teachers/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Teacher teacher = teacherService.getTeacherById(id);
        TeacherDto dto = new TeacherDto();
        dto.setId(teacher.getId());
        dto.setName(teacher.getName());
        dto.setEmail(teacher.getEmail());
        if (teacher.getDepartment() != null) {
            dto.setDepartmentId(teacher.getDepartment().getId());
        }

        model.addAttribute("teacherDto", dto);
        model.addAttribute("teacherId", id);
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "teachers/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateTeacher(@PathVariable Long id,
                                @Valid @ModelAttribute("teacherDto") TeacherDto dto,
                                BindingResult result,
                                Model model,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("teacherId", id);
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "teachers/edit";
        }

        try {
            teacherService.updateTeacher(id, dto, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/teachers/" + id;
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("teacherId", id);
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "teachers/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteTeacher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            teacherService.deleteTeacher(id);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/teachers";
    }

    @GetMapping("/{id}/assign-students")
    public String showAssignStudentsForm(@PathVariable Long id, Authentication authentication, Model model) {
        Teacher teacher = teacherService.getTeacherById(id);

        // Only show students from same department
        model.addAttribute("teacher", teacher);
        model.addAttribute("allStudents", teacherService.getStudentsInSameDepartment(authentication.getName()));
        model.addAttribute("assignedStudentIds", teacher.getStudents().stream().map(s -> s.getId()).toList());
        return "teachers/assign-students";
    }

    @PostMapping("/{id}/assign-students")
    public String assignStudents(@PathVariable Long id,
                                 @RequestParam(value = "studentIds", required = false) List<Long> studentIds,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            Set<Long> ids = studentIds != null ? new HashSet<>(studentIds) : new HashSet<>();
            teacherService.assignStudents(id, ids, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Students assigned successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/teachers/" + id;
    }
}
