package com.taskorchestrator.orchestrator_engine_service_gex.adapter.in.rabbit;

import com.taskorchestrator.orchestrator_engine_service_gex.adapter.in.rabbit.dto.TaskGraphEventPayloadDto;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.in.InputBoxRabbitMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskGraphEventListener {

  public final InputBoxRabbitMessageService inputBoxRabbitMessageService;

  @RabbitListener(
      queues = "${rabbit.queue.task-graph}",
      containerFactory = "taskGraphContainerFactory"
  )
  public void handleTaskGraphCreated(TaskGraphEventPayloadDto event) {
    log.info("Received task graph event: {}", event);
    //Сохранение в БД (в сервисе метод save)
    inputBoxRabbitMessageService.handleNewMessage(event);
    /*Допустим сдесь есть еще какая-то логика и в конце метод падает с ошибкой,
     *происходит повторная обработка сообщения*/
  }
}
