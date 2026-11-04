package com.example.task_Management.Task;

import org.springframework.stereotype.Service;

@Service
public class TaskMapper {
    public Task toDomain (
            TaskEntity task
    ) {
        return new Task(
                task.getId(),
                task.getCreatorId(),
                task.getAssignedUserId(),
                task.getStatus(),
                task.getCreateDateTime(),
                task.getDeadlineDate(),
                task.getPriority(),
                task.getDoneDateTime()
        );
    }

    public TaskEntity toEntity (
            Task task
    ) {
        return new TaskEntity(
                task.id(),
                task.creatorId(),
                task.assignedUserId(),
                task.status(),
                task.createDateTime(),
                task.deadlineDate(),
                task.priority()
        );
    }
}
