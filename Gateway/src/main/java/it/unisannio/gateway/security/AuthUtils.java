package it.unisannio.gateway.security;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthUtils {

    @GetMapping("/user-id")
    public int getUserId(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8081/user/email/" + email);

        Response response = target.request(MediaType.APPLICATION_JSON).get();

        Integer id = response.readEntity(Integer.class);

        if (response.getStatus() == 200) {
            return id;
        } else
            return 0;
    }
}
