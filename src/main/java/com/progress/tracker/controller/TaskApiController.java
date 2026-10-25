package com.progress.tracker.controller;

import com.progress.tracker.dto.TaskProgressStats;
import com.progress.tracker.dto.TaskRequest;
import com.progress.tracker.dto.TaskResponse;
import com.progress.tracker.entity.Task;
import com.progress.tracker.entity.User;
import com.progress.tracker.security.CustomUserDetails;
import com.progress.tracker.service.CompletionService;
import com.progress.tracker.service.StreakService;
import com.progress.tracker.service.TaskService;
import com.progress.tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/tasks")
public class TaskApiController {

    private final TaskService taskService;
    private final CompletionService completionService;
    private final StreakService streakService;
    private final UserService userService;

    public TaskApiController(
            TaskService taskService, CompletionService completionService,
            StreakService streakService, UserService userService) {
        this.taskService = taskService;
        this.completionService = completionService;
        this.streakService = streakService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        User user = userService.findById(userDetails.getId());
        Page<TaskResponse> tasks = taskService.searchUserTasks(user, categoryId, active, search, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        User user = userService.findById(userDetails.getId());
        TaskResponse task = taskService.getTaskResponse(id, user);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TaskRequest request) {
        User user = userService.findById(userDetails.getId());
        TaskResponse created = taskService.createTask(request, user);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        User user = userService.findById(userDetails.getId());
        completionService.completeToday(id, user);
        TaskResponse updated = taskService.getTaskResponse(id, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/completion/today")
    public ResponseEntity<Void> undoCompletion(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        User user = userService.findById(userDetails.getId());
        completionService.undoTodayCompletion(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<TaskProgressStats> getTaskStats(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        User user = userService.findById(userDetails.getId());
        Task task = taskService.getTaskEntity(id, user);
        TaskProgressStats stats = streakService.calculateProgressStats(task, LocalDate.now());
        return ResponseEntity.ok(stats);
    }
}
