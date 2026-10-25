package com.progress.tracker.service;

import com.progress.tracker.dto.DashboardStats;
import com.progress.tracker.dto.HeatmapCell;
import com.progress.tracker.dto.TaskResponse;
import com.progress.tracker.entity.Category;
import com.progress.tracker.entity.Task;
import com.progress.tracker.entity.TaskCompletion;
import com.progress.tracker.entity.User;
import com.progress.tracker.repository.CategoryRepository;
import com.progress.tracker.repository.TaskCompletionRepository;
import com.progress.tracker.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final TaskRepository taskRepository;
    private final TaskCompletionRepository completionRepository;
    private final CategoryRepository categoryRepository;
    private final TaskService taskService;
    private final StreakService streakService;

    public DashboardService(TaskRepository taskRepository,
                            TaskCompletionRepository completionRepository,
                            CategoryRepository categoryRepository,
                            TaskService taskService,
                            StreakService streakService) {
        this.taskRepository = taskRepository;
        this.completionRepository = completionRepository;
        this.categoryRepository = categoryRepository;
        this.taskService = taskService;
        this.streakService = streakService;
    }

    @Transactional(readOnly = true)
    public DashboardStats getDashboardStats(User user) {
        LocalDate today = LocalDate.now();
        List<Task> userTasks = taskRepository.findByUser(user);

        long totalTasks = userTasks.size();
        long activeTasks = userTasks.stream().filter(Task::isActive).count();

        long completedTodayCount = completionRepository.countCompletedByUserAndDate(user, today);

        List<TaskResponse> taskResponses = userTasks.stream()
                .filter(Task::isActive)
                .map(task -> taskService.mapToTaskResponse(task, today))
                .toList();

        int bestCurrentStreak = taskResponses.stream()
                .mapToInt(TaskResponse::getCurrentStreak)
                .max()
                .orElse(0);

        int bestLongestStreak = taskResponses.stream()
                .mapToInt(TaskResponse::getLongestStreak)
                .max()
                .orElse(0);

        double overallCompletionPercentage = 0.0;
        long totalCompletedDays = taskResponses.stream().mapToLong(TaskResponse::getCompletedDays).sum();
        long totalPossibleDays = taskResponses.stream().mapToLong(TaskResponse::getTotalPossibleDays).sum();

        if (totalPossibleDays > 0) {
            overallCompletionPercentage = Math.round((double) totalCompletedDays / totalPossibleDays * 100.0 * 10.0) / 10.0;
        }

        List<Category> allCategories = categoryRepository.findAll();
        List<String> categoryChartLabels = new ArrayList<>();
        List<Double> categoryChartData = new ArrayList<>();
        List<String> categoryChartColors = new ArrayList<>();

        for (Category cat : allCategories) {
            List<TaskResponse> catTasks = taskResponses.stream()
                    .filter(t -> t.getCategoryId().equals(cat.getId()))
                    .toList();

            if (!catTasks.isEmpty()) {
                long catCompleted = catTasks.stream().mapToLong(TaskResponse::getCompletedDays).sum();
                long catPossible = catTasks.stream().mapToLong(TaskResponse::getTotalPossibleDays).sum();
                double catPct = catPossible > 0 ? Math.round((double) catCompleted / catPossible * 100.0 * 10.0) / 10.0 : 0.0;

                categoryChartLabels.add(cat.getName());
                categoryChartData.add(catPct);
                categoryChartColors.add(cat.getColor());
            }
        }

        List<String> weeklyChartLabels = new ArrayList<>();
        List<Long> weeklyChartData = new ArrayList<>();

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("MMM dd");
        for (int i = 13; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long count = completionRepository.countCompletedByUserAndDate(user, date);

            weeklyChartLabels.add(date.format(dayFormatter));
            weeklyChartData.add(count);
        }

        List<HeatmapCell> heatmapData = new ArrayList<>();
        List<TaskCompletion> allCompletions = completionRepository.findAllCompletedByUser(user);
        Map<LocalDate, Long> completionCountMap = allCompletions.stream()
                .collect(Collectors.groupingBy(TaskCompletion::getCompletionDate, Collectors.counting()));

        for (int i = 59; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long count = completionCountMap.getOrDefault(date, 0L);

            int intensity = 0;
            if (count > 0 && count <= 1) intensity = 1;
            else if (count == 2) intensity = 2;
            else if (count == 3) intensity = 3;
            else if (count >= 4) intensity = 4;

            String tooltip = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ": " + count + " task(s) completed";

            heatmapData.add(HeatmapCell.builder()
                    .date(date)
                    .completedCount(count)
                    .intensity(intensity)
                    .tooltipText(tooltip)
                    .build());
        }

        return DashboardStats.builder()
                .totalTasks(totalTasks)
                .activeTasks(activeTasks)
                .completedTodayCount(completedTodayCount)
                .bestCurrentStreak(bestCurrentStreak)
                .bestLongestStreak(bestLongestStreak)
                .overallCompletionPercentage(overallCompletionPercentage)
                .categoryChartLabels(categoryChartLabels)
                .categoryChartData(categoryChartData)
                .categoryChartColors(categoryChartColors)
                .weeklyChartLabels(weeklyChartLabels)
                .weeklyChartData(weeklyChartData)
                .heatmapData(heatmapData)
                .build();
    }
}
