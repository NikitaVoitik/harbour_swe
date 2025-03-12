package com.example.harbour_swer.controllers;

import com.example.harbour_swer.data.subscription.SubscriptionRepository;
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
public class EmailControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @BeforeEach
    void setup() {
        subscriptionRepository.deleteAll();
    }

    @Test
    void testGetMainPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("subscription"));
    }

    @Test
    void testEmailSubmit_ValidEmail() throws Exception {
        mockMvc.perform(post("/subscribe")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("success"))
                .andExpect(model().attribute("email", "test@example.com"))
                .andExpect(model().attributeExists("subscriptions"));

        mockMvc.perform(get("/api/subscriptions"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("test@example.com"))
                .andExpect(jsonPath("$[0].source").value("PAGE"));
    }

    @Test
    void testEmailSubmit_InvalidEmail() throws Exception {
        mockMvc.perform(post("/subscribe")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "invalid-email"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("subscription", "email"));

        mockMvc.perform(get("/api/subscriptions"))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}