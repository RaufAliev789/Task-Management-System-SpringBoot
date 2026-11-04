package com.example.task_Management.Task;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/tasks")
@RestController
public class TaskController {

    private final TaskService taskService;

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);


    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(
            @PathVariable Long id
    ) {
        log.info("Called getTaskById(), id = " + id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.getTaskById(id));
    }

    @GetMapping()
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam(name = "creatorId", required = false) Long creatorId,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber
    ) {
        log.info("Called getAllTasks");

        var filter = new TaskSearchFilter(
                creatorId,
                pageSize,
                pageNumber
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.searchAllByFilter(filter));
    }

    @PostMapping()
    public ResponseEntity<Task> createTask(
            @RequestBody @Valid Task taskToCreate
    ) {
        log.info("Called createTask");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(taskToCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable("id") Long id,
            @RequestBody  @Valid Task taskToUpdate
    ) {
        log.info("Called updateTask. id={}, taskToUpdate={}", id, taskToUpdate);

        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.updateTask(id, taskToUpdate));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Task> makeInProgress(
            @PathVariable Long id,
            @RequestBody @Valid Task taskToUpdate
    ) {
        log.info("Called makeInProgress. id={}, taskToUpdate={}", id, taskToUpdate);

        var task = taskService.makeInProgress(id, taskToUpdate);
        return ResponseEntity.status(HttpStatus.OK)
                .body(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id
    ) {
        log.info("Called deleteTask. id={}", id);

        taskService.deleteTask(id);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(
            @PathVariable Long id
    ) {
        log.info("Called completeTask. id={}", id);

        var task = taskService.completeTask(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(task);
    }
}
