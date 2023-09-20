package com.example.foodmenuapp.food;

import java.util.Collections;
import java.util.List;

record ChatGPTRequest(
        String model,
        List<ChatGPTMessage> messages
) {
    ChatGPTRequest(String message) {
        this("gpt-3.5-turbo", List.of(new ChatGPTMessage("user", message)));
    }
}
