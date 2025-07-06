package org.example.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Student;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Student entity.
 * This controller provides endpoints to create, read, update, and delete students.
 * These operations will trigger CDC events that will be captured by Debezium.
 */
@Slf4j
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Get all students.
     *
     * @return List of all students
     */
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = entityManager.createQuery("SELECT s FROM Student s", Student.class)
                .getResultList();
        return ResponseEntity.ok(students);
    }

    /**
     * Get a student by ID.
     *
     * @param id Student ID
     * @return Student with the given ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = entityManager.find(Student.class, id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    /**
     * Create a new student.
     * This will trigger a CDC create event.
     *
     * @param student Student to create
     * @return Created student
     */
    @PostMapping
    @Transactional
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        log.info("Creating student: {}", student);
        entityManager.persist(student);
        entityManager.flush();
        return ResponseEntity.ok(student);
    }

    /**
     * Update an existing student.
     * This will trigger a CDC update event.
     *
     * @param id Student ID
     * @param student Student data to update
     * @return Updated student
     */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        log.info("Updating student with ID {}: {}", id, student);
        Student existingStudent = entityManager.find(Student.class, id);
        if (existingStudent == null) {
            return ResponseEntity.notFound().build();
        }
        
        existingStudent.setFirstName(student.getFirstName());
        existingStudent.setLastName(student.getLastName());
        existingStudent.setEmail(student.getEmail());
        existingStudent.setAge(student.getAge());
        existingStudent.setGrade(student.getGrade());
        
        entityManager.merge(existingStudent);
        entityManager.flush();
        return ResponseEntity.ok(existingStudent);
    }

    /**
     * Delete a student.
     * This will trigger a CDC delete event.
     *
     * @param id Student ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        log.info("Deleting student with ID: {}", id);
        Student student = entityManager.find(Student.class, id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        
        entityManager.remove(student);
        entityManager.flush();
        return ResponseEntity.noContent().build();
    }
}