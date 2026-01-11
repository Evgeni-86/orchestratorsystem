-- 1. Шаблоны задач
CREATE TABLE task_templates
(
    id            UUID         NOT NULL,
    name          VARCHAR(255) NOT NULL,
    version       VARCHAR(50)  NOT NULL DEFAULT '1.0',
    type          VARCHAR(50)  NOT NULL,
    input_schema  JSONB,
    output_schema JSONB,
    config        JSONB,
    created_at    TIMESTAMPTZ           DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ,

    CONSTRAINT pk_task_templates_id PRIMARY KEY (id)
);

-- 2. Графы
CREATE TABLE task_graphs
(
    id         UUID         NOT NULL,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_task_graphs_id PRIMARY KEY (id)
);

-- 3. Связь графов и шаблонов (многие-ко-многим)
CREATE TABLE graph_tasks
(
    graph_id    UUID REFERENCES task_graphs (id) ON DELETE CASCADE,
    template_id UUID REFERENCES task_templates (id),
    PRIMARY KEY (graph_id, template_id)
);

-- 4. Зависимости между шаблонами ВНУТРИ графа
CREATE TABLE task_dependencies
(
    id                 UUID        NOT NULL,
    graph_id           UUID        NOT NULL,
    parent_template_id UUID        NOT NULL,
    child_template_id  UUID        NOT NULL,
    condition          VARCHAR(50) NOT NULL,
    FOREIGN KEY (graph_id) REFERENCES task_graphs (id) ON DELETE CASCADE,
    FOREIGN KEY (parent_template_id) REFERENCES task_templates (id),
    FOREIGN KEY (child_template_id) REFERENCES task_templates (id),

    CONSTRAINT pk_task_dependencies_id PRIMARY KEY (id),
    CONSTRAINT no_self_dependency CHECK (parent_template_id != child_template_id
)
    );

-- 4. Outbox
CREATE TABLE task_templates_outbox
(
    id           UUID        NOT NULL,
    created_at   TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMPTZ,
    type         VARCHAR(50) NOT NULL,
    payload      VARCHAR,
    outbox_status VARCHAR(50) NOT NULL,
    version      INT,

    CONSTRAINT pk_task_templates_outbox_id PRIMARY KEY (id)
);

-- 6. Индексы для производительности
CREATE INDEX idx_graph_tasks_graph ON graph_tasks (graph_id);
CREATE INDEX idx_graph_tasks_template ON graph_tasks (template_id);
CREATE INDEX idx_dependencies_graph ON task_dependencies (graph_id);
CREATE INDEX idx_dependencies_parent ON task_dependencies (parent_template_id);
CREATE INDEX idx_dependencies_child ON task_dependencies (child_template_id);
CREATE INDEX idx_task_templates_outbox_outbox_status ON task_templates_outbox (outbox_status);