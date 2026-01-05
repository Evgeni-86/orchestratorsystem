package com.taskorchestrator.task_registry.service;

import com.taskorchestrator.task_registry.dto.TaskTemplateUpdateDto;
import com.taskorchestrator.task_registry.dto.task.TaskTemplateCreateDto;
import com.taskorchestrator.task_registry.dto.task.TaskTemplateResponseDto;
import com.taskorchestrator.task_registry.entity.TaskTemplateEntity;
import com.taskorchestrator.task_registry.exception.DataBindingViolationException;
import com.taskorchestrator.task_registry.exception.ObjectNotFoundException;
import com.taskorchestrator.task_registry.mapper.TaskTemplateMapper;
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

  private final TaskTemplateRepository taskTemplateRepository;
  private final TaskTemplateMapper taskTemplateMapper;

  public TaskTemplateResponseDto createTaskTemplate(TaskTemplateCreateDto taskTemplateCreateDto) {
    TaskTemplateEntity taskTemplate = taskTemplateMapper.toEntity();
    TaskTemplateEntity createdTaskTemplate = taskTemplateRepository.save(taskTemplate);
    return taskTemplateMapper.toDto(createdTaskTemplate);
  }

  @Transactional(readOnly = true)
  public Page<TaskTemplateResponseDto> findAllPage(Pageable pageable) {
    Page<TaskTemplateEntity> taskTemplatePage = taskTemplateRepository.findAll(pageable);
    return taskTemplatePage.map(taskTemplateMapper::toDto);
  }

  @Transactional(readOnly = true)
  public TaskTemplateResponseDto findById(String id) {
    TaskTemplateEntity taskTemplate = taskTemplateRepository
        .findById(UUID.fromString(id))
        .orElseThrow(() -> new ObjectNotFoundException("TaskTemplate not found with {id}: " + id));
    return taskTemplateMapper.toDto(taskTemplate);
  }

  @Transactional
  public TaskTemplateResponseDto updateTaskTemplate(String id,
      TaskTemplateUpdateDto taskTemplateUpdateDto) {
    TaskTemplateEntity taskTemplate = taskTemplateRepository
        .findById(UUID.fromString(id))
        .orElseThrow(() -> new ObjectNotFoundException("TaskTemplate not found with {id}: " + id));
    taskTemplateMapper.updateTaskTemplateFromDto(taskTemplateUpdateDto, taskTemplate);
    TaskTemplateEntity updateTaskTemplate = taskTemplateRepository.save(taskTemplate);
    return taskTemplateMapper.toDto(updateTaskTemplate);
  }

  @Transactional
  public void deleteTaskTemplate(String id) {
    TaskTemplateEntity taskTemplate = taskTemplateRepository
        .findById(UUID.fromString(id))
        .orElseThrow(() -> new ObjectNotFoundException("TaskTemplate not found with {id}: " + id));
    try {
      taskTemplateRepository.deleteById(taskTemplate.getId());
    } catch (DataIntegrityViolationException ex) {
      throw new DataBindingViolationException("Невозможно удалить, так как есть связанные объекты");
    }
  }
}
