package it.unisannio.gateway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebPagesController {

    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }
}
