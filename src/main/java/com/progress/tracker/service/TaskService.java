package com.progress.tracker.service;

import com.progress.tracker.dto.DailyCompletionStatus;
import com.progress.tracker.dto.TaskProgressStats;
import com.progress.tracker.dto.TaskRequest;
import com.progress.tracker.dto.TaskResponse;
import com.progress.tracker.entity.Category;
import com.progress.tracker.entity.Task;
import com.progress.tracker.entity.User;
import com.progress.tracker.exception.InvalidTaskDateException;
import com.progress.tracker.exception.ResourceNotFoundException;
import com.progress.tracker.exception.UnauthorizedAccessException;
import com.progress.tracker.repository.CategoryRepository;
import com.progress.tracker.repository.TaskCompletionRepository;
import com.progress.tracker.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TaskCompletionRepository completionRepository;
    private final StreakService streakService;

    public TaskService(TaskRepository taskRepository,
                       CategoryRepository categoryRepository,
                       TaskCompletionRepository completionRepository,
                       StreakService streakService) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.completionRepository = completionRepository;
        this.streakService = streakService;
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request, User user) {
        validateTaskDates(request.getStartDate(), request.getEndDate());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Task task = Task.builder()
                .user(user)
                .category(category)
                .title(request.getTitle().trim())
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .targetFrequency(request.getTargetFrequency())
                .active(request.isActive())
                .build();

        Task savedTask = taskRepository.save(task);
        return mapToTaskResponse(savedTask, LocalDate.now());
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request, User user) {
        validateTaskDates(request.getStartDate(), request.getEndDate());

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        verifyTaskOwnership(task, user);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        task.setCategory(category);
        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        task.setStartDate(request.getStartDate());
        task.setEndDate(request.getEndDate());
        task.setTargetFrequency(request.getTargetFrequency());
        task.setActive(request.isActive());

        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask, LocalDate.now());
    }

    @Transactional
    public void deleteTask(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        verifyTaskOwnership(task, user);

        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public Task getTaskEntity(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        verifyTaskOwnership(task, user);
        return task;
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskResponse(Long id, User user) {
        Task task = getTaskEntity(id, user);
        return mapToTaskResponse(task, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> searchUserTasks(User user, Long categoryId, Boolean active, String search, Pageable pageable) {
        String searchPattern = null;
        if (search != null && !search.trim().isEmpty()) {
            searchPattern = "%" + search.trim().toLowerCase() + "%";
        }
        Page<Task> tasks = taskRepository.searchTasks(user, categoryId, active, searchPattern, pageable);
        LocalDate today = LocalDate.now();
        return tasks.map(task -> mapToTaskResponse(task, today));
    }

    @Transactional(readOnly = true)
    public List<DailyCompletionStatus> getTaskMonthlyCalendar(Task task, int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        LocalDate today = LocalDate.now();

        Set<LocalDate> completedDates = completionRepository.findByTaskAndCompletedTrueOrderByCompletionDateAsc(task).stream()
                .map(tc -> tc.getCompletionDate())
                .collect(Collectors.toSet());

        List<DailyCompletionStatus> days = new ArrayList<>();
        LocalDate curr = startOfMonth;
        while (!curr.isAfter(endOfMonth)) {
            String status;
            String badgeClass;
            boolean completed = completedDates.contains(curr);

            if (curr.isBefore(task.getStartDate())) {
                status = "NOT_STARTED";
                badgeClass = "bg-light text-muted border";
            } else if (curr.isAfter(task.getEndDate()) || curr.isAfter(today)) {
                status = "FUTURE";
                badgeClass = "bg-secondary text-white";
            } else if (completed) {
                status = "COMPLETED";
                badgeClass = "bg-success text-white";
            } else {
                status = "MISSED";
                badgeClass = "bg-danger text-white";
            }

            days.add(DailyCompletionStatus.builder()
                    .date(curr)
                    .status(status)
                    .completed(completed)
                    .statusBadgeClass(badgeClass)
                    .build());

            curr = curr.plusDays(1);
        }

        return days;
    }

    public TaskResponse mapToTaskResponse(Task task, LocalDate referenceDate) {
        TaskProgressStats stats = streakService.calculateProgressStats(task, referenceDate);

        return TaskResponse.builder()
                .id(task.getId())
                .categoryId(task.getCategory().getId())
                .categoryName(task.getCategory().getName())
                .categoryColor(task.getCategory().getColor())
                .title(task.getTitle())
                .description(task.getDescription())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .targetFrequency(task.getTargetFrequency())
                .active(task.isActive())
                .completedToday(stats.isCompletedToday())
                .currentStreak(stats.getCurrentStreak())
                .longestStreak(stats.getLongestStreak())
                .completionPercentage(stats.getCompletionPercentage())
                .totalPossibleDays(stats.getTotalPossibleDays())
                .completedDays(stats.getCompletedDays())
                .missedDays(stats.getMissedDays())
                .remainingDays(stats.getRemainingDays())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    private void validateTaskDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidTaskDateException("Start date and end date are required");
        }
        if (endDate.isBefore(startDate)) {
            throw new InvalidTaskDateException("End date (" + endDate + ") cannot be before start date (" + startDate + ")");
        }
    }

    private void verifyTaskOwnership(Task task, User user) {
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.name().equals("ROLE_ADMIN"));
        if (!task.getUser().getId().equals(user.getId()) && !isAdmin) {
            throw new UnauthorizedAccessException("You are not authorized to access task #" + task.getId());
        }
    }
}
