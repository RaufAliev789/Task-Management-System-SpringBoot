package com.example.task_Management.Task;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Task(
        Long id,

        @NotNull
        Long creatorId,

        @NotNull
        Long assignedUserId,

        TaskStatus status,

        @NotNull
        @FutureOrPresent
        LocalDate createDateTime,

        @NotNull
        @Future
        LocalDate deadlineDate,

        @NotNull
        TaskPriority priority,

        LocalDateTime doneDateTime
) {
}
