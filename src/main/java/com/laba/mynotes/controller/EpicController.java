package com.laba.mynotes.controller;

import com.laba.mynotes.dto.EpicRequest;
import com.laba.mynotes.dto.EpicResponse;
import com.laba.mynotes.model.Epic;
import com.laba.mynotes.model.User;
import com.laba.mynotes.service.EpicService;
import com.laba.mynotes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/epics")
public class EpicController {

    @Autowired
    private EpicService epicService;

    @Autowired
    private UserService userService;

    // GET всіх епіків з фільтрами
    @GetMapping
    public ResponseEntity<List<Epic>> getAllEpics(
            @RequestParam(required = false) String filter,
            Authentication authentication) {

        User user = userService.findByUsername(authentication.getName());
        List<Epic> epics;

        if ("active".equals(filter)) {
            epics = epicService.getActiveEpics(user.getId());
        } else if ("completed".equals(filter)) {
            epics = epicService.getCompletedEpics(user.getId());
        } else {
            epics = epicService.getUserEpics(user.getId());
        }

        return ResponseEntity.ok(epics);
    }

    // GET конкретного епіка з тасками
    @GetMapping("/{id}")
    public ResponseEntity<EpicResponse> getEpic(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            User user = userService.findByUsername(authentication.getName());
            EpicResponse response = epicService.getEpicWithTasks(id, user.getId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // POST створення нового епіка
    @PostMapping
    public ResponseEntity<Epic> createEpic(
            @RequestBody EpicRequest request,
            Authentication authentication) {

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userService.findByUsername(authentication.getName());
        Epic epic = epicService.createEpic(
                request.getTitle(),
                request.getDescription(),
                user.getId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(epic);
    }

    // PUT оновлення епіка
    @PutMapping("/{id}")
    public ResponseEntity<Epic> updateEpic(
            @PathVariable Long id,
            @RequestBody EpicRequest request,
            Authentication authentication) {

        try {
            User user = userService.findByUsername(authentication.getName());
            Epic epic = epicService.updateEpic(
                    id,
                    user.getId(),
                    request.getTitle(),
                    request.getDescription()
            );
            return ResponseEntity.ok(epic);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // PATCH toggle completed
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Epic> toggleEpic(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            User user = userService.findByUsername(authentication.getName());
            Epic epic = epicService.toggleEpic(id, user.getId());
            return ResponseEntity.ok(epic);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // DELETE видалення епіка
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEpic(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            User user = userService.findByUsername(authentication.getName());
            epicService.deleteEpic(id, user.getId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // POST додати таску до епіка
    @PostMapping("/{epicId}/tasks/{taskId}")
    public ResponseEntity<?> addTaskToEpic(
            @PathVariable Long epicId,
            @PathVariable Long taskId,
            Authentication authentication) {

        try {
            User user = userService.findByUsername(authentication.getName());
            epicService.addTaskToEpic(epicId, taskId, user.getId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // DELETE видалити таску з епіка
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<?> removeTaskFromEpic(
            @PathVariable Long taskId,
            Authentication authentication) {

        try {
            User user = userService.findByUsername(authentication.getName());
            epicService.removeTaskFromEpic(taskId, user.getId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}