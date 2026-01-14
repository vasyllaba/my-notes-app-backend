package com.laba.mynotes.service;

import com.laba.mynotes.dto.EpicResponse;
import com.laba.mynotes.model.Epic;
import com.laba.mynotes.model.Task;
import com.laba.mynotes.repository.EpicRepository;
import com.laba.mynotes.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EpicService {

    @Autowired
    private EpicRepository epicRepository;

    @Autowired
    private TaskRepository taskRepository;

    public List<Epic> getUserEpics(Long userId) {
        return epicRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Epic> getActiveEpics(Long userId) {
        return epicRepository.findByUserIdAndCompletedFalseOrderByCreatedAtDesc(userId);
    }

    public List<Epic> getCompletedEpics(Long userId) {
        return epicRepository.findByUserIdAndCompletedTrueOrderByCompletedAtDesc(userId);
    }

    public EpicResponse getEpicWithTasks(Long epicId, Long userId) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new RuntimeException("Epic not found"));

        if (!epic.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        List<Task> tasks = taskRepository.findByEpicIdOrderByDueDateAsc(epicId);

        Double totalEstimation = tasks.stream()
                .filter(task -> task.getEstimationHours() != null)
                .mapToDouble(Task::getEstimationHours)
                .sum();

        long completedCount = tasks.stream()
                .filter(Task::getCompleted)
                .count();

        return new EpicResponse(
                epic,
                tasks,
                totalEstimation,
                (int) completedCount,
                tasks.size()
        );
    }

    public Epic createEpic(String title, String description, Long userId) {
        Epic epic = new Epic();
        epic.setTitle(title);
        epic.setDescription(description);
        epic.setUserId(userId);
        epic.setCompleted(false);
        return epicRepository.save(epic);
    }

    public Epic updateEpic(Long epicId, Long userId, String title, String description) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new RuntimeException("Epic not found"));

        if (!epic.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (title != null) epic.setTitle(title);
        if (description != null) epic.setDescription(description);

        return epicRepository.save(epic);
    }

    public Epic toggleEpic(Long epicId, Long userId) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new RuntimeException("Epic not found"));

        if (!epic.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        epic.setCompleted(!epic.getCompleted());
        if (epic.getCompleted()) {
            epic.setCompletedAt(LocalDateTime.now());
        } else {
            epic.setCompletedAt(null);
        }

        return epicRepository.save(epic);
    }

    public void deleteEpic(Long epicId, Long userId) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new RuntimeException("Epic not found"));

        if (!epic.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        // Видаляємо зв'язок з тасками (таски залишаються, але без epicId)
        List<Task> tasks = taskRepository.findByEpicIdOrderByDueDateAsc(epicId);
        tasks.forEach(task -> {
            task.setEpicId(null);
            taskRepository.save(task);
        });

        epicRepository.delete(epic);
    }

    public void addTaskToEpic(Long epicId, Long taskId, Long userId) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new RuntimeException("Epic not found"));

        if (!epic.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        task.setEpicId(epicId);
        taskRepository.save(task);
    }

    public void removeTaskFromEpic(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        task.setEpicId(null);
        taskRepository.save(task);
    }
}