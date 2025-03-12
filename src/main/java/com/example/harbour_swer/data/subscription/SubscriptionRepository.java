package com.example.harbour_swer.data.subscription;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    boolean existsByEmail(String email);
}