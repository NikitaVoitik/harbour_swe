package com.example.harbour_swer.forms;

import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class SubscriptionService {
    private final SubscriptionList subscriptionList = new SubscriptionList();

    public void addSubscription(Subscription subscription) {
        subscriptionList.addSubscription(subscription);
    }
}