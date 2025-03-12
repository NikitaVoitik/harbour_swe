package com.example.harbour_swer.controllers;

import com.example.harbour_swer.controllers.subscription_email.EmailController;
import com.example.harbour_swer.data.subscription.Subscription;
import com.example.harbour_swer.data.subscription.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private EmailController emailController;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest request;

    @Test
    void testValidation() {
        Subscription subscription = new Subscription("test");
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = emailController.emailSubmit(subscription, bindingResult, model, request);

        assertEquals("main", viewName);
        verify(subscriptionService, never()).addSubscription(any());
    }

    @Test
    void testEmailSubmit() {
        Subscription subscription = new Subscription("test@example.com");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(subscriptionService.getAllSubscriptions()).thenReturn(List.of(subscription));

        String viewName = emailController.emailSubmit(subscription, bindingResult, model, request);

        assertEquals("success", viewName);
        verify(subscriptionService).addSubscription(subscription);
        verify(model).addAttribute("email", "test@example.com");
        verify(model).addAttribute("subscriptions", List.of(subscription));
        assertEquals("127.0.0.1", subscription.getIp());
    }
}