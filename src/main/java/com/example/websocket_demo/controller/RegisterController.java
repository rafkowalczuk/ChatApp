package com.example.websocket_demo.controller;

import com.example.websocket_demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {
    @Autowired
    private UserService userService;



    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }
    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String role) {
        userService.registerUser(username, password, role);
        return "redirect:/login";
    }
}
