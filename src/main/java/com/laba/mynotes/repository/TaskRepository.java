package com.laba.mynotes.repository;

import com.laba.mynotes.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Всі таски користувача, відсортовані по даті виконання
    List<Task> findByUserIdOrderByDueDateAsc(Long userId);

    // Таски в певному діапазоні дат
    List<Task> findByUserIdAndDueDateBetweenOrderByDueDateAsc(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // Тільки незавершені таски
    List<Task> findByUserIdAndCompletedFalseOrderByDueDateAsc(Long userId);

    // Тільки завершені таски
    List<Task> findByUserIdAndCompletedTrueOrderByCompletedAtDesc(Long userId);

    // Таски певного епіка
    List<Task> findByEpicIdOrderByDueDateAsc(Long epicId);

    // Таски без епіка
    List<Task> findByUserIdAndEpicIdIsNullOrderByDueDateAsc(Long userId);
}