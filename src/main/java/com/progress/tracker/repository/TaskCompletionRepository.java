package com.progress.tracker.repository;

import com.progress.tracker.entity.Task;
import com.progress.tracker.entity.TaskCompletion;
import com.progress.tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskCompletionRepository extends JpaRepository<TaskCompletion, Long> {

    List<TaskCompletion> findByTaskOrderByCompletionDateAsc(Task task);

    List<TaskCompletion> findByTaskAndCompletedTrueOrderByCompletionDateAsc(Task task);

    Optional<TaskCompletion> findByTaskAndCompletionDate(Task task, LocalDate completionDate);

    boolean existsByTaskAndCompletionDateAndCompletedTrue(Task task, LocalDate completionDate);

    void deleteByTaskAndCompletionDate(Task task, LocalDate completionDate);

    @Query("SELECT tc FROM TaskCompletion tc JOIN tc.task t WHERE t.user = :user AND tc.completionDate = :date AND tc.completed = true")
    List<TaskCompletion> findCompletedByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);

    @Query("SELECT tc FROM TaskCompletion tc JOIN tc.task t WHERE t.user = :user AND tc.completed = true ORDER BY tc.completionDate ASC")
    List<TaskCompletion> findAllCompletedByUser(@Param("user") User user);

    @Query("SELECT COUNT(tc) FROM TaskCompletion tc JOIN tc.task t WHERE t.user = :user AND tc.completionDate = :date AND tc.completed = true")
    long countCompletedByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);

    @Query("SELECT COUNT(tc) FROM TaskCompletion tc JOIN tc.task t WHERE t.user = :user AND tc.completed = true")
    long countTotalCompletedByUser(@Param("user") User user);
}
