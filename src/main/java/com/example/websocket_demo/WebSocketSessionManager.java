package com.example.websocket_demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WebSocketSessionManager {
    private final List<String> activeUsers = new ArrayList<>();
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketSessionManager(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void addUser(String username) {
        if (!activeUsers.contains(username)) {
            activeUsers.add(username);
        }
        broadcastActiveUsers();
    }

    public void removeUser(String username) {
        activeUsers.remove(username);
        broadcastActiveUsers();
    }

    public void broadcastActiveUsers() {
        messagingTemplate.convertAndSend("/topic/active-users", activeUsers);
    }
}













