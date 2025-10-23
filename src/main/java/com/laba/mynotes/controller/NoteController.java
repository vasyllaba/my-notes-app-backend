package com.laba.mynotes.controller;

import com.laba.mynotes.model.Note;
import com.laba.mynotes.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    // GET всіх нотаток (нові зверху)
    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes() {
        List<Note> notes = noteRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(notes);
    }

    // POST створення нової нотатки
    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        // Перевірка що content не пустий
        if (note.getContent() == null || note.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Note savedNote = noteRepository.save(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
    }
}