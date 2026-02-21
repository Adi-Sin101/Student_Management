package com.example.studentmanagementsystem.repository;

import com.example.studentmanagementsystem.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByDepartmentId(Long departmentId);

    List<Course> findByTeacherId(Long teacherId);
}
