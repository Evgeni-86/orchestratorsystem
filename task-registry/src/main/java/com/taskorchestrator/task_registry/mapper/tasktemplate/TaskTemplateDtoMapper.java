package com.taskorchestrator.task_registry.mapper.tasktemplate;

import com.taskorchestrator.task_registry.config.CentralMapperConfig;
import com.taskorchestrator.task_registry.domain.TaskTemplate;
import com.taskorchestrator.task_registry.dto.task.TaskTemplateCreateDto;
import com.taskorchestrator.task_registry.dto.task.TaskTemplateResponseDto;
import com.taskorchestrator.task_registry.dto.task.TaskTemplateUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface TaskTemplateDtoMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  TaskTemplate createDtoToDomain(TaskTemplateCreateDto dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateDomainFromDto(@MappingTarget TaskTemplate domain, TaskTemplateUpdateDto dto);

  TaskTemplateResponseDto domainToResponseDto(TaskTemplate domain);
}
