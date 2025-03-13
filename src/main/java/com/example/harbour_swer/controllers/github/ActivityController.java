package com.example.harbour_swer.controllers.github;

import com.example.harbour_swer.data.dto.ActivityDto;
import com.example.harbour_swer.data.github.Activity;
import com.example.harbour_swer.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    private final ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/repositories/{repositoryId}")
    public ResponseEntity<Page<ActivityDto>> getRepositoryActivities(
            @PathVariable Long repositoryId,
            @RequestParam(required = false) Activity.ActivityType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ActivityDto> activities = activityService.getActivitiesForRepository(repositoryId, type, page, size);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Page<ActivityDto>> getUserActivities(
            @PathVariable Long userId,
            @RequestParam(required = false) Activity.ActivityType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ActivityDto> activities = activityService.getActivitiesForUser(userId, type, page, size);
        return ResponseEntity.ok(activities);
    }
}
