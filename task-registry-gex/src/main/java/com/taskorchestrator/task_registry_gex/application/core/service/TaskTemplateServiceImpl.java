package com.taskorchestrator.task_registry_gex.application.core.service;

import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.task.TaskTemplateCreateDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.task.TaskTemplateFilterDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.task.TaskTemplateResponseDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.task.TaskTemplateUpdateDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.mapper.tasktemplate.TaskTemplateDirectMapper;
import com.taskorchestrator.task_registry_gex.adapter.in.web.mapper.tasktemplate.TaskTemplateDtoMapper;
import com.taskorchestrator.task_registry_gex.adapter.out.persistence.entity.TaskTemplateEntity;
import com.taskorchestrator.task_registry_gex.adapter.out.persistence.mapper.tasktemplate.TaskTemplateEntityMapper;
import com.taskorchestrator.task_registry_gex.adapter.out.persistence.repository.specification.TaskTemplateSpecificationBuilder;
import com.taskorchestrator.task_registry_gex.application.core.domain.TaskTemplate;
import com.taskorchestrator.task_registry_gex.application.core.port.in.TaskTemplateService;
import com.taskorchestrator.task_registry_gex.application.core.port.out.TaskTemplateRepository;
import com.taskorchestrator.task_registry_gex.infrastructure.exception.DataBindingViolationException;
import com.taskorchestrator.task_registry_gex.infrastructure.exception.ObjectNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskTemplateServiceImpl implements TaskTemplateService {

  public static final String TASK_TEMPLATE_NOT_FOUND_WITH_ID = "TaskTemplate not found with {id}: ";
  private final TaskTemplateRepository taskTemplateRepository;
  private final TaskTemplateDtoMapper taskTemplateDtoMapper;
  private final TaskTemplateEntityMapper taskTemplateEntityMapper;
  private final TaskTemplateDirectMapper taskTemplateDirectMapper;
  private final TaskTemplateSpecificationBuilder taskTemplateSpecificationBuilder;

  public TaskTemplateResponseDto createTaskTemplate(TaskTemplateCreateDto taskTemplateCreateDto) {
    TaskTemplate domainTaskTemplate = taskTemplateDtoMapper.createDtoToDomain(
        taskTemplateCreateDto);
    TaskTemplateEntity taskTemplate = taskTemplateEntityMapper.domainToEntity(domainTaskTemplate);
    TaskTemplateEntity createdTaskTemplate = taskTemplateRepository.save(taskTemplate);
    return taskTemplateDirectMapper.entityToResponseDto(createdTaskTemplate);
  }

  @Transactional(readOnly = true)
  public Page<TaskTemplateResponseDto> findAllPage(TaskTemplateFilterDto filter,
      Pageable pageable) {
    Specification<TaskTemplateEntity> specification = taskTemplateSpecificationBuilder.build(
        filter);
    Page<TaskTemplateEntity> taskTemplatePage = taskTemplateRepository.findAll(specification,
        pageable);
    return taskTemplatePage.map(taskTemplateDirectMapper::entityToResponseDto);
  }

  @Transactional(readOnly = true)
  public TaskTemplateResponseDto findById(String id) {
    TaskTemplateEntity taskTemplate = taskTemplateRepository
        .findById(UUID.fromString(id))
        .orElseThrow(() -> new ObjectNotFoundException(TASK_TEMPLATE_NOT_FOUND_WITH_ID + id));
    return taskTemplateDirectMapper.entityToResponseDto(taskTemplate);
  }

  @Transactional
  public TaskTemplateResponseDto updateTaskTemplate(String id,
      TaskTemplateUpdateDto taskTemplateUpdateDto) {
    TaskTemplateEntity taskTemplate = taskTemplateRepository
        .findById(UUID.fromString(id))
        .orElseThrow(() -> new ObjectNotFoundException(TASK_TEMPLATE_NOT_FOUND_WITH_ID + id));
    taskTemplateDirectMapper.updateEntityFromDto(taskTemplate, taskTemplateUpdateDto);
    return taskTemplateDirectMapper.entityToResponseDto(taskTemplate);
  }

  @Transactional
  public void deleteTaskTemplate(String id) {
    TaskTemplateEntity taskTemplate = taskTemplateRepository
        .findById(UUID.fromString(id))
        .orElseThrow(() -> new ObjectNotFoundException(TASK_TEMPLATE_NOT_FOUND_WITH_ID + id));
    try {
      taskTemplateRepository.deleteById(taskTemplate.getId());
    } catch (DataIntegrityViolationException ex) {
      throw new DataBindingViolationException("Невозможно удалить, так как есть связанные объекты");
    }
  }
}
