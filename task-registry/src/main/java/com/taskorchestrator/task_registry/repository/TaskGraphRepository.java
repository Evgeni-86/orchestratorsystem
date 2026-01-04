package com.taskorchestrator.task_registry.repository;

import com.taskorchestrator.task_registry.entity.TaskGraphEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskGraphRepository extends JpaRepository<TaskGraphEntity, UUID> {

}
