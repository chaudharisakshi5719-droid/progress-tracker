package com.progress.tracker.dto;

import java.time.LocalDate;

public class StreakStats {
    private int currentStreak;
    private int longestStreak;
    private LocalDate lastCompletedDate;
    private String streakStatusLabel;

    public StreakStats() {
    }

    public StreakStats(int currentStreak, int longestStreak, LocalDate lastCompletedDate, String streakStatusLabel) {
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.lastCompletedDate = lastCompletedDate;
        this.streakStatusLabel = streakStatusLabel;
    }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }

    public LocalDate getLastCompletedDate() { return lastCompletedDate; }
    public void setLastCompletedDate(LocalDate lastCompletedDate) { this.lastCompletedDate = lastCompletedDate; }

    public String getStreakStatusLabel() { return streakStatusLabel; }
    public void setStreakStatusLabel(String streakStatusLabel) { this.streakStatusLabel = streakStatusLabel; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private int currentStreak;
        private int longestStreak;
        private LocalDate lastCompletedDate;
        private String streakStatusLabel;

        public Builder currentStreak(int currentStreak) { this.currentStreak = currentStreak; return this; }
        public Builder longestStreak(int longestStreak) { this.longestStreak = longestStreak; return this; }
        public Builder lastCompletedDate(LocalDate lastCompletedDate) { this.lastCompletedDate = lastCompletedDate; return this; }
        public Builder streakStatusLabel(String streakStatusLabel) { this.streakStatusLabel = streakStatusLabel; return this; }

        public StreakStats build() {
            return new StreakStats(currentStreak, longestStreak, lastCompletedDate, streakStatusLabel);
        }
    }
}
