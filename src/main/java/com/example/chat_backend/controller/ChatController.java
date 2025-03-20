package com.example.chat_backend.controller;

import com.example.chat_backend.entity.Message;
import com.example.chat_backend.entity.User;
import com.example.chat_backend.repository.MessageRepository;
import com.example.chat_backend.repository.UserRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class ChatController {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public ChatController(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public Message sendMessage(Message message) {
        message.setTimestamp(LocalDateTime.now());

        // Достаём id из message.getUser().getId() и подтягиваем полного юзера
        if (message.getUser() != null && message.getUser().getId() != null) {
            Optional<User> userOptional = userRepository.findById(message.getUser().getId());
            userOptional.ifPresent(message::setUser);
        }

        messageRepository.save(message);
        return message;
    }
}