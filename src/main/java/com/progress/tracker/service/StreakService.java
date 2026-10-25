package com.progress.tracker.service;

import com.progress.tracker.dto.StreakStats;
import com.progress.tracker.dto.TaskProgressStats;
import com.progress.tracker.entity.Task;

import java.time.LocalDate;

public interface StreakService {
    StreakStats calculateStreak(Task task, LocalDate referenceDate);
    int calculateLongestStreak(java.util.Set<LocalDate> completedDates, LocalDate startDate, LocalDate referenceDate);
    TaskProgressStats calculateProgressStats(Task task, LocalDate referenceDate);
}
