package com.progress.tracker.service;

import com.progress.tracker.dto.StreakStats;
import com.progress.tracker.dto.TaskProgressStats;
import com.progress.tracker.entity.Task;
import com.progress.tracker.entity.TaskCompletion;
import com.progress.tracker.repository.TaskCompletionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StreakServiceImpl implements StreakService {

    private final TaskCompletionRepository completionRepository;

    public StreakServiceImpl(TaskCompletionRepository completionRepository) {
        this.completionRepository = completionRepository;
    }

    @Override
    public StreakStats calculateStreak(Task task, LocalDate referenceDate) {
        List<TaskCompletion> completions = completionRepository.findByTaskAndCompletedTrueOrderByCompletionDateAsc(task);
        Set<LocalDate> completedDates = completions.stream()
                .map(TaskCompletion::getCompletionDate)
                .filter(d -> !d.isAfter(referenceDate))
                .collect(Collectors.toSet());

        if (completedDates.isEmpty()) {
            return StreakStats.builder()
                    .currentStreak(0)
                    .longestStreak(0)
                    .lastCompletedDate(null)
                    .streakStatusLabel("No completions yet")
                    .build();
        }

        LocalDate maxCompletedDate = completedDates.stream().max(LocalDate::compareTo).orElse(null);

        int longestStreak = calculateLongestStreak(completedDates, task.getStartDate(), referenceDate);

        int currentStreak;
        String statusLabel;

        if (completedDates.contains(referenceDate)) {
            currentStreak = countConsecutiveBackwards(completedDates, referenceDate);
            statusLabel = "Last completed: Today";
        } else if (completedDates.contains(referenceDate.minusDays(1))) {
            currentStreak = countConsecutiveBackwards(completedDates, referenceDate.minusDays(1));
            statusLabel = "Last completed: Yesterday";
        } else {
            currentStreak = 0;
            statusLabel = maxCompletedDate != null ? "Last completed: " + maxCompletedDate : "No recent completions";
        }

        return StreakStats.builder()
                .currentStreak(currentStreak)
                .longestStreak(longestStreak)
                .lastCompletedDate(maxCompletedDate)
                .streakStatusLabel(statusLabel)
                .build();
    }

    private int countConsecutiveBackwards(Set<LocalDate> completedDates, LocalDate startDate) {
        int count = 0;
        LocalDate curr = startDate;
        while (completedDates.contains(curr)) {
            count++;
            curr = curr.minusDays(1);
        }
        return count;
    }

    @Override
    public int calculateLongestStreak(Set<LocalDate> completedDates, LocalDate startDate, LocalDate referenceDate) {
        if (completedDates.isEmpty()) {
            return 0;
        }

        List<LocalDate> sortedDates = completedDates.stream()
                .filter(d -> !d.isBefore(startDate) && !d.isAfter(referenceDate))
                .sorted()
                .toList();

        if (sortedDates.isEmpty()) {
            return 0;
        }

        int maxStreak = 1;
        int tempStreak = 1;

        for (int i = 1; i < sortedDates.size(); i++) {
            LocalDate prev = sortedDates.get(i - 1);
            LocalDate curr = sortedDates.get(i);

            if (prev.plusDays(1).equals(curr)) {
                tempStreak++;
            } else if (!prev.equals(curr)) {
                tempStreak = 1;
            }

            if (tempStreak > maxStreak) {
                maxStreak = tempStreak;
            }
        }

        return maxStreak;
    }

    @Override
    public TaskProgressStats calculateProgressStats(Task task, LocalDate referenceDate) {
        List<TaskCompletion> completions = completionRepository.findByTaskAndCompletedTrueOrderByCompletionDateAsc(task);
        Set<LocalDate> completedDates = completions.stream()
                .map(TaskCompletion::getCompletionDate)
                .filter(d -> !d.isAfter(referenceDate))
                .collect(Collectors.toSet());

        LocalDate effectiveEnd = referenceDate.isBefore(task.getEndDate()) ? referenceDate : task.getEndDate();

        long totalPossibleDays = 0;
        if (!referenceDate.isBefore(task.getStartDate())) {
            totalPossibleDays = ChronoUnit.DAYS.between(task.getStartDate(), effectiveEnd) + 1;
        }
        if (totalPossibleDays < 0) totalPossibleDays = 0;

        long completedDays = completedDates.stream()
                .filter(d -> !d.isBefore(task.getStartDate()) && !d.isAfter(effectiveEnd))
                .count();

        long missedDays = totalPossibleDays - completedDays;
        if (missedDays < 0) missedDays = 0;

        long remainingDays = 0;
        if (task.getEndDate().isAfter(referenceDate)) {
            LocalDate calcStart = task.getStartDate().isAfter(referenceDate) ? task.getStartDate() : referenceDate.plusDays(1);
            remainingDays = ChronoUnit.DAYS.between(calcStart, task.getEndDate()) + 1;
            if (remainingDays < 0) remainingDays = 0;
        }

        double completionPercentage = totalPossibleDays > 0
                ? Math.round((double) completedDays / totalPossibleDays * 100.0 * 10.0) / 10.0
                : 0.0;

        boolean completedToday = completedDates.contains(referenceDate);

        StreakStats streakStats = calculateStreak(task, referenceDate);

        return TaskProgressStats.builder()
                .totalPossibleDays(totalPossibleDays)
                .completedDays(completedDays)
                .missedDays(missedDays)
                .remainingDays(remainingDays)
                .completionPercentage(completionPercentage)
                .currentStreak(streakStats.getCurrentStreak())
                .longestStreak(streakStats.getLongestStreak())
                .completedToday(completedToday)
                .build();
    }
}
