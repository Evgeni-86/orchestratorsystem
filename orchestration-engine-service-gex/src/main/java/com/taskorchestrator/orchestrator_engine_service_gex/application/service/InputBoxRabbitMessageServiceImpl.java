package com.taskorchestrator.orchestrator_engine_service_gex.application.service;

import com.taskorchestrator.orchestrator_engine_service_gex.adapter.in.rabbit.dto.TaskGraphEventPayloadDto;
import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.entity.InputTaskGraphRabbitMessageEntity;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.in.InputBoxRabbitMessageService;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.out.persistence.InputBoxMessageRepository;
import com.taskorchestrator.orchestrator_engine_service_gex.application.service.mappers.InputBoxRabbitMessageDirectMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InputBoxRabbitMessageServiceImpl implements InputBoxRabbitMessageService {

  private final InputBoxMessageRepository inputBoxMessageRepository;
  private final InputBoxRabbitMessageDirectMapper inputBoxRabbitMessageDirectMapper;

  @Override
  public void handleNewMessage(TaskGraphEventPayloadDto eventPayloadDto) {
    InputTaskGraphRabbitMessageEntity messageEntity = inputBoxRabbitMessageDirectMapper.dtoToEntity(
        eventPayloadDto);
    messageEntity.setId(UUID.randomUUID());
    //Сохранение сообщения в БД
    inputBoxMessageRepository.insertIfNotExists(messageEntity);
  }
}
