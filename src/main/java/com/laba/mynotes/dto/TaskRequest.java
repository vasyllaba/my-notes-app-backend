package com.laba.mynotes.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private Double estimationHours;
    private LocalDateTime dueDate;
}