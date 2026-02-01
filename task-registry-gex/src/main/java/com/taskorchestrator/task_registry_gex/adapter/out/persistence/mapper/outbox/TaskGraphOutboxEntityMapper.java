package com.taskorchestrator.task_registry_gex.adapter.out.persistence.mapper.outbox;

import com.taskorchestrator.task_registry_gex.adapter.out.persistence.entity.TaskGraphOutboxEntity;
import com.taskorchestrator.task_registry_gex.application.core.domain.TaskGraphOutboxMessage;
import com.taskorchestrator.task_registry_gex.infrastructure.config.CentralMapperConfig;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface TaskGraphOutboxEntityMapper {

  TaskGraphOutboxMessage toTaskGraphOutboxMessage(
      TaskGraphOutboxEntity taskGraphOutboxEntity);

  TaskGraphOutboxEntity toTaskGraphOutboxEntity(
      TaskGraphOutboxMessage taskGraphOutboxMessage);

  List<TaskGraphOutboxMessage> toTaskGraphOutboxMessagesList(
      List<TaskGraphOutboxEntity> taskGraphOutboxEntity);
}
