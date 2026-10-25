package com.progress.tracker.controller;

import com.progress.tracker.dto.CategoryDto;
import com.progress.tracker.dto.UserAdminDto;
import com.progress.tracker.entity.Task;
import com.progress.tracker.entity.User;
import com.progress.tracker.repository.TaskCompletionRepository;
import com.progress.tracker.repository.TaskRepository;
import com.progress.tracker.repository.UserRepository;
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

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final TaskService taskService;
    private final CategoryService categoryService;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskCompletionRepository completionRepository;

    public AdminController(
            UserService userService, TaskService taskService,
            CategoryService categoryService, UserRepository userRepository,
            TaskRepository taskRepository, TaskCompletionRepository completionRepository) {
        this.userService = userService;
        this.taskService = taskService;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.completionRepository = completionRepository;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByEnabledTrue();
        long totalTasks = taskRepository.count();
        long activeTasks = taskRepository.countByActiveTrue();
        long totalCompletions = completionRepository.count();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("activeTasks", activeTasks);
        model.addAttribute("totalCompletions", totalCompletions);
        model.addAttribute("categories", categoryService.getAllCategories());

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<UserAdminDto> userPage = userService.getAllUsersForAdmin(pageable);
        model.addAttribute("userPage", userPage);
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        User adminUser = userService.findById(adminDetails.getId());
        try {
            userService.toggleUserStatus(id, adminUser);
            redirectAttributes.addFlashAttribute("successMessage", "User status updated successfully");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/tasks")
    public String listAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Task> taskPage = taskRepository.findAll(pageable);
        model.addAttribute("taskPage", taskPage);
        return "admin/tasks";
    }

    @PostMapping("/tasks/{id}/delete")
    public String deleteInappropriateTask(
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        User adminUser = userService.findById(adminDetails.getId());
        taskService.deleteTask(id, adminUser);
        redirectAttributes.addFlashAttribute("successMessage", "Task #" + id + " has been deleted by Admin.");
        return "redirect:/admin/tasks";
    }

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("categoryDto", new CategoryDto());
        return "admin/categories";
    }

    @PostMapping("/categories/new")
    public String createCategory(
            @Valid @ModelAttribute("categoryDto") CategoryDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/categories";
        }
        try {
            categoryService.createCategory(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Category created successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/edit")
    public String editCategory(
            @PathVariable Long id,
            @Valid @ModelAttribute("categoryDto") CategoryDto dto,
            RedirectAttributes redirectAttributes) {
        try {
            categoryService.updateCategory(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/categories";
    }
}
