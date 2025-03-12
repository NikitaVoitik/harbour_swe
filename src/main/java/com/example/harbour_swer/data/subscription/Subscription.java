package com.example.harbour_swer.data.subscription;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
@Entity
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
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