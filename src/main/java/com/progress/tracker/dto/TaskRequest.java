package com.progress.tracker.dto;

import com.progress.tracker.entity.TargetFrequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class TaskRequest {

    private Long id;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Start date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @NotNull(message = "Target frequency is required")
    private TargetFrequency targetFrequency = TargetFrequency.DAILY;

    private boolean active = true;

    public TaskRequest() {
    }

    public TaskRequest(Long id, Long categoryId, String title, String description, LocalDate startDate, LocalDate endDate, TargetFrequency targetFrequency, boolean active) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetFrequency = targetFrequency != null ? targetFrequency : TargetFrequency.DAILY;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public TargetFrequency getTargetFrequency() { return targetFrequency; }
    public void setTargetFrequency(TargetFrequency targetFrequency) { this.targetFrequency = targetFrequency; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long categoryId;
        private String title;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;
        private TargetFrequency targetFrequency = TargetFrequency.DAILY;
        private boolean active = true;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder targetFrequency(TargetFrequency targetFrequency) { this.targetFrequency = targetFrequency; return this; }
        public Builder active(boolean active) { this.active = active; return this; }

        public TaskRequest build() {
            return new TaskRequest(id, categoryId, title, description, startDate, endDate, targetFrequency, active);
        }
    }
}
