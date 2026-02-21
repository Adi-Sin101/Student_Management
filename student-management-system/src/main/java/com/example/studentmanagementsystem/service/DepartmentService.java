package com.example.studentmanagementsystem.service;

import com.example.studentmanagementsystem.dto.DepartmentDto;
import com.example.studentmanagementsystem.entity.Department;
import com.example.studentmanagementsystem.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }

    public Department getDepartmentByName(String name) {
        return departmentRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Department not found with name: " + name));
    }

    public Department createDepartment(DepartmentDto dto) {
        if (departmentRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Department with this name already exists");
        }

        Department department = new Department();
        department.setName(dto.getName());
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Long id, DepartmentDto dto) {
        Department department = getDepartmentById(id);

        // Check if name is being changed and if new name already exists
        if (!department.getName().equals(dto.getName())) {
            if (departmentRepository.existsByName(dto.getName())) {
                throw new RuntimeException("Department with this name already exists");
            }
            department.setName(dto.getName());
        }

        return departmentRepository.save(department);
    }

    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new RuntimeException("Department not found with id: " + id);
        }
        departmentRepository.deleteById(id);
    }
}
