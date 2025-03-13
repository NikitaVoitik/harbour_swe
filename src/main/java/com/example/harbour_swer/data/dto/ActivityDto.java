package com.example.harbour_swer.data.dto;

import com.example.harbour_swer.data.github.Activity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityDto {
    private Long id;

    @NotNull(message = "Activity type is required")
    private Activity.ActivityType type;

    @NotBlank(message = "Activity ID is required")
    private String activityId;

    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private Long repositoryId;

    private String author;

    @NotNull(message = "Creation date is required")
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
