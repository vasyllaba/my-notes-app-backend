package com.laba.mynotes.controller;

import com.laba.mynotes.dto.TaskRequest;
import com.laba.mynotes.model.Task;
import com.laba.mynotes.model.User;
import com.laba.mynotes.service.TaskService;
import com.laba.mynotes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    // GET всіх тасок з фільтрами
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Authentication authentication) {

        User user = userService.findByUsername(authentication.getName());
        List<Task> tasks;

        if (startDate != null && endDate != null) {
            tasks = taskService.getTasksInRange(user.getId(), startDate, endDate);
        } else if ("active".equals(filter)) {
            tasks = taskService.getActiveTasks(user.getId());
        } else if ("completed".equals(filter)) {
            tasks = taskService.getCompletedTasks(user.getId());
        } else {
            tasks = taskService.getUserTasks(user.getId());
        }

        return ResponseEntity.ok(tasks);
    }

    // POST створення нової таски
    @PostMapping
    public ResponseEntity<Task> createTask(
            @RequestBody TaskRequest request,
            Authentication authentication) {

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (request.getDueDate() == null) {
            return ResponseEntity.badRequest().build();
        }

        User user = userService.findByUsername(authentication.getName());
        Task task = taskService.createTask(
                request.getTitle(),
                request.getDescription(),
                request.getEstimationHours(),
                request.getDueDate(),
                user.getId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    // PUT оновлення таски
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @RequestBody TaskRequest request,
            Authentication authentication) {

        try {
            User user = userService.findByUsername(authentication.getName());
            Task task = taskService.updateTask(
                    id,
                    user.getId(),
                    request.getTitle(),
                    request.getDescription(),
                    request.getEstimationHours(),
                    request.getDueDate()
            );
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // PATCH toggle completed
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Task> toggleTask(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            User user = userService.findByUsername(authentication.getName());
            Task task = taskService.toggleTask(id, user.getId());
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // DELETE видалення таски
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            User user = userService.findByUsername(authentication.getName());
            taskService.deleteTask(id, user.getId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}