package com.example.task_Management.Task;

public record TaskSearchFilter(
        Long creatorId,
        Integer pageSize,
        Integer pageNumber
) {
}
