package com.taskorchestrator.task_registry.mapper.outbox;

import com.taskorchestrator.task_registry.config.CentralMapperConfig;
import com.taskorchestrator.task_registry.domain.TaskGraphOutboxMessage;
import com.taskorchestrator.task_registry.entity.TaskGraphOutboxEntity;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface TaskGraphOutboxEntityMapper {

  TaskGraphOutboxMessage toTaskGraphOutboxMessage(
      TaskGraphOutboxEntity taskGraphOutboxEntity);

  TaskGraphOutboxEntity toTaskGraphOutboxEntity(
      TaskGraphOutboxMessage taskGraphOutboxMessage);
}
