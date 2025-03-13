package com.example.harbour_swer.controllers.github;

import com.example.harbour_swer.data.dto.RepositoryDto;
import com.example.harbour_swer.services.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repositories")
public class RepositoryController {
    private final RepositoryService repositoryService;

    @Autowired
    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @PostMapping("/{userId}/track")
    public ResponseEntity<RepositoryDto> trackRepository(
            @PathVariable Long userId,
            @RequestParam String name) {
        RepositoryDto repository = repositoryService.trackRepository(userId, name);
        return new ResponseEntity<>(repository, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{userId}", produces = "application/json")
    public ResponseEntity<List<RepositoryDto>> getUserRepositories(@PathVariable Long userId) {
        List<RepositoryDto> repositories = repositoryService.getUserRepositories(userId);
        return ResponseEntity.ok(repositories);
    }
}
