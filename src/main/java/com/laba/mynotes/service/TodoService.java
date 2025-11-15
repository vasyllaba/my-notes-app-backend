package com.laba.mynotes.service;

import com.laba.mynotes.model.Todo;
import com.laba.mynotes.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    public List<Todo> getUserTodos(Long userId) {
        return todoRepository.findByUserIdOrderByCompletedAscCreatedAtDesc(userId);
    }

    public List<Todo> getActiveTodos(Long userId) {
        return todoRepository.findByUserIdAndCompletedFalseOrderByCreatedAtDesc(userId);
    }

    public List<Todo> getCompletedTodos(Long userId) {
        return todoRepository.findByUserIdAndCompletedTrueOrderByCompletedAtDesc(userId);
    }

    public Todo createTodo(String title, Long userId) {
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setUserId(userId);
        todo.setCompleted(false);
        return todoRepository.save(todo);
    }

    public Todo toggleTodo(Long todoId, Long userId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (!todo.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        todo.setCompleted(!todo.getCompleted());

        if (todo.getCompleted()) {
            todo.setCompletedAt(LocalDateTime.now());
        } else {
            todo.setCompletedAt(null);
        }

        return todoRepository.save(todo);
    }

    public void deleteTodo(Long todoId, Long userId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (!todo.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        todoRepository.delete(todo);
    }
}