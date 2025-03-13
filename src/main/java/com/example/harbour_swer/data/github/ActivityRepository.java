package com.example.harbour_swer.data.github;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Page<Activity> findByRepositoryOrderByCreatedAtDesc(Repository repository, Pageable pageable);

    List<Activity> findByRepositoryAndTypeAndCreatedAtAfter(Repository repository, Activity.ActivityType type, LocalDateTime since);

    boolean existsByRepositoryAndTypeAndActivityId(Repository repository, Activity.ActivityType type, String activityId);

    @Query("SELECT a FROM Activity a WHERE a.repository.user.id = :userId ORDER BY a.createdAt DESC")
    Page<Activity> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.repository.user.id = :userId AND a.type = :type ORDER BY a.createdAt DESC")
    Page<Activity> findByUserIdAndType(Long userId, Activity.ActivityType type, Pageable pageable);
}
