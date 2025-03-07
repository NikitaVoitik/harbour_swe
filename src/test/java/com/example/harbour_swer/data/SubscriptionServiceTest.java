package com.example.harbour_swer.data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void testAddSubscription_WhenEmailDoesNotExist() {
        Subscription subscription = new Subscription("new@example.com");
        when(subscriptionRepository.existsByEmail("new@example.com")).thenReturn(false);

        subscriptionService.addSubscription(subscription);

        verify(subscriptionRepository).save(subscription);
    }

    @Test
    void testAddSubscription_WhenEmailExists() {
        Subscription subscription = new Subscription("existing@example.com");
        when(subscriptionRepository.existsByEmail("existing@example.com")).thenReturn(true);

        subscriptionService.addSubscription(subscription);

        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void testGetAllSubscriptions() {
        List<Subscription> expectedSubscriptions = Arrays.asList(
                new Subscription("user1@example.com"),
                new Subscription("user2@example.com")
        );
        when(subscriptionRepository.findAll()).thenReturn(expectedSubscriptions);

        List<Subscription> actualSubscriptions = subscriptionService.getAllSubscriptions();

        assertEquals(expectedSubscriptions, actualSubscriptions);
    }
}