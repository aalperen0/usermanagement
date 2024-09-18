package com.example.my_app.Controller;

import com.example.my_app.Service.MessageProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1")
public class MessageController {

    private final MessageProducerService messageProducerService;

    @Autowired
    public MessageController(MessageProducerService messageProducerService) {
        this.messageProducerService = messageProducerService;
    }

    @PostMapping("/sendMessage")
    private ResponseEntity<String> sendMessage() {
        String totalTime = messageProducerService.sendMessagesWithThreads();
        return ResponseEntity.ok(totalTime + " ms");
    }
}
