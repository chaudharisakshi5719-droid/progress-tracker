package com.progress.tracker.service;

import com.progress.tracker.entity.Task;
import com.progress.tracker.entity.TaskCompletion;
import com.progress.tracker.entity.User;
import com.progress.tracker.exception.DuplicateCompletionException;
import com.progress.tracker.exception.InvalidTaskDateException;
import com.progress.tracker.exception.ResourceNotFoundException;
import com.progress.tracker.exception.UnauthorizedAccessException;
import com.progress.tracker.repository.TaskCompletionRepository;
import com.progress.tracker.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class CompletionService {

    private final TaskRepository taskRepository;
    private final TaskCompletionRepository completionRepository;
    private final StreakService streakService;

    public CompletionService(TaskRepository taskRepository,
                             TaskCompletionRepository completionRepository,
                             StreakService streakService) {
        this.taskRepository = taskRepository;
        this.completionRepository = completionRepository;
        this.streakService = streakService;
    }

    @Transactional
    public TaskCompletion completeTaskForDate(Long taskId, User user, LocalDate date) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        verifyTaskOwnership(task, user);

        if (!task.isActive()) {
            throw new InvalidTaskDateException("Cannot complete an inactive task");
        }

        if (date.isAfter(LocalDate.now())) {
            throw new InvalidTaskDateException("Future dates cannot be completed");
        }

        if (date.isBefore(task.getStartDate())) {
            throw new InvalidTaskDateException("Completion date cannot be before task start date (" + task.getStartDate() + ")");
        }

        if (date.isAfter(task.getEndDate())) {
            throw new InvalidTaskDateException("Completion date cannot be after task end date (" + task.getEndDate() + ")");
        }

        Optional<TaskCompletion> existing = completionRepository.findByTaskAndCompletionDate(task, date);
        if (existing.isPresent()) {
            if (existing.get().isCompleted()) {
                throw new DuplicateCompletionException("Task is already completed for date: " + date);
            } else {
                TaskCompletion completion = existing.get();
                completion.setCompleted(true);
                completion.setCompletedAt(Instant.now());
                return completionRepository.save(completion);
            }
        }

        TaskCompletion newCompletion = TaskCompletion.builder()
                .task(task)
                .completionDate(date)
                .completed(true)
                .completedAt(Instant.now())
                .build();

        return completionRepository.save(newCompletion);
    }

    @Transactional
    public TaskCompletion completeToday(Long taskId, User user) {
        return completeTaskForDate(taskId, user, LocalDate.now());
    }

    @Transactional
    public void undoTodayCompletion(Long taskId, User user) {
        undoTaskCompletionForDate(taskId, user, LocalDate.now());
    }

    @Transactional
    public void undoTaskCompletionForDate(Long taskId, User user, LocalDate date) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        verifyTaskOwnership(task, user);

        completionRepository.deleteByTaskAndCompletionDate(task, date);
    }

    public boolean isCompletedOnDate(Task task, LocalDate date) {
        return completionRepository.existsByTaskAndCompletionDateAndCompletedTrue(task, date);
    }

    private void verifyTaskOwnership(Task task, User user) {
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.name().equals("ROLE_ADMIN"));
        if (!task.getUser().getId().equals(user.getId()) && !isAdmin) {
            throw new UnauthorizedAccessException("You are not authorized to modify completions for this task");
        }
    }
}
