package com.example.studentmanagementsystem.controller;

import com.example.studentmanagementsystem.entity.Student;
import com.example.studentmanagementsystem.entity.Teacher;
import com.example.studentmanagementsystem.repository.StudentRepository;
import com.example.studentmanagementsystem.repository.TeacherRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public HomeController(StudentRepository studentRepository, TeacherRepository teacherRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        boolean isTeacher = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER"));

        if (isTeacher) {
            Teacher teacher = teacherRepository.findByEmail(email).orElse(null);
            model.addAttribute("user", teacher);
            model.addAttribute("userType", "TEACHER");
        } else {
            Student student = studentRepository.findByEmail(email).orElse(null);
            model.addAttribute("user", student);
            model.addAttribute("userType", "STUDENT");
        }

        model.addAttribute("isTeacher", isTeacher);
        return "dashboard";
    }
}
