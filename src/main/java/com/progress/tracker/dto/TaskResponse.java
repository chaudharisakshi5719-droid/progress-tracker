package com.progress.tracker.dto;

import com.progress.tracker.entity.TargetFrequency;

import java.time.Instant;
import java.time.LocalDate;

public class TaskResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private TargetFrequency targetFrequency;
    private boolean active;
    private boolean completedToday;
    private int currentStreak;
    private int longestStreak;
    private double completionPercentage;
    private long totalPossibleDays;
    private long completedDays;
    private long missedDays;
    private long remainingDays;
    private Instant createdAt;
    private Instant updatedAt;

    public TaskResponse() {
    }

    public TaskResponse(Long id, Long categoryId, String categoryName, String categoryColor, String title, String description, LocalDate startDate, LocalDate endDate, TargetFrequency targetFrequency, boolean active, boolean completedToday, int currentStreak, int longestStreak, double completionPercentage, long totalPossibleDays, long completedDays, long missedDays, long remainingDays, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetFrequency = targetFrequency;
        this.active = active;
        this.completedToday = completedToday;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.completionPercentage = completionPercentage;
        this.totalPossibleDays = totalPossibleDays;
        this.completedDays = completedDays;
        this.missedDays = missedDays;
        this.remainingDays = remainingDays;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getCategoryColor() { return categoryColor; }
    public void setCategoryColor(String categoryColor) { this.categoryColor = categoryColor; }

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

    public boolean isCompletedToday() { return completedToday; }
    public void setCompletedToday(boolean completedToday) { this.completedToday = completedToday; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }

    public double getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(double completionPercentage) { this.completionPercentage = completionPercentage; }

    public long getTotalPossibleDays() { return totalPossibleDays; }
    public void setTotalPossibleDays(long totalPossibleDays) { this.totalPossibleDays = totalPossibleDays; }

    public long getCompletedDays() { return completedDays; }
    public void setCompletedDays(long completedDays) { this.completedDays = completedDays; }

    public long getMissedDays() { return missedDays; }
    public void setMissedDays(long missedDays) { this.missedDays = missedDays; }

    public long getRemainingDays() { return remainingDays; }
    public void setRemainingDays(long remainingDays) { this.remainingDays = remainingDays; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long categoryId;
        private String categoryName;
        private String categoryColor;
        private String title;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;
        private TargetFrequency targetFrequency;
        private boolean active;
        private boolean completedToday;
        private int currentStreak;
        private int longestStreak;
        private double completionPercentage;
        private long totalPossibleDays;
        private long completedDays;
        private long missedDays;
        private long remainingDays;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder categoryColor(String categoryColor) { this.categoryColor = categoryColor; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder targetFrequency(TargetFrequency targetFrequency) { this.targetFrequency = targetFrequency; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder completedToday(boolean completedToday) { this.completedToday = completedToday; return this; }
        public Builder currentStreak(int currentStreak) { this.currentStreak = currentStreak; return this; }
        public Builder longestStreak(int longestStreak) { this.longestStreak = longestStreak; return this; }
        public Builder completionPercentage(double completionPercentage) { this.completionPercentage = completionPercentage; return this; }
        public Builder totalPossibleDays(long totalPossibleDays) { this.totalPossibleDays = totalPossibleDays; return this; }
        public Builder completedDays(long completedDays) { this.completedDays = completedDays; return this; }
        public Builder missedDays(long missedDays) { this.missedDays = missedDays; return this; }
        public Builder remainingDays(long remainingDays) { this.remainingDays = remainingDays; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public TaskResponse build() {
            return new TaskResponse(id, categoryId, categoryName, categoryColor, title, description, startDate, endDate, targetFrequency, active, completedToday, currentStreak, longestStreak, completionPercentage, totalPossibleDays, completedDays, missedDays, remainingDays, createdAt, updatedAt);
        }
    }
}
