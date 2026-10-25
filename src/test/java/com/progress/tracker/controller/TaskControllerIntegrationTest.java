package com.progress.tracker.controller;

import com.progress.tracker.entity.Category;
import com.progress.tracker.entity.Role;
import com.progress.tracker.entity.Task;
import com.progress.tracker.entity.TargetFrequency;
import com.progress.tracker.entity.User;
import com.progress.tracker.repository.CategoryRepository;
import com.progress.tracker.repository.TaskCompletionRepository;
import com.progress.tracker.repository.TaskRepository;
import com.progress.tracker.repository.UserRepository;
import com.progress.tracker.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskCompletionRepository completionRepository;

    private User testUser;
    private User adminUser;
    private Category testCategory;
    private CustomUserDetails userPrincipal;
    private CustomUserDetails adminPrincipal;

    @BeforeEach
    void setUp() {
        completionRepository.deleteAll();
        taskRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        testUser = userRepository.save(User.builder()
                .username("testuser")
                .email("user@test.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .enabled(true)
                .roles(Set.of(Role.ROLE_USER))
                .build());

        adminUser = userRepository.save(User.builder()
                .username("adminuser")
                .email("admin@test.com")
                .password("password123")
                .firstName("Admin")
                .lastName("User")
                .enabled(true)
                .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                .build());

        testCategory = categoryRepository.save(Category.builder()
                .name("Fitness")
                .description("Physical exercise")
                .color("#dc3545")
                .build());

        userPrincipal = new CustomUserDetails(testUser);
        adminPrincipal = new CustomUserDetails(adminUser);
    }

    @Test
    @DisplayName("Unauthenticated user redirected to login page when accessing dashboard")
    void testUnauthenticatedAccessRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @DisplayName("Authenticated user can view dashboard")
    void testAuthenticatedUserDashboardAccess() throws Exception {
        mockMvc.perform(get("/dashboard").with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/dashboard"))
                .andExpect(model().attributeExists("stats"));
    }

    @Test
    @DisplayName("Authenticated user can view task list")
    void testTaskListAccess() throws Exception {
        mockMvc.perform(get("/tasks").with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/list"))
                .andExpect(model().attributeExists("tasksPage"));
    }

    @Test
    @DisplayName("Task creation succeeds for authenticated user")
    void testTaskCreation() throws Exception {
        mockMvc.perform(post("/tasks/new")
                        .with(user(userPrincipal))
                        .with(csrf())
                        .param("categoryId", testCategory.getId().toString())
                        .param("title", "Daily 5K Run")
                        .param("description", "Run 5 kilometers every morning")
                        .param("startDate", LocalDate.now().toString())
                        .param("endDate", LocalDate.now().plusMonths(1).toString())
                        .param("targetFrequency", "DAILY")
                        .param("active", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }

    @Test
    @DisplayName("Complete task today and undo completion works")
    void testCompleteAndUndoTask() throws Exception {
        Task task = taskRepository.save(Task.builder()
                .user(testUser)
                .category(testCategory)
                .title("Evening Walk")
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now().plusDays(5))
                .targetFrequency(TargetFrequency.DAILY)
                .active(true)
                .build());

        // Complete Today
        mockMvc.perform(post("/tasks/" + task.getId() + "/complete")
                        .with(user(userPrincipal))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        // Undo Today
        mockMvc.perform(post("/tasks/" + task.getId() + "/undo")
                        .with(user(userPrincipal))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }

    @Test
    @DisplayName("Normal user cannot access admin dashboard (403 forbidden)")
    void testNormalUserCannotAccessAdmin() throws Exception {
        mockMvc.perform(get("/admin/dashboard").with(user(userPrincipal)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin user can access admin dashboard")
    void testAdminUserAccessAdminDashboard() throws Exception {
        mockMvc.perform(get("/admin/dashboard").with(user(adminPrincipal)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"));
    }
}
