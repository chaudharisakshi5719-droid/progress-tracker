package com.progress.tracker.service;

import com.progress.tracker.dto.TaskRequest;
import com.progress.tracker.entity.Category;
import com.progress.tracker.entity.Role;
import com.progress.tracker.entity.Task;
import com.progress.tracker.entity.TargetFrequency;
import com.progress.tracker.entity.User;
import com.progress.tracker.exception.InvalidTaskDateException;
import com.progress.tracker.exception.UnauthorizedAccessException;
import com.progress.tracker.repository.CategoryRepository;
import com.progress.tracker.repository.TaskCompletionRepository;
import com.progress.tracker.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TaskCompletionRepository completionRepository;

    @Mock
    private StreakService streakService;

    @InjectMocks
    private TaskService taskService;

    private User owner;
    private User attacker;
    private Category category;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).username("owner").roles(Set.of(Role.ROLE_USER)).build();
        attacker = User.builder().id(2L).username("attacker").roles(Set.of(Role.ROLE_USER)).build();
        category = Category.builder().id(100L).name("Reading").build();
    }

    @Test
    @DisplayName("Invalid task dates (end date before start date) rejected")
    void testCreateTaskInvalidDates() {
        TaskRequest request = TaskRequest.builder()
                .categoryId(100L)
                .title("Read 20 pages")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().minusDays(1)) // invalid!
                .targetFrequency(TargetFrequency.DAILY)
                .build();

        assertThrows(InvalidTaskDateException.class, () -> taskService.createTask(request, owner));
    }

    @Test
    @DisplayName("Unauthorized task update throws UnauthorizedAccessException")
    void testUnauthorizedTaskUpdate() {
        Task task = Task.builder()
                .id(50L)
                .user(owner)
                .category(category)
                .title("Read 20 pages")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .build();

        when(taskRepository.findById(50L)).thenReturn(Optional.of(task));

        TaskRequest updateRequest = TaskRequest.builder()
                .categoryId(100L)
                .title("Read 50 pages")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .build();

        assertThrows(UnauthorizedAccessException.class, () -> taskService.updateTask(50L, updateRequest, attacker));
    }
}
