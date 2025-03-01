package com.example.harbour_swer.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class Subscription {
    @NotEmpty(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    private String timestamp;
    private String ip;

    public Subscription() {
        timestamp = java.time.LocalDateTime.now().toString();
    }

    public Subscription(String email) {
        this.email = email;
        timestamp = java.time.LocalDateTime.now().toString();
    }
}