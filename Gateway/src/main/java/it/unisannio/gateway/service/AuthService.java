package it.unisannio.gateway.service;

import it.unisannio.gateway.entity.User;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<String> register(User user) {

        Client client = ClientBuilder.newClient();

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            WebTarget target = client.target("http://localhost:8081/client/register");

            Response response = target
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(user, MediaType.APPLICATION_JSON));

            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                return ResponseEntity.ok("User registered successfully.");
            } else {
                String errorMessage = response.readEntity(String.class);
                return ResponseEntity.status(response.getStatus()).body(errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        } finally {
            client.close();
        }
    }

    public ResponseEntity<String> delete(Integer id) {

        Client client = ClientBuilder.newClient();

        try {
            WebTarget target = client.target("http://localhost:8081/client/" + id);

            Response response = target.request(MediaType.APPLICATION_JSON).delete();

            if (!(response.getStatus() == Response.Status.OK.getStatusCode())) {
                String errorMessage = response.readEntity(String.class);
                return ResponseEntity.status(response.getStatus()).body(errorMessage);
            }  else {
                return ResponseEntity.ok("User deleted successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        } finally {
            client.close();
        }
    }
}
