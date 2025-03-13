package com.example.harbour_swer.services;

import com.example.harbour_swer.data.dto.ActivityDto;
import com.example.harbour_swer.data.github.Activity;
import com.example.harbour_swer.data.github.Repository;
import com.example.harbour_swer.data.github.ActivityRepository;
import com.example.harbour_swer.data.github.RepositoryRepository;
import com.example.harbour_swer.data.user.User;
import com.example.harbour_swer.data.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final GithubApiService githubApiService;

    @Autowired
    public ActivityService(
            ActivityRepository activityRepository,
            RepositoryRepository repositoryRepository,
            UserRepository userRepository,
            GithubApiService githubApiService) {
        this.activityRepository = activityRepository;
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.githubApiService = githubApiService;
    }

    public Page<ActivityDto> getActivitiesForRepository(Long repositoryId, Activity.ActivityType type, int page, int size) {
        Repository repository = repositoryRepository.findById(repositoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Repository not found with id: " + repositoryId));

        Pageable pageable = PageRequest.of(page, size);
        Page<Activity> activities;

        if (type == null) {
            activities = activityRepository.findByRepositoryOrderByCreatedAtDesc(repository, pageable);
        } else {
            activities = activityRepository.findByRepositoryAndTypeOrderByCreatedAtDesc(repository, type, pageable);
        }

        return activities.map(this::convertToDto);
    }

    public Page<ActivityDto> getActivitiesForUser(Long userId, Activity.ActivityType type, int page, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with id: " + userId));

        Pageable pageable = PageRequest.of(page, size);
        Page<Activity> activities;

        if (type == null) {
            activities = activityRepository.findByUserId(userId, pageable);
        } else {
            activities = activityRepository.findByUserIdAndType(userId, type, pageable);
        }

        return activities.map(this::convertToDto);
    }

    public void fetchAndSaveActivities(Repository repository) {
        LocalDateTime lastUpdated = repository.getLastChecked() != null ?
                repository.getLastChecked() : LocalDateTime.now().minusDays(30);

        List<Activity> newActivities = new ArrayList<>();
        String repoName = repository.getName();

        List<Activity> commits = githubApiService.fetchCommits(repoName, lastUpdated);
        System.out.println(commits);
        for (Activity commit : commits) {
            System.out.println(1);
            System.out.println(commit);
            if (!activityRepository.existsByRepositoryAndTypeAndActivityId(
                    repository, Activity.ActivityType.COMMIT, commit.getActivityId())) {
                commit.setRepository(repository);
                newActivities.add(commit);
            }
        }

        List<Activity> pullRequests = githubApiService.fetchPullRequests(repoName, "all");
        for (Activity pr : pullRequests) {
            if (!activityRepository.existsByRepositoryAndTypeAndActivityId(
                    repository, Activity.ActivityType.PULL_REQUEST, pr.getActivityId())) {
                pr.setRepository(repository);
                newActivities.add(pr);
            }
        }

        List<Activity> issues = githubApiService.fetchIssues(repoName, "all");
        for (Activity issue : issues) {
            if (!activityRepository.existsByRepositoryAndTypeAndActivityId(
                    repository, Activity.ActivityType.ISSUE, issue.getActivityId())) {
                issue.setRepository(repository);
                newActivities.add(issue);
            }
        }

        List<Activity> releases = githubApiService.fetchReleases(repoName);
        for (Activity release : releases) {
            if (!activityRepository.existsByRepositoryAndTypeAndActivityId(
                    repository, Activity.ActivityType.RELEASE, release.getActivityId())) {
                release.setRepository(repository);
                newActivities.add(release);
            }
        }

        if (!newActivities.isEmpty()) {
            activityRepository.saveAll(newActivities);
        }
    }

    private ActivityDto convertToDto(Activity activity) {
        ActivityDto dto = new ActivityDto();
        dto.setId(activity.getId());
        dto.setType(activity.getType());
        dto.setActivityId(activity.getActivityId());
        dto.setTitle(activity.getTitle());
        dto.setDescription(activity.getDescription());
        dto.setAuthor(activity.getAuthor());
        dto.setCreatedAt(activity.getCreatedAt());
        dto.setUpdatedAt(activity.getUpdatedAt());
        dto.setRepositoryId(activity.getRepository().getId());
        return dto;
    }
}