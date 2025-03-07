package com.example.harbour_swer.controllers;

import com.example.harbour_swer.data.Subscription;
import com.example.harbour_swer.data.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EmailController {
    private final SubscriptionService subscriptionService;

    public EmailController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/")
    public String emailView(Model model) {
        model.addAttribute("subscription", new Subscription());
        return "main";
    }

    @PostMapping("/subscribe")
    public String emailSubmit(@ModelAttribute("subscription") @Valid Subscription subscription, BindingResult bindingResult, Model model, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "main";
        }

        subscription.setIp(request.getRemoteAddr());
        subscription.setPageSource();
        subscriptionService.addSubscription(subscription);

        model.addAttribute("email", subscription.getEmail());
        model.addAttribute("subscriptions", subscriptionService.getAllSubscriptions());

        return "success";
    }
}