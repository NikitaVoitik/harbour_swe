package com.example.harbour_swer.controllers.subscription_email;

import com.example.harbour_swer.data.subscription.Subscription;
import com.example.harbour_swer.data.subscription.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EmailApiController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public EmailApiController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/subscriptions")
    public List<Subscription> getAllSubscriptions() {
        return subscriptionService.getAllSubscriptions();
    }

    @PostMapping("/subscribe")
    public Subscription subscribe(@Valid @RequestBody Subscription subscription, HttpServletRequest request) {
        subscription.setIp(request.getRemoteAddr());
        subscription.setApiSource();
        subscriptionService.addSubscription(subscription);
        return subscription;
    }
}