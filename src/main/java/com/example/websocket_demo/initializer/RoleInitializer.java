package com.example.websocket_demo.initializer;

import com.example.websocket_demo.entity.Role;
import com.example.websocket_demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByName("USER").isEmpty()) {
            Role userRole = new Role();
            userRole.setName("USER");
            roleRepository.save(userRole);
        }

        if (roleRepository.findByName("MODERATOR").isEmpty()) {
            Role moderatorRole = new Role();
            moderatorRole.setName("MODERATOR");
            roleRepository.save(moderatorRole);
        }
    }
}