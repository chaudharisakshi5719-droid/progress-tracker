package com.progress.tracker.dto;

import java.time.LocalDate;

public class DailyCompletionStatus {
    private LocalDate date;
    private String status;
    private boolean completed;
    private String statusBadgeClass;

    public DailyCompletionStatus() {
    }

    public DailyCompletionStatus(LocalDate date, String status, boolean completed, String statusBadgeClass) {
        this.date = date;
        this.status = status;
        this.completed = completed;
        this.statusBadgeClass = statusBadgeClass;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getStatusBadgeClass() { return statusBadgeClass; }
    public void setStatusBadgeClass(String statusBadgeClass) { this.statusBadgeClass = statusBadgeClass; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private LocalDate date;
        private String status;
        private boolean completed;
        private String statusBadgeClass;

        public Builder date(LocalDate date) { this.date = date; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder completed(boolean completed) { this.completed = completed; return this; }
        public Builder statusBadgeClass(String statusBadgeClass) { this.statusBadgeClass = statusBadgeClass; return this; }

        public DailyCompletionStatus build() {
            return new DailyCompletionStatus(date, status, completed, statusBadgeClass);
        }
    }
}
