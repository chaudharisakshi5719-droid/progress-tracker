package com.progress.tracker.service;

import com.progress.tracker.entity.Category;
import com.progress.tracker.entity.Role;
import com.progress.tracker.entity.Task;
import com.progress.tracker.entity.TaskCompletion;
import com.progress.tracker.entity.TargetFrequency;
import com.progress.tracker.entity.User;
import com.progress.tracker.exception.DuplicateCompletionException;
import com.progress.tracker.exception.InvalidTaskDateException;
import com.progress.tracker.exception.UnauthorizedAccessException;
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
class CompletionServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskCompletionRepository completionRepository;

    @Mock
    private StreakService streakService;

    @InjectMocks
    private CompletionService completionService;

    private User owner;
    private User attacker;
    private Task task;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).username("owner").roles(Set.of(Role.ROLE_USER)).build();
        attacker = User.builder().id(2L).username("attacker").roles(Set.of(Role.ROLE_USER)).build();
        Category category = Category.builder().id(1L).name("Fitness").build();

        task = Task.builder()
                .id(10L)
                .user(owner)
                .category(category)
                .title("Morning Exercise")
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now().plusDays(5))
                .targetFrequency(TargetFrequency.DAILY)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Complete task today successfully")
    void testCompleteTodaySuccess() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(completionRepository.findByTaskAndCompletionDate(eq(task), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(completionRepository.save(any(TaskCompletion.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TaskCompletion completion = completionService.completeToday(10L, owner);

        assertNotNull(completion);
        assertTrue(completion.isCompleted());
        assertEquals(LocalDate.now(), completion.getCompletionDate());
    }

    @Test
    @DisplayName("Test 6: Duplicate completion for same task/date rejected")
    void testDuplicateCompletionRejected() {
        TaskCompletion existing = TaskCompletion.builder()
                .task(task)
                .completionDate(LocalDate.now())
                .completed(true)
                .build();

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(completionRepository.findByTaskAndCompletionDate(eq(task), any(LocalDate.class)))
                .thenReturn(Optional.of(existing));

        assertThrows(DuplicateCompletionException.class, () -> completionService.completeToday(10L, owner));
    }

    @Test
    @DisplayName("Future date completion rejected")
    void testFutureCompletionRejected() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        LocalDate futureDate = LocalDate.now().plusDays(1);
        assertThrows(InvalidTaskDateException.class, () -> 
                completionService.completeTaskForDate(10L, owner, futureDate));
    }

    @Test
    @DisplayName("Unauthorized completion attempt throws exception (IDOR protection)")
    void testUnauthorizedCompletionAttempt() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThrows(UnauthorizedAccessException.class, () -> completionService.completeToday(10L, attacker));
    }

    @Test
    @DisplayName("Undo today completion calls delete")
    void testUndoTodayCompletion() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        completionService.undoTodayCompletion(10L, owner);

        verify(completionRepository, times(1)).deleteByTaskAndCompletionDate(eq(task), eq(LocalDate.now()));
    }
}
