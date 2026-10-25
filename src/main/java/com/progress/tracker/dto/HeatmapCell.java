package com.progress.tracker.dto;

import java.time.LocalDate;

public class HeatmapCell {
    private LocalDate date;
    private long completedCount;
    private int intensity;
    private String tooltipText;

    public HeatmapCell() {
    }

    public HeatmapCell(LocalDate date, long completedCount, int intensity, String tooltipText) {
        this.date = date;
        this.completedCount = completedCount;
        this.intensity = intensity;
        this.tooltipText = tooltipText;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public long getCompletedCount() { return completedCount; }
    public void setCompletedCount(long completedCount) { this.completedCount = completedCount; }

    public int getIntensity() { return intensity; }
    public void setIntensity(int intensity) { this.intensity = intensity; }

    public String getTooltipText() { return tooltipText; }
    public void setTooltipText(String tooltipText) { this.tooltipText = tooltipText; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private LocalDate date;
        private long completedCount;
        private int intensity;
        private String tooltipText;

        public Builder date(LocalDate date) { this.date = date; return this; }
        public Builder completedCount(long completedCount) { this.completedCount = completedCount; return this; }
        public Builder intensity(int intensity) { this.intensity = intensity; return this; }
        public Builder tooltipText(String tooltipText) { this.tooltipText = tooltipText; return this; }

        public HeatmapCell build() {
            return new HeatmapCell(date, completedCount, intensity, tooltipText);
        }
    }
}
