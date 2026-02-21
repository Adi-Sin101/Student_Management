package com.example.studentmanagementsystem.service;

import com.example.studentmanagementsystem.dto.DepartmentDto;
import com.example.studentmanagementsystem.entity.Department;
import com.example.studentmanagementsystem.repository.DepartmentRepository;
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
@DisplayName("DepartmentService Unit Tests")
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private Department testDepartment;

    @BeforeEach
    void setUp() {
        testDepartment = new Department("Computer Science");
        testDepartment.setId(1L);
    }

    @Test
    @DisplayName("getAllDepartments should return all departments")
    void getAllDepartments_shouldReturnAll() {
        Department dept2 = new Department("Mathematics");
        dept2.setId(2L);
        when(departmentRepository.findAll()).thenReturn(Arrays.asList(testDepartment, dept2));

        List<Department> result = departmentService.getAllDepartments();

        assertThat(result).hasSize(2);
        verify(departmentRepository).findAll();
    }

    @Test
    @DisplayName("getDepartmentById should return department when found")
    void getDepartmentById_whenFound_shouldReturn() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));

        Department result = departmentService.getDepartmentById(1L);

        assertThat(result.getName()).isEqualTo("Computer Science");
    }

    @Test
    @DisplayName("getDepartmentById should throw when not found")
    void getDepartmentById_whenNotFound_shouldThrow() {
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getDepartmentById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Department not found");
    }

    @Test
    @DisplayName("getDepartmentByName should return department when found")
    void getDepartmentByName_whenFound_shouldReturn() {
        when(departmentRepository.findByName("Computer Science")).thenReturn(Optional.of(testDepartment));

        Department result = departmentService.getDepartmentByName("Computer Science");

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getDepartmentByName should throw when not found")
    void getDepartmentByName_whenNotFound_shouldThrow() {
        when(departmentRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getDepartmentByName("Unknown"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Department not found");
    }

    @Test
    @DisplayName("createDepartment should create successfully")
    void createDepartment_shouldCreate() {
        DepartmentDto dto = new DepartmentDto();
        dto.setName("Physics");
        when(departmentRepository.existsByName("Physics")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenAnswer(inv -> {
            Department d = inv.getArgument(0);
            d.setId(2L);
            return d;
        });

        Department result = departmentService.createDepartment(dto);

        assertThat(result.getName()).isEqualTo("Physics");
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    @DisplayName("createDepartment should throw when name exists")
    void createDepartment_whenNameExists_shouldThrow() {
        DepartmentDto dto = new DepartmentDto();
        dto.setName("Computer Science");
        when(departmentRepository.existsByName("Computer Science")).thenReturn(true);

        assertThatThrownBy(() -> departmentService.createDepartment(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("updateDepartment should update successfully")
    void updateDepartment_shouldUpdate() {
        DepartmentDto dto = new DepartmentDto();
        dto.setName("CS Department");
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(departmentRepository.existsByName("CS Department")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenAnswer(inv -> inv.getArgument(0));

        Department result = departmentService.updateDepartment(1L, dto);

        assertThat(result.getName()).isEqualTo("CS Department");
    }

    @Test
    @DisplayName("updateDepartment should throw when new name already exists")
    void updateDepartment_whenNameExists_shouldThrow() {
        DepartmentDto dto = new DepartmentDto();
        dto.setName("Mathematics");
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(departmentRepository.existsByName("Mathematics")).thenReturn(true);

        assertThatThrownBy(() -> departmentService.updateDepartment(1L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("deleteDepartment should delete when exists")
    void deleteDepartment_shouldDelete() {
        when(departmentRepository.existsById(1L)).thenReturn(true);

        departmentService.deleteDepartment(1L);

        verify(departmentRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteDepartment should throw when not found")
    void deleteDepartment_whenNotFound_shouldThrow() {
        when(departmentRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> departmentService.deleteDepartment(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Department not found");
    }
}
