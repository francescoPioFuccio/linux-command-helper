package it.unisannio.historyservice.service;

import it.unisannio.historyservice.entity.UserPromptResponse;
import it.unisannio.historyservice.repository.UserPromptResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPromptResponseService {

    @Autowired
    private UserPromptResponseRepository userPromptResponseRepository;

    public ResponseEntity findAllByUserId(Integer userId) {
        Iterable<UserPromptResponse> userPromptResponses = userPromptResponseRepository.findAllByUserId(userId);

        if (!userPromptResponses.iterator().hasNext()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("L'utente non ha ancora interrogato il modello LLM!");
        } else {
            return ResponseEntity.ok(userPromptResponses);
        }
    }

    public ResponseEntity addUserPromptResponse(Integer userId, UserPromptResponse userPromptResponse) {

        List<UserPromptResponse> existingResponses = userPromptResponseRepository.findAllByUserIdOrderByTimestampAsc(userId);

        if (existingResponses.size() >= 20) {
            UserPromptResponse oldestResponse = existingResponses.get(0);
            userPromptResponseRepository.delete(oldestResponse);
        }

        userPromptResponse.setTimestamp(userPromptResponse.getTimestamp());
        userPromptResponseRepository.save(userPromptResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body("Risposta aggiunta con successo!");
    }
}


