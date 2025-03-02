package com.example.harbour_swer.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class Subscription {
    private enum Source {
        API,
        PAGE
    }

    @NotEmpty(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    private String timestamp;
    private String ip;
    private Source source;

    public Subscription() {
        timestamp = java.time.LocalDateTime.now().toString();
    }

    public void setPageSource() {
        source = Source.PAGE;
    }

    public void setApiSource()  {
        source = Source.API;
    }

    public Subscription(String email) {
        this.email = email;
        timestamp = java.time.LocalDateTime.now().toString();
    }
}