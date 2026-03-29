package com.taskorchestrator.orchestrator_engine_service_gex.application.service.mappers;

import com.taskorchestrator.orchestrator_engine_service_gex.adapter.in.rabbit.dto.TaskGraphEventPayloadDto;
import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.entity.InputTaskGraphRabbitMessageEntity;
import com.taskorchestrator.orchestrator_engine_service_gex.infrastructure.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface InputBoxRabbitMessageDirectMapper {

  @Mapping(target = "id", ignore = true)
  InputTaskGraphRabbitMessageEntity dtoToEntity(TaskGraphEventPayloadDto eventPayloadDto);
}
