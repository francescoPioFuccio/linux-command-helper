package it.unisannio.gateway.entity;

public class MessageRequest {
    private String message;
    private Integer user;

    public MessageRequest(String message, Integer user) {
        this.message = message;
        this.user = user;
    }

    protected MessageRequest() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }
}
