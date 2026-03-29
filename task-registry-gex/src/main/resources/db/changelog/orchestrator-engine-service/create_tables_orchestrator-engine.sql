-- 1. Входящие сообщения из RabbitMQ
CREATE TABLE input_message_rabbitmq
(
    id         UUID         NOT NULL,
    graph_id   VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_input_message_rabbitmq_id PRIMARY KEY (id),
    CONSTRAINT idx_input_message_rabbitmq_graph_id UNIQUE (graph_id)
);

-- 1. Экземпляр задачи
CREATE TABLE task_instances
(
    id           UUID         NOT NULL,
    execution_id UUID         NOT NULL,
    template_id  VARCHAR(255) NOT NULL,
    status       VARCHAR(50)  NOT NULL,
    input        JSONB,
    output       JSONB,
    depends_on   JSONB,
    children     JSONB,
    created_at   TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    started_at   TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,

    CONSTRAINT pk_task_instances_id PRIMARY KEY (id)
);
CREATE INDEX idx_task_instances_execution_id ON task_instances (execution_id);

-- 1. Экземпляр процесса выполнения
CREATE TABLE workflow_execution
(
    id            UUID        NOT NULL,
    graph_id      UUID        NOT NULL,
    status        VARCHAR(50) NOT NULL,
    input         JSONB,
    output        JSONB,
    started_at    TIMESTAMPTZ,
    finished_at   TIMESTAMPTZ,
    error_message VARCHAR(255),

    CONSTRAINT pk_workflow_execution_id PRIMARY KEY (id),
    CONSTRAINT idx_workflow_execution_graph_id UNIQUE (graph_id)
);