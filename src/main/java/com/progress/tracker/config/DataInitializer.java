package com.progress.tracker.config;

import com.progress.tracker.entity.Category;
import com.progress.tracker.entity.Role;
import com.progress.tracker.entity.TargetFrequency;
import com.progress.tracker.entity.Task;
import com.progress.tracker.entity.TaskCompletion;
import com.progress.tracker.entity.User;
import com.progress.tracker.repository.CategoryRepository;
import com.progress.tracker.repository.TaskCompletionRepository;
import com.progress.tracker.repository.TaskRepository;
import com.progress.tracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final TaskCompletionRepository completionRepository;
    private final PasswordEncoder passwordEncoder;
    private final DefaultUsersProperties properties;

    public DataInitializer(
            UserRepository userRepository, CategoryRepository categoryRepository,
            TaskRepository taskRepository, TaskCompletionRepository completionRepository,
            PasswordEncoder passwordEncoder, DefaultUsersProperties properties) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
        this.completionRepository = completionRepository;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    @Transactional
    public void run(String... args) {
        DefaultUsersProperties.DefaultUser adminConfig = (properties != null) ? properties.admin() : null;
        if (adminConfig == null) {
            adminConfig = new DefaultUsersProperties.DefaultUser(
                    "admin", "admin@progresstracker.com", "admin123", "System", "Admin", true
            );
        }
        final DefaultUsersProperties.DefaultUser finalAdminConfig = adminConfig;
        User admin = userRepository.findByUsername(finalAdminConfig.username())
                .orElseGet(() -> userRepository.save(User.builder()
                        .username(finalAdminConfig.username())
                        .email(finalAdminConfig.email())
                        .password(passwordEncoder.encode(finalAdminConfig.password()))
                        .firstName(finalAdminConfig.firstName())
                        .lastName(finalAdminConfig.lastName())
                        .enabled(finalAdminConfig.enabled())
                        .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                        .build()
                ));

        DefaultUsersProperties.DefaultUser demoConfig = (properties != null) ? properties.user() : null;
        if (demoConfig == null) {
            demoConfig = new DefaultUsersProperties.DefaultUser(
                    "user", "user@progresstracker.com", "user123", "Demo", "User", true
            );
        }
        final DefaultUsersProperties.DefaultUser finalDemoConfig = demoConfig;
        User user = userRepository.findByUsername(finalDemoConfig.username())
                .orElseGet(() -> userRepository.save(User.builder()
                        .username(finalDemoConfig.username())
                        .email(finalDemoConfig.email())
                        .password(passwordEncoder.encode(finalDemoConfig.password()))
                        .firstName(finalDemoConfig.firstName())
                        .lastName(finalDemoConfig.lastName())
                        .enabled(finalDemoConfig.enabled())
                        .roles(Set.of(Role.ROLE_USER))
                        .build()
                ));
        ensureCategoriesExist();
        if (taskRepository.countByUser(user) == 0) seedSampleTasks(user);
    }

    private void ensureCategoriesExist() {
        createCategoryIfMissing("Fitness", "Physical health, workouts, and exercise", "#dc3545");
        createCategoryIfMissing("Study", "Learning, courses, and educational goals", "#0d6efd");
        createCategoryIfMissing("Work", "Career tasks and professional projects", "#ffc107");
        createCategoryIfMissing("Health", "Wellness, nutrition, and mental health", "#198754");
        createCategoryIfMissing("Personal", "Habits, personal growth, and daily routines", "#6f42c1");
        createCategoryIfMissing("Reading", "Books, articles, and documentation", "#fd7e14");
        createCategoryIfMissing("Coding", "Software development and practice problems", "#20c997");
        createCategoryIfMissing("Finance", "Budgeting, saving, and financial planning", "#0dcaf0");
    }

    private void seedSampleTasks(User user) {
        Category fitness = categoryRepository.findByNameIgnoreCase("Fitness").orElseThrow();
        Category coding = categoryRepository.findByNameIgnoreCase("Coding").orElseThrow();

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(20);
        LocalDate endDate = today.plusDays(30);
        Task task1 = taskRepository.save(Task.builder()
                .user(user)
                .category(fitness)
                .title("Exercise")
                .description("30-minute cardio and strength workout daily")
                .startDate(startDate)
                .endDate(endDate)
                .targetFrequency(TargetFrequency.DAILY)
                .active(true)
                .build());
        Task task2 = taskRepository.save(Task.builder()
                .user(user)
                .category(coding)
                .title("Java Practice")
                .description("Complete one Spring Boot exercise or LeetCode problem")
                .startDate(startDate)
                .endDate(endDate)
                .targetFrequency(TargetFrequency.DAILY)
                .active(true)
                .build());

        for (int i = 0; i <= 10; i++) {
            createCompletion(task1, today.minusDays(i));
        }

        for (int i = 0; i <= 7; i++) {
            createCompletion(task2, today.minusDays(i));
        }

        createCompletion(task2, today.minusDays(10));
        createCompletion(task2, today.minusDays(11));
    }

    private void createCategoryIfMissing(String name, String description, String color) {
        if (!categoryRepository.existsByNameIgnoreCase(name)) {
            categoryRepository.save(Category.builder()
                    .name(name)
                    .description(description)
                    .color(color)
                    .build());
        }
    }

    private void createCompletion(Task task, LocalDate date) {
        if (!completionRepository.existsByTaskAndCompletionDateAndCompletedTrue(task, date)) {
            completionRepository.save(TaskCompletion.builder()
                    .task(task)
                    .completionDate(date)
                    .completed(true)
                    .completedAt(Instant.now())
                    .build());
        }
    }
}
