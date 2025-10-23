package com.laba.mynotes.repository;

import com.laba.mynotes.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    // Spring автоматично надає методи: save(), findAll(), findById(), deleteById()

    // Кастомний метод для сортування по даті (нові зверху)
    List<Note> findAllByOrderByCreatedAtDesc();
}