package com.progress.tracker.dto;

public class TaskProgressStats {
    private long totalPossibleDays;
    private long completedDays;
    private long missedDays;
    private long remainingDays;
    private double completionPercentage;
    private int currentStreak;
    private int longestStreak;
    private boolean completedToday;

    public TaskProgressStats() {
    }

    public TaskProgressStats(long totalPossibleDays, long completedDays, long missedDays, long remainingDays, double completionPercentage, int currentStreak, int longestStreak, boolean completedToday) {
        this.totalPossibleDays = totalPossibleDays;
        this.completedDays = completedDays;
        this.missedDays = missedDays;
        this.remainingDays = remainingDays;
        this.completionPercentage = completionPercentage;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.completedToday = completedToday;
    }

    public long getTotalPossibleDays() { return totalPossibleDays; }
    public void setTotalPossibleDays(long totalPossibleDays) { this.totalPossibleDays = totalPossibleDays; }

    public long getCompletedDays() { return completedDays; }
    public void setCompletedDays(long completedDays) { this.completedDays = completedDays; }

    public long getMissedDays() { return missedDays; }
    public void setMissedDays(long missedDays) { this.missedDays = missedDays; }

    public long getRemainingDays() { return remainingDays; }
    public void setRemainingDays(long remainingDays) { this.remainingDays = remainingDays; }

    public double getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(double completionPercentage) { this.completionPercentage = completionPercentage; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }

    public boolean isCompletedToday() { return completedToday; }
    public void setCompletedToday(boolean completedToday) { this.completedToday = completedToday; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private long totalPossibleDays;
        private long completedDays;
        private long missedDays;
        private long remainingDays;
        private double completionPercentage;
        private int currentStreak;
        private int longestStreak;
        private boolean completedToday;

        public Builder totalPossibleDays(long totalPossibleDays) { this.totalPossibleDays = totalPossibleDays; return this; }
        public Builder completedDays(long completedDays) { this.completedDays = completedDays; return this; }
        public Builder missedDays(long missedDays) { this.missedDays = missedDays; return this; }
        public Builder remainingDays(long remainingDays) { this.remainingDays = remainingDays; return this; }
        public Builder completionPercentage(double completionPercentage) { this.completionPercentage = completionPercentage; return this; }
        public Builder currentStreak(int currentStreak) { this.currentStreak = currentStreak; return this; }
        public Builder longestStreak(int longestStreak) { this.longestStreak = longestStreak; return this; }
        public Builder completedToday(boolean completedToday) { this.completedToday = completedToday; return this; }

        public TaskProgressStats build() {
            return new TaskProgressStats(totalPossibleDays, completedDays, missedDays, remainingDays, completionPercentage, currentStreak, longestStreak, completedToday);
        }
    }
}
