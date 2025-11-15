package com.laba.mynotes.controller;

import com.laba.mynotes.model.Todo;
import com.laba.mynotes.model.User;
import com.laba.mynotes.service.TodoService;
import com.laba.mynotes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    // GET всіх TODO з фільтром
    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos(
            @RequestParam(required = false) String filter,
            Authentication authentication) {

        User user = userService.findByUsername(authentication.getName());
        List<Todo> todos;

        if ("active".equals(filter)) {
            todos = todoService.getActiveTodos(user.getId());
        } else if ("completed".equals(filter)) {
            todos = todoService.getCompletedTodos(user.getId());
        } else {
            todos = todoService.getUserTodos(user.getId());
        }

        return ResponseEntity.ok(todos);
    }

    // POST створення нового TODO
    @PostMapping
    public ResponseEntity<Todo> createTodo(
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        String title = request.get("title");
        String description = request.get("description");
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userService.findByUsername(authentication.getName());
        Todo todo = todoService.createTodo(title, description, user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(todo);
    }

    // PATCH toggle completed
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Todo> toggleTodo(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            User user = userService.findByUsername(authentication.getName());
            Todo todo = todoService.toggleTodo(id, user.getId());
            return ResponseEntity.ok(todo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // DELETE видалення TODO
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            User user = userService.findByUsername(authentication.getName());
            todoService.deleteTodo(id, user.getId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}