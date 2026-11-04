package com.example.task_Management.Task;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository repository;
    private final TaskMapper mapper;

    public TaskService(TaskRepository repository, TaskMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Task getTaskById(Long id) {
        TaskEntity  taskEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException
                        ("Not found task by id: " + id));

        return mapper.toDomain(taskEntity);
    }

    public List<Task> searchAllByFilter(
            TaskSearchFilter filter
    ) {
        int pageSize = filter.pageSize() != null
                ? filter.pageSize() : 10;

        int pageNumber = filter.pageNumber() != null
                ? filter.pageNumber() : 0;

        var pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        List<TaskEntity> allEntities = repository.searchAllByFilter(
                filter.creatorId(),
                pageable
        );

        return allEntities.stream()
                .map(mapper::toDomain).toList();
    }


    public Task createTask(Task taskToCreate) {

        if (taskToCreate.status() != null) {
            throw new IllegalStateException("Status should be empty");
        }

        if (!taskToCreate.createDateTime().isBefore(taskToCreate.deadlineDate())) {
            throw new IllegalStateException("Create date must be 1 day earlier");
        }

        var taskToSave = new TaskEntity(
                null,
                taskToCreate.creatorId(),
                taskToCreate.assignedUserId(),
                TaskStatus.CREATED,
                taskToCreate.createDateTime(),
                taskToCreate.deadlineDate(),
                taskToCreate.priority()
        );

        var savedTask = repository.save(taskToSave);

        return mapper.toDomain(savedTask);
    }

    public Task updateTask(Long id, Task taskToUpdate) {

        var taskEntity = repository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(
                        "Not found task by id =" +  id
                ));

        if (taskToUpdate.status() == TaskStatus.DONE) {
            throw new IllegalStateException("Cannot modify task: status =  "+ taskToUpdate.status());
        }

        if (!taskToUpdate.createDateTime().isBefore(taskToUpdate.deadlineDate())) {
            throw new IllegalStateException("Create date must be 1 day earlier");
        }

        var updatedTask = new TaskEntity(
                taskEntity.getId(),
                taskToUpdate.creatorId(),
                taskToUpdate.assignedUserId(),
                taskToUpdate.status(),
                taskToUpdate.createDateTime(),
                taskToUpdate.deadlineDate(),
                taskToUpdate.priority()
        );

        var savedUpdatedTask = repository.save(updatedTask);

        return mapper.toDomain(savedUpdatedTask);
    }

    @Transactional
    public Task makeInProgress(Long id, Task taskToUpdate) {

        var taskEntity = repository.findById(id)
                .orElseThrow(()->new EntityNotFoundException(
                        "Not found task by id =" +  id
                ));

        if ((taskEntity.getAssignedUserId() == null) ||  (taskEntity.getCreatorId() == null))
            throw new IllegalArgumentException("Cannot start task because of empty assignedUserId/creatorID. assignedUserId= " + taskEntity.getAssignedUserId());

        long activeTasksCount = repository.countByAssignedUserIdAndStatus(taskEntity.getAssignedUserId(), TaskStatus.IN_PROGRESS);

        if (activeTasksCount >= 5) {
            throw new IllegalStateException("Cannot make status IN_PROGRESS because count of assignedUserId is more than 5. ActiveTasksCount =  " + activeTasksCount);
        }

        var taskMadeInProgressUpdated = new TaskEntity(
                taskEntity.getId(),
                taskToUpdate.creatorId(),
                taskToUpdate.assignedUserId(),
                TaskStatus.IN_PROGRESS,
                taskToUpdate.createDateTime(),
                taskToUpdate.deadlineDate(),
                taskToUpdate.priority()
        );

        repository.save(taskMadeInProgressUpdated);

        return mapper.toDomain(taskMadeInProgressUpdated);
    }

    public void deleteTask(Long id) {

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Not found task by id "+  id);
        }

       repository.deleteById(id);
    }

    public Task completeTask(Long id) {
        var taskEntity = repository.findById(id)
                .orElseThrow(()->new EntityNotFoundException(
                        "Not found task by id =" +  id
                ));

        if ((taskEntity.getAssignedUserId() == null) || (taskEntity.getDeadlineDate() == null)) {
            throw new IllegalArgumentException("Important fields are not written");
        }

        taskEntity.setStatus(TaskStatus.DONE);
        taskEntity.setDoneDateTime(LocalDateTime.now());
        repository.save(taskEntity);

        return mapper.toDomain(taskEntity);

    }




}
