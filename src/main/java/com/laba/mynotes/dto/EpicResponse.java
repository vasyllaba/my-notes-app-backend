package com.laba.mynotes.dto;

import com.laba.mynotes.model.Epic;
import com.laba.mynotes.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EpicResponse {
    private Epic epic;
    private List<Task> tasks;
    private Double totalEstimationHours;
    private Integer completedTasksCount;
    private Integer totalTasksCount;
}