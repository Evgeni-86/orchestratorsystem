package com.taskorchestrator.task_registry_gex.adapter.in.web.mapper.tasktemplate;

import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.task.TaskTemplateResponseDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.task.TaskTemplateUpdateDto;
import com.taskorchestrator.task_registry_gex.adapter.out.persistence.entity.TaskTemplateEntity;
import com.taskorchestrator.task_registry_gex.infrastructure.config.CentralMapperConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = CentralMapperConfig.class)
public interface TaskTemplateDirectMapper {

  @Mapping(target = "createdAt", source = "createdAt")
  @Mapping(target = "updatedAt", source = "updatedAt")
  TaskTemplateResponseDto entityToResponseDto(TaskTemplateEntity entity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "graphs", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntityFromDto(@MappingTarget TaskTemplateEntity entity, TaskTemplateUpdateDto dto);
}
