package com.laba.mynotes.controller;

import com.laba.mynotes.model.Note;
import com.laba.mynotes.model.User;
import com.laba.mynotes.repository.NoteRepository;
import com.laba.mynotes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Note>> getUserNotes(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        List<Note> notes = noteRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return ResponseEntity.ok(notes);
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note, Authentication authentication) {
        if (note.getContent() == null || note.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userService.findByUsername(authentication.getName());
        note.setUserId(user.getId());

        Note savedNote = noteRepository.save(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
    }
}
