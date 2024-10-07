package com.example.websocket_demo.controller;

import com.example.websocket_demo.entity.Message;
import com.example.websocket_demo.WebSocketSessionManager;
import com.example.websocket_demo.entity.User;
import com.example.websocket_demo.repository.MessageRepository;
import com.example.websocket_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.PageRequest;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
public class WebsocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionManager sessionManager;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Autowired
    public WebsocketController(SimpMessagingTemplate messagingTemplate, WebSocketSessionManager sessionManager, MessageRepository messageRepository, UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.sessionManager = sessionManager;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public Message handleMessage(Message message) {
        Message chatMessage = new Message();
        chatMessage.setUser(message.getUser());
        chatMessage.setMessage(message.getMessage());
        chatMessage.setTimestamp(LocalDateTime.now());

        messageRepository.save(chatMessage);

        return chatMessage;
    }

    @MessageMapping("/private-message")
    public void sendPrivateMessage(Map<String, String> payload) {
        String recipient = payload.get("recipient");
        String messageContent = payload.get("message");
        String sender = payload.get("sender");

        Message privateMessage = new Message();
        privateMessage.setUser(sender);
        privateMessage.setMessage("Private message to " + recipient + ": " + messageContent);
        privateMessage.setTimestamp(LocalDateTime.now());

        messageRepository.save(privateMessage);

        messagingTemplate.convertAndSendToUser(recipient, "/queue/private", privateMessage);
    }

    @MessageMapping("/history")
    public void loadChatHistory(Map<String, String> payload) {

        String lastMessageIdString = payload.get("lastMessageId");
        Long lastMessageId = lastMessageIdString != null ? Long.parseLong(lastMessageIdString) : Long.MAX_VALUE;

        List<Message> messages = messageRepository.findByIdLessThanOrderByTimestampDesc(lastMessageId, PageRequest.of(0, 20));

        for (Message message : messages) {
            messagingTemplate.convertAndSend("/topic/history", message);
        }
    }

    @MessageMapping("/search")
    @SendTo("/topic/search-results")
    public List<Message> searchMessages(String keyword) {
        return messageRepository.findByMessageContaining(keyword);
    }

    @MessageMapping("/delete-message")
    public void deleteMessage(Map<String, String> payload, Principal principal) {
        String messageId = payload.get("messageId");
        String username = principal.getName();


        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        boolean isModerator = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("MODERATOR"));

        if (isModerator) {

            messageRepository.deleteById(Long.parseLong(messageId));
            messagingTemplate.convertAndSend("/topic/message-deleted", messageId);
        } else {

            throw new AccessDeniedException("Only moderators can delete messages");
        }
    }


    @MessageMapping("/connect")
    public void connectUser(String username) {
        sessionManager.addUser(username);

        Message joinMessage = new Message();
        joinMessage.setUser("System");
        joinMessage.setMessage(username + " has joined the chat.");
        joinMessage.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/messages", joinMessage);
    }

    @MessageMapping("/disconnect")
    public void disconnectUser(String username) {
        sessionManager.removeUser(username);

        Message leaveMessage = new Message();
        leaveMessage.setUser("System");
        leaveMessage.setMessage(username + " has left the chat.");
        leaveMessage.setTimestamp(LocalDateTime.now());

        messageRepository.save(leaveMessage);

        messagingTemplate.convertAndSend("/topic/messages", leaveMessage);
    }}