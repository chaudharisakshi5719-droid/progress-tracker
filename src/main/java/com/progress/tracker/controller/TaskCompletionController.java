package com.progress.tracker.controller;

import com.progress.tracker.dto.TaskResponse;
import com.progress.tracker.entity.User;
import com.progress.tracker.security.CustomUserDetails;
import com.progress.tracker.service.CompletionService;
import com.progress.tracker.service.TaskService;
import com.progress.tracker.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tasks")
public class TaskCompletionController {

    private final CompletionService completionService;
    private final TaskService taskService;
    private final UserService userService;

    public TaskCompletionController(
            CompletionService completionService,
            TaskService taskService,
            UserService userService) {
        this.completionService = completionService;
        this.taskService = taskService;
        this.userService = userService;
    }

    @PostMapping("/{id}/complete")
    public String completeToday(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        User user = userService.findById(userDetails.getId());
        try {
            completionService.completeToday(id, user);
            TaskResponse taskResponse = taskService.getTaskResponse(id, user);
            String msg;
            if (taskResponse.getCurrentStreak() > 1) {
                msg = "🔥 Great job! Your streak is now " + taskResponse.getCurrentStreak() + " days.";
            } else {
                msg = "Today's completion recorded! Current streak: " + taskResponse.getCurrentStreak() + " day.";
            }
            redirectAttributes.addFlashAttribute("successMessage", msg);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/undo")
    public String undoToday(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        User user = userService.findById(userDetails.getId());
        try {
            completionService.undoTodayCompletion(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "Today's completion has been removed.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/tasks";
    }
}
