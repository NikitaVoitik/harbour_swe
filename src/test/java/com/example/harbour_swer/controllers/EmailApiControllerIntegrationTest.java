package com.example.harbour_swer.controllers;

import com.example.harbour_swer.data.subscription.Subscription;
import com.example.harbour_swer.data.subscription.SubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EmailApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        subscriptionRepository.deleteAll();
    }

    @Test
    void testGetAllSubscriptions() throws Exception {
        Subscription sub1 = new Subscription("test1@example.com");
        Subscription sub2 = new Subscription("test2@example.com");
        subscriptionRepository.save(sub1);
        subscriptionRepository.save(sub2);

        mockMvc.perform(get("/api/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder("test1@example.com", "test2@example.com")));
    }

    @Test
    void testSubscribe() throws Exception {
        Subscription subscription = new Subscription("new@example.com");

        mockMvc.perform(post("/api/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(subscription)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.ip").isNotEmpty())
                .andExpect(jsonPath("$.source").value("API"));

        mockMvc.perform(get("/api/subscriptions"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("new@example.com"));
    }
}