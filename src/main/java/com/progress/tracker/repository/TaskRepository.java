package com.progress.tracker.repository;

import com.progress.tracker.entity.Task;
import com.progress.tracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph(attributePaths = {"category", "user"})
    List<Task> findByUser(User user);

    @EntityGraph(attributePaths = {"category", "user"})
    List<Task> findByUserAndActive(User user, boolean active);

    @EntityGraph(attributePaths = {"category", "user"})
    Page<Task> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "user"})
    Optional<Task> findByIdAndUser(Long id, User user);

    @EntityGraph(attributePaths = {"category", "user"})
    @Query("SELECT t FROM Task t WHERE t.user = :user " +
            "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
            "AND (:active IS NULL OR t.active = :active) " +
            "AND (:searchPattern IS NULL OR LOWER(t.title) LIKE :searchPattern OR (t.description IS NOT NULL AND LOWER(t.description) LIKE :searchPattern))")
    Page<Task> searchTasks(
            @Param("user") User user,
            @Param("categoryId") Long categoryId,
            @Param("active") Boolean active,
            @Param("searchPattern") String searchPattern,
            Pageable pageable);

    long countByUser(User user);

    long countByUserAndActive(User user, boolean active);

    long countByCategoryId(Long categoryId);

    long countByActiveTrue();

    @EntityGraph(attributePaths = {"category", "user"})
    Page<Task> findAll(Pageable pageable);
}
