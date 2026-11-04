package com.example.task_Management.Task;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    @Modifying
    @Query("""
           UPDATE TaskEntity t
           SET t.status = :status
           WHERE id =:id          
           """)
    void setStatus(
            @Param("id") Long id,
            @Param("status") TaskStatus taskStatus);


    long countByAssignedUserIdAndStatus(Long assignedUserId, TaskStatus status);

    @Query("""
            SELECT t FROM TaskEntity t
            WHERE (:creatorId IS NULL OR t.creatorId = :creatorId)  
            """)
    List<TaskEntity> searchAllByFilter(
            @Param("creatorId") Long creatorId,
            Pageable pageable
    );

}
