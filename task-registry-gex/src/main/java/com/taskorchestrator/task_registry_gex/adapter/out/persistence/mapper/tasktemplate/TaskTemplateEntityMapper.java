package com.taskorchestrator.task_registry_gex.adapter.out.persistence.mapper.tasktemplate;

import com.taskorchestrator.task_registry_gex.adapter.out.persistence.entity.TaskTemplateEntity;
import com.taskorchestrator.task_registry_gex.application.core.domain.TaskTemplate;
import com.taskorchestrator.task_registry_gex.infrastructure.config.CentralMapperConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = CentralMapperConfig.class)
public interface TaskTemplateEntityMapper {

  TaskTemplate entityToDomain(TaskTemplateEntity entity);

  @Mapping(target = "graphs", ignore = true)
  TaskTemplateEntity domainToEntity(TaskTemplate domain);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "graphs", ignore = true)
  void updateEntityFromDomain(@MappingTarget TaskTemplateEntity entity, TaskTemplate domain);
}
