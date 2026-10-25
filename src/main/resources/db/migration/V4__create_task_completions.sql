CREATE TABLE task_completions (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    completion_date DATE NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT TRUE,
    completed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_task_completion_date UNIQUE (task_id, completion_date)
);

CREATE INDEX idx_completion_task_date ON task_completions(task_id, completion_date);
