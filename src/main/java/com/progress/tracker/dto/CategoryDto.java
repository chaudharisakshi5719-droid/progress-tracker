package com.progress.tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryDto {

    private Long id;

    @NotBlank(message = "Category name is required")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @NotBlank(message = "Color is required")
    private String color = "#0d6efd";

    private long taskCount;

    public CategoryDto() {
        this.color = "#0d6efd";
    }

    public CategoryDto(Long id, String name, String description, String color, long taskCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color != null ? color : "#0d6efd";
        this.taskCount = taskCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColor() { return color != null ? color : "#0d6efd"; }
    public void setColor(String color) { this.color = color; }

    public long getTaskCount() { return taskCount; }
    public void setTaskCount(long taskCount) { this.taskCount = taskCount; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private String color = "#0d6efd";
        private long taskCount;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder color(String color) { this.color = color; return this; }
        public Builder taskCount(long taskCount) { this.taskCount = taskCount; return this; }

        public CategoryDto build() {
            return new CategoryDto(id, name, description, color, taskCount);
        }
    }
}
