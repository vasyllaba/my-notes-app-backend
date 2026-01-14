package com.laba.mynotes.repository;

import com.laba.mynotes.model.Epic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpicRepository extends JpaRepository<Epic, Long> {

    // Всі епіки користувача
    List<Epic> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Тільки незавершені епіки
    List<Epic> findByUserIdAndCompletedFalseOrderByCreatedAtDesc(Long userId);

    // Тільки завершені епіки
    List<Epic> findByUserIdAndCompletedTrueOrderByCompletedAtDesc(Long userId);
}