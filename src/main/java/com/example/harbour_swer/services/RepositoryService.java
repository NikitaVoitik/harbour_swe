package com.example.harbour_swer.services;

import com.example.harbour_swer.data.dto.RepositoryDto;
import com.example.harbour_swer.data.github.Repository;
import com.example.harbour_swer.data.user.User;
import com.example.harbour_swer.data.github.RepositoryRepository;
import com.example.harbour_swer.data.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepositoryService {
    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final GithubApiService githubApiService;

    @Autowired
    public RepositoryService(
            RepositoryRepository repositoryRepository,
            UserRepository userRepository,
            GithubApiService githubApiService) {
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.githubApiService = githubApiService;
    }

    public RepositoryDto trackRepository(Long userId, String repoFullName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId));

        boolean alreadyTracked = user.getTrackedRepositories().stream()
                .anyMatch(repo -> repo.getName().equals(repoFullName));

        if (alreadyTracked) {
            throw new IllegalArgumentException("Repository already tracked");
        }

        Repository repoInfo = githubApiService.fetchRepositoryInfo(repoFullName);
        if (repoInfo == null) {
            throw new IllegalArgumentException("Repository not found on GitHub: " + repoFullName);
        }

        repoInfo.setUser(user);
        Repository savedRepository = repositoryRepository.save(repoInfo);

        return convertToDto(savedRepository);
    }

    public List<RepositoryDto> getUserRepositories(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId));

        List<Repository> repositories = repositoryRepository.findByUser(user);

        return repositories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<Repository> getRepositoriesToUpdate(int batchSize) {
        return repositoryRepository.findAllByOrderByLastCheckedAsc();
    }

    public void updateLastChecked(Repository repository) {
        repository.setLastChecked(LocalDateTime.now());
        repositoryRepository.save(repository);
    }

    private RepositoryDto convertToDto(Repository repository) {
        RepositoryDto dto = new RepositoryDto();
        dto.setId(repository.getId());
        dto.setName(repository.getName());
        dto.setLastChecked(repository.getLastChecked());
        return dto;
    }
}