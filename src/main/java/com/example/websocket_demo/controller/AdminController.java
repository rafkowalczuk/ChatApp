package com.example.websocket_demo.controller;

import com.example.websocket_demo.entity.Role;
import com.example.websocket_demo.entity.User;
import com.example.websocket_demo.repository.RoleRepository;
import com.example.websocket_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public String adminPage() {
        return "admin";
    }

    @PostMapping("/change-role")
    public String changeUserRole(@RequestParam String username, @RequestParam String roleName) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        user.getRoles().clear();
        user.getRoles().add(role);

        userRepository.save(user);

        return "redirect:/admin";
    }

}
