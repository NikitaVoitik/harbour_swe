package com.example.harbour_swer.data;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public void addSubscription(Subscription subscription) {
        if (!subscriptionRepository.existsByEmail(subscription.getEmail())) {
            subscriptionRepository.save(subscription);
        }
    }

    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }
}