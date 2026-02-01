package com.taskorchestrator.task_registry_gex.adapter.out.persistence.repository;

import com.taskorchestrator.task_registry_gex.adapter.out.persistence.entity.TaskDependencyEntity;
import com.taskorchestrator.task_registry_gex.application.core.port.out.TaskDependencyRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTaskDependencyRepository extends JpaRepository<TaskDependencyEntity, UUID>,
    TaskDependencyRepository {

}
