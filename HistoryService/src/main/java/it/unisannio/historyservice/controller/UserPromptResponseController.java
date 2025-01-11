package it.unisannio.historyservice.controller;

import it.unisannio.historyservice.entity.UserPromptResponse;
import it.unisannio.historyservice.service.UserPromptResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/history")
public class UserPromptResponseController {

    @Autowired
    UserPromptResponseService userPromptResponseService;

    @GetMapping("/user/{id}")
    public ResponseEntity findAllByUserId(@PathVariable int id) {
        return userPromptResponseService.findAllByUserId(id);
    }

    @PostMapping("/user/{id}")
    public ResponseEntity addHistoryElement(@PathVariable int id, @RequestBody UserPromptResponse userPromptResponse) {
        return userPromptResponseService.addUserPromptResponse(id, userPromptResponse);
    }

    @GetMapping("/prompt/{id}")
    public ResponseEntity findPromptById(@PathVariable int id) {
        return userPromptResponseService.findById(id);
    }
}
