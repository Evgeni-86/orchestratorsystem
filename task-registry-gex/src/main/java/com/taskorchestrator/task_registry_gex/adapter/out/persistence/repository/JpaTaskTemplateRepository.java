package com.taskorchestrator.task_registry_gex.adapter.out.persistence.repository;

import com.taskorchestrator.task_registry_gex.adapter.out.persistence.entity.TaskTemplateEntity;
import com.taskorchestrator.task_registry_gex.application.core.port.out.TaskTemplateRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTaskTemplateRepository extends JpaRepository<TaskTemplateEntity, UUID>,
    JpaSpecificationExecutor<TaskTemplateEntity>, TaskTemplateRepository {

}
