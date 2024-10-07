package com.example.websocket_demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.GrantedAuthority;


@Controller
public class ChatController {

    @GetMapping("/chat")
    public String showChatPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auth.getAuthorities().forEach(authority -> {
            System.out.println("Authority: " + authority.getAuthority());
        });

        String username = auth.getName();
        model.addAttribute("username", username);

        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("USER");
        model.addAttribute("role", role);

        return "chat";
    }

}
