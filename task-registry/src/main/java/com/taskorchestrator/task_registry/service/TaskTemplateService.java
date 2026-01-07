package com.taskorchestrator.task_registry.service;

import com.taskorchestrator.task_registry.domain.TaskTemplate;
import com.taskorchestrator.task_registry.dto.task.TaskTemplateUpdateDto;
import com.taskorchestrator.task_registry.dto.task.TaskTemplateCreateDto;
import com.taskorchestrator.task_registry.dto.task.TaskTemplateResponseDto;
import com.taskorchestrator.task_registry.entity.TaskTemplateEntity;
import com.taskorchestrator.task_registry.exception.DataBindingViolationException;
import com.taskorchestrator.task_registry.exception.ObjectNotFoundException;
import com.taskorchestrator.task_registry.mapper.tasktemplate.TaskTemplateDtoMapper;
import com.taskorchestrator.task_registry.mapper.tasktemplate.TaskTemplateEntityMapper;
import com.taskorchestrator.task_registry.mapper.tasktemplate.TaskTemplateDirectMapper;
import com.taskorchestrator.task_registry.repository.TaskTemplateRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskTemplateService {

  public static final String TASK_TEMPLATE_NOT_FOUND_WITH_ID = "TaskTemplate not found with {id}: ";
  private final TaskTemplateRepository taskTemplateRepository;
  private final TaskTemplateDtoMapper taskTemplateDtoMapper;
  private final TaskTemplateEntityMapper taskTemplateEntityMapper;
  private final TaskTemplateDirectMapper taskTemplateDirectMapper;

  public TaskTemplateResponseDto createTaskTemplate(TaskTemplateCreateDto taskTemplateCreateDto) {
    TaskTemplate domainTaskTemplate = taskTemplateDtoMapper.createDtoToDomain(
        taskTemplateCreateDto);
    TaskTemplateEntity taskTemplate = taskTemplateEntityMapper.domainToEntity(domainTaskTemplate);
    TaskTemplateEntity createdTaskTemplate = taskTemplateRepository.save(taskTemplate);
    return taskTemplateDirectMapper.entityToResponseDto(createdTaskTemplate);
  }

  @Transactional(readOnly = true)
  public Page<TaskTemplateResponseDto> findAllPage(Pageable pageable) {
    Page<TaskTemplateEntity> taskTemplatePage = taskTemplateRepository.findAll(pageable);
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
