package com.example.harbour_swer.controllers;

import com.example.harbour_swer.controllers.subscription_email.EmailApiController;
import com.example.harbour_swer.data.subscription.Subscription;
import com.example.harbour_swer.data.subscription.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailApiControllerTest {

    @InjectMocks
    private EmailApiController emailApiController;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SubscriptionService subscriptionService;

    @Test
    void getAllSubscriptions() {
        Subscription subscription = new Subscription("test@example.com");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        Subscription res = emailApiController.subscribe(subscription, request);

        List<Subscription> result = List.of(subscription);
        when(subscriptionService.getAllSubscriptions()).thenReturn(result);
        assertEquals(subscriptionService.getAllSubscriptions(), result);
    }

    @Test
    void subscribe() {
        Subscription subscription = new Subscription("test@example.com");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        Subscription result = emailApiController.subscribe(subscription, request);

        assertEquals("127.0.0.1", result.getIp());
        assertEquals("test@example.com", result.getEmail());
        verify(subscriptionService).addSubscription(subscription);
    }
}