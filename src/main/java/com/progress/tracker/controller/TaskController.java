package com.progress.tracker.controller;

import com.progress.tracker.dto.DailyCompletionStatus;
import com.progress.tracker.dto.TaskRequest;
import com.progress.tracker.dto.TaskResponse;
import com.progress.tracker.entity.Task;
import com.progress.tracker.entity.User;
import com.progress.tracker.security.CustomUserDetails;
import com.progress.tracker.service.CategoryService;
import com.progress.tracker.service.TaskService;
import com.progress.tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final CategoryService categoryService;
    private final UserService userService;

    public TaskController(TaskService taskService, CategoryService categoryService, UserService userService) {
        this.taskService = taskService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping
    public String listTasks(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir,
            Model model) {
        User user = userService.findById(userDetails.getId());
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TaskResponse> taskPage = taskService.searchUserTasks(user, categoryId, active, search, pageable);
        model.addAttribute("tasksPage", taskPage);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedActive", active);
        model.addAttribute("searchQuery", search);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        return "tasks/list";
    }

    @GetMapping("/new")
    public String newTaskForm(Model model) {
        TaskRequest request = TaskRequest.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .active(true)
                .build();
        model.addAttribute("taskRequest", request);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("pageTitle", "Create New Task");
        return "tasks/form";
    }

    @PostMapping("/new")
    public String createTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute("taskRequest") TaskRequest taskRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        User user = userService.findById(userDetails.getId());
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("pageTitle", "Create New Task");
            return "tasks/form";
        }
        try {
            taskService.createTask(taskRequest, user);
            redirectAttributes.addFlashAttribute("successMessage", "Task created successfully!");
            return "redirect:/tasks";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("pageTitle", "Create New Task");
            return "tasks/form";
        }
    }

    @GetMapping("/{id}")
    public String viewTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {
        User user = userService.findById(userDetails.getId());
        Task task = taskService.getTaskEntity(id, user);
        TaskResponse response = taskService.getTaskResponse(id, user);
        LocalDate now = LocalDate.now();
        int calYear = (year != null) ? year : now.getYear();
        int calMonth = (month != null) ? month : now.getMonthValue();
        List<DailyCompletionStatus> calendarDays = taskService.getTaskMonthlyCalendar(task, calYear, calMonth);
        model.addAttribute("task", response);
        model.addAttribute("calendarDays", calendarDays);
        model.addAttribute("currentYear", calYear);
        model.addAttribute("currentMonth", calMonth);
        return "tasks/detail";
    }

    @GetMapping("/{id}/edit")
    public String editTaskForm(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            Model model) {
        User user = userService.findById(userDetails.getId());
        Task task = taskService.getTaskEntity(id, user);
        TaskRequest request = TaskRequest.builder()
                .id(task.getId())
                .categoryId(task.getCategory().getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .targetFrequency(task.getTargetFrequency())
                .active(task.isActive())
                .build();
        model.addAttribute("taskRequest", request);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("pageTitle", "Edit Task #" + id);
        return "tasks/form";
    }

    @PostMapping("/{id}/edit")
    public String updateTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @ModelAttribute("taskRequest") TaskRequest taskRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        User user = userService.findById(userDetails.getId());
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("pageTitle", "Edit Task #" + id);
            return "tasks/form";
        }
        try {
            taskService.updateTask(id, taskRequest, user);
            redirectAttributes.addFlashAttribute("successMessage", "Task updated successfully!");
            return "redirect:/tasks/" + id;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("pageTitle", "Edit Task #" + id);
            return "tasks/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        User user = userService.findById(userDetails.getId());
        taskService.deleteTask(id, user);
        redirectAttributes.addFlashAttribute("successMessage", "Task deleted successfully!");
        return "redirect:/tasks";
    }
}
