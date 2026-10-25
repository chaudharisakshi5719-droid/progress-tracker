package com.progress.tracker.service;

import com.progress.tracker.dto.StreakStats;
import com.progress.tracker.dto.TaskProgressStats;
import com.progress.tracker.entity.Category;
import com.progress.tracker.entity.Task;
import com.progress.tracker.entity.TaskCompletion;
import com.progress.tracker.entity.TargetFrequency;
import com.progress.tracker.entity.User;
import com.progress.tracker.repository.TaskCompletionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StreakServiceTest {

    @Mock
    private TaskCompletionRepository completionRepository;

    @InjectMocks
    private StreakServiceImpl streakService;

    private Task sampleTask;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.of(2026, 10, 5);
        User user = User.builder().id(1L).username("testuser").build();
        Category category = Category.builder().id(1L).name("Coding").build();

        sampleTask = Task.builder()
                .id(100L)
                .user(user)
                .category(category)
                .title("Daily Java Practice")
                .startDate(today.minusDays(10))
                .endDate(today.plusDays(10))
                .targetFrequency(TargetFrequency.DAILY)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Test 1: Mon, Tue, Wed completed consecutive ending today -> Current streak = 3")
    void testCurrentStreakConsecutiveEndingToday() {
        LocalDate mon = today.minusDays(2);
        LocalDate tue = today.minusDays(1);
        LocalDate wed = today;

        List<TaskCompletion> completions = List.of(
                createCompletion(mon),
                createCompletion(tue),
                createCompletion(wed)
        );

        when(completionRepository.findByTaskAndCompletedTrueOrderByCompletionDateAsc(sampleTask))
                .thenReturn(completions);

        StreakStats stats = streakService.calculateStreak(sampleTask, wed);

        assertEquals(3, stats.getCurrentStreak());
        assertEquals(3, stats.getLongestStreak());
        assertEquals("Last completed: Today", stats.getStreakStatusLabel());
    }

    @Test
    @DisplayName("Test 2: Mon, Tue completed, Wed missed, Thu completed -> Current streak = 1")
    void testCurrentStreakBroken() {
        LocalDate mon = today.minusDays(3);
        LocalDate tue = today.minusDays(2);
        LocalDate thu = today;

        List<TaskCompletion> completions = List.of(
                createCompletion(mon),
                createCompletion(tue),
                createCompletion(thu)
        );

        when(completionRepository.findByTaskAndCompletedTrueOrderByCompletionDateAsc(sampleTask))
                .thenReturn(completions);

        StreakStats stats = streakService.calculateStreak(sampleTask, thu);

        assertEquals(1, stats.getCurrentStreak());
        assertEquals(2, stats.getLongestStreak());
        assertEquals("Last completed: Today", stats.getStreakStatusLabel());
    }

    @Test
    @DisplayName("Test 3: Mon, Tue, Wed, Thu completed, Fri, Sat missed -> Longest streak = 4")
    void testLongestStreakCalculation() {
        LocalDate mon = today.minusDays(5);
        LocalDate tue = today.minusDays(4);
        LocalDate wed = today.minusDays(3);
        LocalDate thu = today.minusDays(2);

        List<TaskCompletion> completions = List.of(
                createCompletion(mon),
                createCompletion(tue),
                createCompletion(wed),
                createCompletion(thu)
        );

        when(completionRepository.findByTaskAndCompletedTrueOrderByCompletionDateAsc(sampleTask))
                .thenReturn(completions);

        StreakStats stats = streakService.calculateStreak(sampleTask, today);

        assertEquals(0, stats.getCurrentStreak());
        assertEquals(4, stats.getLongestStreak());
    }

    @Test
    @DisplayName("Test 4: No completions -> Current streak = 0, Longest streak = 0")
    void testNoCompletions() {
        when(completionRepository.findByTaskAndCompletedTrueOrderByCompletionDateAsc(sampleTask))
                .thenReturn(List.of());

        StreakStats stats = streakService.calculateStreak(sampleTask, today);

        assertEquals(0, stats.getCurrentStreak());
        assertEquals(0, stats.getLongestStreak());
        assertNull(stats.getLastCompletedDate());
        assertEquals("No completions yet", stats.getStreakStatusLabel());
    }

    @Test
    @DisplayName("Test 5: Future completions should never be counted")
    void testFutureCompletionsIgnored() {
        LocalDate todayDate = today;
        LocalDate futureDate = today.plusDays(2);

        List<TaskCompletion> completions = List.of(
                createCompletion(todayDate),
                createCompletion(futureDate)
        );

        when(completionRepository.findByTaskAndCompletedTrueOrderByCompletionDateAsc(sampleTask))
                .thenReturn(completions);

        StreakStats stats = streakService.calculateStreak(sampleTask, todayDate);

        assertEquals(1, stats.getCurrentStreak());
        assertEquals(1, stats.getLongestStreak());
    }

    @Test
    @DisplayName("Test Task progress stats calculation")
    void testCalculateProgressStats() {
        LocalDate start = today.minusDays(9);
        sampleTask.setStartDate(start);

        List<TaskCompletion> completions = List.of(
                createCompletion(today.minusDays(4)),
                createCompletion(today.minusDays(3)),
                createCompletion(today.minusDays(2)),
                createCompletion(today.minusDays(1)),
                createCompletion(today)
        );

        when(completionRepository.findByTaskAndCompletedTrueOrderByCompletionDateAsc(sampleTask))
                .thenReturn(completions);

        TaskProgressStats progress = streakService.calculateProgressStats(sampleTask, today);

        assertEquals(10, progress.getTotalPossibleDays());
        assertEquals(5, progress.getCompletedDays());
        assertEquals(5, progress.getMissedDays());
        assertEquals(50.0, progress.getCompletionPercentage());
        assertEquals(5, progress.getCurrentStreak());
        assertTrue(progress.isCompletedToday());
    }

    private TaskCompletion createCompletion(LocalDate date) {
        return TaskCompletion.builder()
                .task(sampleTask)
                .completionDate(date)
                .completed(true)
                .build();
    }
}
