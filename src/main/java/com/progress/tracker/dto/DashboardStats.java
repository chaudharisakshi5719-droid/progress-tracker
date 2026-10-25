package com.progress.tracker.dto;

import java.util.List;

public class DashboardStats {
    private long totalTasks;
    private long activeTasks;
    private long completedTodayCount;
    private int bestCurrentStreak;
    private int bestLongestStreak;
    private double overallCompletionPercentage;

    private List<String> categoryChartLabels;
    private List<Double> categoryChartData;
    private List<String> categoryChartColors;

    private List<String> weeklyChartLabels;
    private List<Long> weeklyChartData;

    private List<HeatmapCell> heatmapData;

    public DashboardStats() {
    }

    public DashboardStats(long totalTasks, long activeTasks, long completedTodayCount, int bestCurrentStreak, int bestLongestStreak, double overallCompletionPercentage, List<String> categoryChartLabels, List<Double> categoryChartData, List<String> categoryChartColors, List<String> weeklyChartLabels, List<Long> weeklyChartData, List<HeatmapCell> heatmapData) {
        this.totalTasks = totalTasks;
        this.activeTasks = activeTasks;
        this.completedTodayCount = completedTodayCount;
        this.bestCurrentStreak = bestCurrentStreak;
        this.bestLongestStreak = bestLongestStreak;
        this.overallCompletionPercentage = overallCompletionPercentage;
        this.categoryChartLabels = categoryChartLabels;
        this.categoryChartData = categoryChartData;
        this.categoryChartColors = categoryChartColors;
        this.weeklyChartLabels = weeklyChartLabels;
        this.weeklyChartData = weeklyChartData;
        this.heatmapData = heatmapData;
    }

    public long getTotalTasks() { return totalTasks; }
    public void setTotalTasks(long totalTasks) { this.totalTasks = totalTasks; }

    public long getActiveTasks() { return activeTasks; }
    public void setActiveTasks(long activeTasks) { this.activeTasks = activeTasks; }

    public long getCompletedTodayCount() { return completedTodayCount; }
    public void setCompletedTodayCount(long completedTodayCount) { this.completedTodayCount = completedTodayCount; }

    public int getBestCurrentStreak() { return bestCurrentStreak; }
    public void setBestCurrentStreak(int bestCurrentStreak) { this.bestCurrentStreak = bestCurrentStreak; }

    public int getBestLongestStreak() { return bestLongestStreak; }
    public void setBestLongestStreak(int bestLongestStreak) { this.bestLongestStreak = bestLongestStreak; }

    public double getOverallCompletionPercentage() { return overallCompletionPercentage; }
    public void setOverallCompletionPercentage(double overallCompletionPercentage) { this.overallCompletionPercentage = overallCompletionPercentage; }

    public List<String> getCategoryChartLabels() { return categoryChartLabels; }
    public void setCategoryChartLabels(List<String> categoryChartLabels) { this.categoryChartLabels = categoryChartLabels; }

    public List<Double> getCategoryChartData() { return categoryChartData; }
    public void setCategoryChartData(List<Double> categoryChartData) { this.categoryChartData = categoryChartData; }

    public List<String> getCategoryChartColors() { return categoryChartColors; }
    public void setCategoryChartColors(List<String> categoryChartColors) { this.categoryChartColors = categoryChartColors; }

    public List<String> getWeeklyChartLabels() { return weeklyChartLabels; }
    public void setWeeklyChartLabels(List<String> weeklyChartLabels) { this.weeklyChartLabels = weeklyChartLabels; }

    public List<Long> getWeeklyChartData() { return weeklyChartData; }
    public void setWeeklyChartData(List<Long> weeklyChartData) { this.weeklyChartData = weeklyChartData; }

    public List<HeatmapCell> getHeatmapData() { return heatmapData; }
    public void setHeatmapData(List<HeatmapCell> heatmapData) { this.heatmapData = heatmapData; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private long totalTasks;
        private long activeTasks;
        private long completedTodayCount;
        private int bestCurrentStreak;
        private int bestLongestStreak;
        private double overallCompletionPercentage;
        private List<String> categoryChartLabels;
        private List<Double> categoryChartData;
        private List<String> categoryChartColors;
        private List<String> weeklyChartLabels;
        private List<Long> weeklyChartData;
        private List<HeatmapCell> heatmapData;

        public Builder totalTasks(long totalTasks) { this.totalTasks = totalTasks; return this; }
        public Builder activeTasks(long activeTasks) { this.activeTasks = activeTasks; return this; }
        public Builder completedTodayCount(long completedTodayCount) { this.completedTodayCount = completedTodayCount; return this; }
        public Builder bestCurrentStreak(int bestCurrentStreak) { this.bestCurrentStreak = bestCurrentStreak; return this; }
        public Builder bestLongestStreak(int bestLongestStreak) { this.bestLongestStreak = bestLongestStreak; return this; }
        public Builder overallCompletionPercentage(double overallCompletionPercentage) { this.overallCompletionPercentage = overallCompletionPercentage; return this; }
        public Builder categoryChartLabels(List<String> categoryChartLabels) { this.categoryChartLabels = categoryChartLabels; return this; }
        public Builder categoryChartData(List<Double> categoryChartData) { this.categoryChartData = categoryChartData; return this; }
        public Builder categoryChartColors(List<String> categoryChartColors) { this.categoryChartColors = categoryChartColors; return this; }
        public Builder weeklyChartLabels(List<String> weeklyChartLabels) { this.weeklyChartLabels = weeklyChartLabels; return this; }
        public Builder weeklyChartData(List<Long> weeklyChartData) { this.weeklyChartData = weeklyChartData; return this; }
        public Builder heatmapData(List<HeatmapCell> heatmapData) { this.heatmapData = heatmapData; return this; }

        public DashboardStats build() {
            return new DashboardStats(totalTasks, activeTasks, completedTodayCount, bestCurrentStreak, bestLongestStreak, overallCompletionPercentage, categoryChartLabels, categoryChartData, categoryChartColors, weeklyChartLabels, weeklyChartData, heatmapData);
        }
    }
}
