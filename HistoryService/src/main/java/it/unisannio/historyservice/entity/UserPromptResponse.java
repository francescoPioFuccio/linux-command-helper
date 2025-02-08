package it.unisannio.historyservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class UserPromptResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private int userId;
    @Column(nullable = false)
    private String prompt;
    @Column(nullable = false, length = 1000)
    private String response;
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    protected UserPromptResponse() { }

    public UserPromptResponse(int userId, String prompt, String response) {
        this.userId = userId;
        this.prompt = prompt;
        this.response = response;
        this.timestamp = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getResponse() {
        return response;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
