package com.example.websocket_demo.client;

import com.example.websocket_demo.entity.Message;

import java.util.ArrayList;

public interface MessageListener {
    void onMessageRecieve(Message message);
    void onActiveUsersUpdated(ArrayList<String> users);
}
