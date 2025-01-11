package it.unisannio.userservice.entity;

import jakarta.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;
    private String password;
    private String provider; // google, github, facebook, site
    private String providerId;
    private String fullName;
    @Column(unique = true, nullable = false)
    private String email;

    public User(String username, String password, String provider, String providerId, String fullName, String email) {
        this.username = username;
        this.password = password;
        this.provider = provider;
        this.providerId = providerId;
        this.fullName = fullName;
        this.email = email;
    }

    public User() {

    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String firstName) {
        this.fullName = firstName;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
}
