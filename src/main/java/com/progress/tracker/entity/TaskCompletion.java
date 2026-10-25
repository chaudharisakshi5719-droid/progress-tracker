package com.progress.tracker.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "task_completions",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_task_completion_date", columnNames = {"task_id", "completion_date"})
       },
       indexes = {
           @Index(name = "idx_completion_task_date", columnList = "task_id, completion_date")
       })
public class TaskCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "completion_date", nullable = false)
    private LocalDate completionDate;

    @Column(nullable = false)
    private boolean completed = true;

    @Column(name = "completed_at", nullable = false)
    private Instant completedAt;

    public TaskCompletion() {
    }

    public TaskCompletion(Long id, Task task, LocalDate completionDate, boolean completed, Instant completedAt) {
        this.id = id;
        this.task = task;
        this.completionDate = completionDate;
        this.completed = completed;
        this.completedAt = completedAt != null ? completedAt : Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.completedAt == null) {
            this.completedAt = Instant.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }

    public LocalDate getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDate completionDate) { this.completionDate = completionDate; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Task task;
        private LocalDate completionDate;
        private boolean completed = true;
        private Instant completedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder task(Task task) { this.task = task; return this; }
        public Builder completionDate(LocalDate completionDate) { this.completionDate = completionDate; return this; }
        public Builder completed(boolean completed) { this.completed = completed; return this; }
        public Builder completedAt(Instant completedAt) { this.completedAt = completedAt; return this; }

        public TaskCompletion build() {
            return new TaskCompletion(id, task, completionDate, completed, completedAt);
        }
    }
}
