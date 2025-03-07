package com.example.harbour_swer.controllers;

import com.example.harbour_swer.forms.Subscription;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class EmailContoller {
    private final List<Subscription> emails = new ArrayList<>();

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

        String ip = request.getRemoteAddr();
        subscription.setIp(ip);

        model.addAttribute("email", subscription.getEmail());

        emails.add(subscription);

        getEmails();
        return "success";
    }

    public void getEmails() {
        for (Subscription email : emails) {
            System.out.println(email);
        }
    }
}