package com.taskorchestrator.task_registry.mapper.tasktemplate;

import com.taskorchestrator.task_registry.config.CentralMapperConfig;
import com.taskorchestrator.task_registry.dto.task.TaskTemplateResponseDto;
import com.taskorchestrator.task_registry.dto.task.TaskTemplateUpdateDto;
import com.taskorchestrator.task_registry.entity.TaskTemplateEntity;
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
  void updateEntityFromDto(@MappingTarget TaskTemplateEntity entity, TaskTemplateUpdateDto dto);
}
