package it.unisannio.gateway.service;

import it.unisannio.gateway.entity.CustomOAuth2User;
import it.unisannio.gateway.entity.User;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);


        String provider = userRequest.getClientRegistration().getRegistrationId(); // google, github, facebook
        String providerId = oAuth2User.getName();
        String email = oAuth2User.getAttribute("email");
        String fullName = oAuth2User.getAttribute("name");
        String username = oAuth2User.getAttribute("login");

        User user = new User();
        user.setUsername((username != null ? username : email)); // Si usa l'email come username se lo stesso è nullo
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("defaultPassword")); // Genera una password di default
        user.setFullName(fullName);
        user.setProvider(provider);
        user.setProviderId(providerId);

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8081/user/register");

        try {
            Response response = target
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(user, MediaType.APPLICATION_JSON));

            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                System.out.println("Utente creato con successo!");
            } else if (response.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
                System.out.println("Utente già esistente, recupero dati...");
            } else {
                System.err.println("Errore durante la registrazione utente: " + response.getStatus());
                throw new OAuth2AuthenticationException(new OAuth2Error("registration_error"),
                        "Errore durante la registrazione utente");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new OAuth2AuthenticationException(new OAuth2Error("registration_error"), "Errore durante la registrazione utente");
        }

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }
}
