package com.example.harbour_swer.forms;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SubscriptionList {
    private List<Subscription> subscriptions = new ArrayList<>();

    public void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
    }
}
