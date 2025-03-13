package com.example.harbour_swer.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class RepositoryDto {
    private Long id;

    @NotBlank(message = "Repository name is required")
    @Size(min = 1, max = 100, message = "Repository name must be between 1 and 100 characters")
    private String name;

    private LocalDateTime lastChecked;
}
