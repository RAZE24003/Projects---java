package com.projecttt.projectassignment.controller;

import com.projecttt.projectassignment.model.Project;
import com.projecttt.projectassignment.model.Student;
import com.projecttt.projectassignment.repository.ProjectRepository;
import com.projecttt.projectassignment.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "${SPRING_ORIGINS:*}")
@RequestMapping("/students")
public class StudentController {

    private static int maxProjectsPerStudent=3;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/")
    public ResponseEntity<List<Student>> getAllStudent()
    {
        return ResponseEntity.ok(studentRepository.findAll());
    }

    public Student retriveStudentById(int id)
    {
        return studentRepository.findById(id).orElseThrow(
                ()-> new ExpressionException("Student with id" +id + "is not Found!")
        );
    }

    public Project retriveProjectById(int id)
    {
        return projectRepository.findById(id).orElseThrow(
                ()-> new ExpressionException("Project with id" +id + "is not Found!")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable int id)
    {
        return ResponseEntity.of(studentRepository.findById(id));
    }

    @PostMapping("/")
    public ResponseEntity<Student> createStudent(@RequestBody Student studentDetails)
    {
        Student savedStudent= studentRepository.save(studentDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable int id,@RequestBody Student student)
    {
        Student student1 = retriveStudentById(id);
        student1.setName(student.getName());
        student1.setAverage(student.getAverage());
        Student updatedStudent = studentRepository.save(student1);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable int id)
    {
        Student student = retriveStudentById(id);
        studentRepository.delete(student);
        return ResponseEntity.ok("Student deleted Successfully");
    }

    @GetMapping("/max_project")
    public ResponseEntity<Integer> getMaxProject()
    {
        return ResponseEntity.ok(maxProjectsPerStudent);
    }

    @PutMapping("/max_project")
    public ResponseEntity<Integer> updateMaxProject(@RequestBody int maximumNumber)
    {
        maxProjectsPerStudent=maximumNumber;
        return ResponseEntity.ok(maximumNumber);
    }

    @PostMapping("/{student_id}/projects/{project_id}")
    public ResponseEntity<Student> addProjectToStudent (@PathVariable int student_id ,@PathVariable int project_id)
    {
        Student student = retriveStudentById(student_id);
        Project project =retriveProjectById(project_id);
        for (Project p : student.getProjects())
        {
            if (p.getId()==project.getId())
            {
                return ResponseEntity.status(400).body(student);
            }
        }
        if (student.getProjects().size()>=maxProjectsPerStudent)
        {
            return ResponseEntity.badRequest().body(student);
        }
        student.getProjects().add(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(studentRepository.save(student));

    }


    @DeleteMapping("/{student_id}/projects/{project_id}")
    public ResponseEntity<Student> deleteProjectFromStudent (@PathVariable int student_id ,@PathVariable int project_id)
    {
        Student student = retriveStudentById(student_id);
        Project project =retriveProjectById(project_id);
        student.getProjects().remove(project);
        return ResponseEntity.ok(studentRepository.save(student));
    }

    @GetMapping("{student_id}/availableprojects")
    public ResponseEntity<List<Project>> getStudentAvailableProjects(@PathVariable int student_id)
    {
        Student student = retriveStudentById(student_id);
        List<Project> availableProjects = new ArrayList<Project>();
        if (student.getProjects().size()>=maxProjectsPerStudent)
        {
            return ResponseEntity.ok(availableProjects);
        }
        List<Project> allProjects = projectRepository.findAll();
        HashSet<Integer> projectId = new HashSet<>();
        for (Project p : student.getProjects())
        {
            projectId.add(p.getId());
        }
        for (Project project : allProjects)
        {
            if(! projectId.contains(project.getId()))
            {
                availableProjects.add(project);
            }
        }

        return ResponseEntity.ok(availableProjects);
    }

    @GetMapping("/assignment")
    public ResponseEntity<HashMap<String, String>> assignProjectToStudent() {

        HashMap<String, String> assignList = new HashMap<>();

        HashSet<Integer> projectIds = projectRepository.findAll()
                .stream()
                .map(Project::getId)
                .collect(Collectors.toCollection(HashSet::new));

        List<Student> listStudent = studentRepository.findAll();
        listStudent.sort((s1, s2) -> Double.compare(s2.getAverage(), s1.getAverage()));

        for (Student s : listStudent) {
            for (Project p : s.getProjects()) {
                if (projectIds.contains(p.getId())) {
                    assignList.put(s.getName(), p.getName());
                    projectIds.remove(p.getId());
                    break;
                }
            }
        }

        return ResponseEntity.ok(assignList);
    }


}
