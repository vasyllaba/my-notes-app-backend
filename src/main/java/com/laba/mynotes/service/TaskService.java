package com.laba.mynotes.service;

import com.laba.mynotes.model.Task;
import com.laba.mynotes.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getUserTasks(Long userId) {
        return taskRepository.findByUserIdOrderByDueDateAsc(userId);
    }

    public List<Task> getTasksInRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return taskRepository.findByUserIdAndDueDateBetweenOrderByDueDateAsc(userId, startDate, endDate);
    }

    public List<Task> getActiveTasks(Long userId) {
        return taskRepository.findByUserIdAndCompletedFalseOrderByDueDateAsc(userId);
    }

    public List<Task> getCompletedTasks(Long userId) {
        return taskRepository.findByUserIdAndCompletedTrueOrderByCompletedAtDesc(userId);
    }

    public Task createTask(String title, String description, Double estimationHours,
                           LocalDateTime dueDate, Long userId) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setEstimationHours(estimationHours);
        task.setDueDate(dueDate);
        task.setUserId(userId);
        task.setCompleted(false);
        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, Long userId, String title, String description,
                           Double estimationHours, LocalDateTime dueDate) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (title != null) task.setTitle(title);
        if (description != null) task.setDescription(description);
        if (estimationHours != null) task.setEstimationHours(estimationHours);
        if (dueDate != null) task.setDueDate(dueDate);

        return taskRepository.save(task);
    }

    public Task toggleTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        task.setCompleted(!task.getCompleted());
        if (task.getCompleted()) {
            task.setCompletedAt(LocalDateTime.now());
        } else {
            task.setCompletedAt(null);
        }

        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        taskRepository.delete(task);
    }
}