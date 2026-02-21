package com.example.studentmanagementsystem.config;

import com.example.studentmanagementsystem.entity.Course;
import com.example.studentmanagementsystem.entity.Department;
import com.example.studentmanagementsystem.entity.Student;
import com.example.studentmanagementsystem.entity.Teacher;
import com.example.studentmanagementsystem.repository.CourseRepository;
import com.example.studentmanagementsystem.repository.DepartmentRepository;
import com.example.studentmanagementsystem.repository.StudentRepository;
import com.example.studentmanagementsystem.repository.TeacherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(DepartmentRepository departmentRepository,
                           TeacherRepository teacherRepository,
                           StudentRepository studentRepository,
                           CourseRepository courseRepository,
                           PasswordEncoder passwordEncoder) {
        this.departmentRepository = departmentRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Initialize if no teachers exist (fresh start or after clearing data)
        if (teacherRepository.count() == 0) {
            System.out.println("Initializing database with seed data...");

            String encodedPassword = passwordEncoder.encode("password123");

            // Create Departments
            Department csDepartment = departmentRepository.findByName("Computer Science")
                    .orElseGet(() -> departmentRepository.save(new Department("Computer Science")));

            Department eeDepartment = departmentRepository.findByName("Electrical Engineering")
                    .orElseGet(() -> departmentRepository.save(new Department("Electrical Engineering")));

            Department meDepartment = departmentRepository.findByName("Mechanical Engineering")
                    .orElseGet(() -> departmentRepository.save(new Department("Mechanical Engineering")));

            // Create 3 Teachers
            Teacher teacher1 = new Teacher();
            teacher1.setName("Dr. John Smith");
            teacher1.setEmail("john.smith@university.edu");
            teacher1.setPassword(encodedPassword);
            teacher1.setDepartment(csDepartment);
            teacher1 = teacherRepository.save(teacher1);

            Teacher teacher2 = new Teacher();
            teacher2.setName("Dr. Sarah Johnson");
            teacher2.setEmail("sarah.johnson@university.edu");
            teacher2.setPassword(encodedPassword);
            teacher2.setDepartment(eeDepartment);
            teacher2 = teacherRepository.save(teacher2);

            Teacher teacher3 = new Teacher();
            teacher3.setName("Dr. Michael Brown");
            teacher3.setEmail("michael.brown@university.edu");
            teacher3.setPassword(encodedPassword);
            teacher3.setDepartment(meDepartment);
            teacher3 = teacherRepository.save(teacher3);

            // Create 5 Students
            Student student1 = new Student();
            student1.setName("Alice Williams");
            student1.setRoll("CS2021001");
            student1.setEmail("alice.williams@student.edu");
            student1.setPassword(encodedPassword);
            student1.setDepartment(csDepartment);
            student1 = studentRepository.save(student1);

            Student student2 = new Student();
            student2.setName("Bob Anderson");
            student2.setRoll("CS2021002");
            student2.setEmail("bob.anderson@student.edu");
            student2.setPassword(encodedPassword);
            student2.setDepartment(csDepartment);
            student2 = studentRepository.save(student2);

            Student student3 = new Student();
            student3.setName("Carol Martinez");
            student3.setRoll("EE2021001");
            student3.setEmail("carol.martinez@student.edu");
            student3.setPassword(encodedPassword);
            student3.setDepartment(eeDepartment);
            student3 = studentRepository.save(student3);

            Student student4 = new Student();
            student4.setName("David Thompson");
            student4.setRoll("EE2021002");
            student4.setEmail("david.thompson@student.edu");
            student4.setPassword(encodedPassword);
            student4.setDepartment(eeDepartment);
            student4 = studentRepository.save(student4);

            Student student5 = new Student();
            student5.setName("Emma Garcia");
            student5.setRoll("ME2021001");
            student5.setEmail("emma.garcia@student.edu");
            student5.setPassword(encodedPassword);
            student5.setDepartment(meDepartment);
            student5 = studentRepository.save(student5);

            // Create Courses
            Course course1 = new Course();
            course1.setName("Data Structures");
            course1.setDescription("Introduction to data structures and algorithms");
            course1.setDepartment(csDepartment);
            course1.setTeacher(teacher1);
            courseRepository.save(course1);

            Course course2 = new Course();
            course2.setName("Database Systems");
            course2.setDescription("Fundamentals of database design and SQL");
            course2.setDepartment(csDepartment);
            course2.setTeacher(teacher1);
            courseRepository.save(course2);

            Course course3 = new Course();
            course3.setName("Circuit Analysis");
            course3.setDescription("Basic electrical circuit theory");
            course3.setDepartment(eeDepartment);
            course3.setTeacher(teacher2);
            courseRepository.save(course3);

            Course course4 = new Course();
            course4.setName("Thermodynamics");
            course4.setDescription("Principles of heat and energy transfer");
            course4.setDepartment(meDepartment);
            course4.setTeacher(teacher3);
            courseRepository.save(course4);

            System.out.println("=================================");
            System.out.println("Database initialized with:");
            System.out.println("- 3 Departments");
            System.out.println("- 3 Teachers");
            System.out.println("- 5 Students");
            System.out.println("- 4 Courses");
            System.out.println("");
            System.out.println("Login Credentials (Password: password123):");
            System.out.println("");
            System.out.println("TEACHERS:");
            System.out.println("  - john.smith@university.edu");
            System.out.println("  - sarah.johnson@university.edu");
            System.out.println("  - michael.brown@university.edu");
            System.out.println("");
            System.out.println("STUDENTS:");
            System.out.println("  - alice.williams@student.edu");
            System.out.println("  - bob.anderson@student.edu");
            System.out.println("  - carol.martinez@student.edu");
            System.out.println("  - david.thompson@student.edu");
            System.out.println("  - emma.garcia@student.edu");
            System.out.println("=================================");
        } else {
            System.out.println("Database already has data. Skipping initialization.");
            System.out.println("To re-initialize, clear the teachers table first.");
        }
    }
}
