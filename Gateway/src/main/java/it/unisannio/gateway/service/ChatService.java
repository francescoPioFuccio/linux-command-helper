package it.unisannio.gateway.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unisannio.gateway.entity.MessageRequest;
import it.unisannio.gateway.entity.UserPromptResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    public ResponseEntity<String> sendMessage(MessageRequest request) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://rag-server:8083/query");

        String jsonMessage = "{ \"query\": \"" + request.getMessage() + "\" }";

        Response response = target.request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonMessage, MediaType.APPLICATION_JSON));

        String entity = response.readEntity(String.class);

        if (response.getStatus() != 200) {
            return ResponseEntity.status(response.getStatus()).body(entity);
        } else {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(entity);
                String response_content = rootNode.get("response").asText();

                if (request.getUser() != 0) {
                    saveUserHistory(request.getMessage(), response_content, request.getUser());
                }

                return ResponseEntity.ok().body(response_content);

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Errore durante l'elaborazione della risposta");
            }
        }
    }

    private void saveUserHistory(String prompt, String response, int userId){
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://history-service:8082/history/user/" + userId);
        UserPromptResponse history = new UserPromptResponse(userId, prompt, response);

        target.request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(history, MediaType.APPLICATION_JSON));

    }

    public ResponseEntity<List<Map<String, Object>>> getUserHistory(int userId) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://history-service:8082/history/user/" + userId);

        Response response = target.request(MediaType.APPLICATION_JSON).get();

        if (response.getStatus() != 200)
            return ResponseEntity.status(response.getStatus()).body(null);

        List<UserPromptResponse> history = response.readEntity(new GenericType<List<UserPromptResponse>>() {});

        List<Map<String, Object>> result = new ArrayList<>();
        for (UserPromptResponse item : history) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("prompt", item.getPrompt());
            result.add(map);
        }

        return ResponseEntity.ok(result);
    }

    public ResponseEntity getPromptDetails(int id) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://history-service:8082/history/prompt/" + id);
        Response response = target.request(MediaType.APPLICATION_JSON).get();
        if (response.getStatus() != 200)
            return ResponseEntity.status(response.getStatus()).body(null);

        UserPromptResponse item = response.readEntity(UserPromptResponse.class);

        Map<String, String> details = Map.of(
                "prompt", item.getPrompt(),
                "response", item.getResponse()
        );

        return ResponseEntity.ok(details);
    }
}
