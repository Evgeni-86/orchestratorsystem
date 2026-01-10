package com.taskorchestrator.task_registry.repository;

import com.taskorchestrator.task_registry.entity.TaskTemplateEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskTemplateRepository extends JpaRepository<TaskTemplateEntity, UUID>,
    JpaSpecificationExecutor<TaskTemplateEntity> {

}
