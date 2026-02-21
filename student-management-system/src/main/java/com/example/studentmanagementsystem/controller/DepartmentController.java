package com.example.studentmanagementsystem.controller;

import com.example.studentmanagementsystem.dto.DepartmentDto;
import com.example.studentmanagementsystem.entity.Department;
import com.example.studentmanagementsystem.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/departments")
@PreAuthorize("hasRole('TEACHER')")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public String listDepartments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "departments/list";
    }

    @GetMapping("/{id}")
    public String viewDepartment(@PathVariable Long id, Model model) {
        model.addAttribute("department", departmentService.getDepartmentById(id));
        return "departments/view";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("departmentDto", new DepartmentDto());
        return "departments/create";
    }

    @PostMapping("/create")
    public String createDepartment(@Valid @ModelAttribute("departmentDto") DepartmentDto dto,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "departments/create";
        }

        try {
            departmentService.createDepartment(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Department created successfully!");
            return "redirect:/departments";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "departments/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Department department = departmentService.getDepartmentById(id);
        DepartmentDto dto = new DepartmentDto();
        dto.setId(department.getId());
        dto.setName(department.getName());

        model.addAttribute("departmentDto", dto);
        model.addAttribute("departmentId", id);
        return "departments/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateDepartment(@PathVariable Long id,
                                   @Valid @ModelAttribute("departmentDto") DepartmentDto dto,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("departmentId", id);
            return "departments/edit";
        }

        try {
            departmentService.updateDepartment(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Department updated successfully!");
            return "redirect:/departments/" + id;
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("departmentId", id);
            return "departments/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Department deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/departments";
    }
}
