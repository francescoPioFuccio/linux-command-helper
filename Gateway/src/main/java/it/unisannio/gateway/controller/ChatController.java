package it.unisannio.gateway.controller;

import it.unisannio.gateway.entity.MessageRequest;
import it.unisannio.gateway.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/AIChat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody MessageRequest request) {
        return chatService.sendMessage(request);
    }

    @GetMapping("/history/{id}")
    public ResponseEntity getHistory(@PathVariable("id") int id) {
        return  chatService.getUserHistory(id);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity getDetails(@PathVariable("id") int id) {
        return chatService.getPromptDetails(id);
    }
}
