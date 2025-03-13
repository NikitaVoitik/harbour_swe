package com.example.harbour_swer.data.github;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.harbour_swer.data.user.User;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface RepositoryRepository extends JpaRepository<Repository, Long> {
    List<Repository> findByUser(User user);

    List<Repository> findAllByOrderByLastCheckedAsc();
}
