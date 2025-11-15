package com.laba.mynotes.repository;

import com.laba.mynotes.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    // Всі to-do користувача (незавершені зверху)
    List<Todo> findByUserIdOrderByCompletedAscCreatedAtDesc(Long userId);

    // Тільки незавершені
    List<Todo> findByUserIdAndCompletedFalseOrderByCreatedAtDesc(Long userId);

    // Тільки завершені
    List<Todo> findByUserIdAndCompletedTrueOrderByCompletedAtDesc(Long userId);
}