package com.example.websocket_demo.service;

import com.example.websocket_demo.entity.Role;
import com.example.websocket_demo.entity.User;
import com.example.websocket_demo.repository.RoleRepository;
import com.example.websocket_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User registerUser(String username, String rawPassword, String roleName) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Tworzenie nowego użytkownika
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));

        // Znalezienie roli na podstawie nazwy (np. "USER")
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        // Przypisanie roli użytkownikowi
        user.getRoles().add(role);

        // Zapis użytkownika w bazie danych
        return userRepository.save(user);
    }
}
