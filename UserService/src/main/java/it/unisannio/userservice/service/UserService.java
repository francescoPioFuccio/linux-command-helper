package it.unisannio.userservice.service;

import it.unisannio.userservice.entity.User;
import it.unisannio.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<String> registerUser(String username, String email, String password, String provider, String providerId, String fullName) {
        if (userRepository.findByEmail(email).isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists!");

        if (userRepository.findByUsername(username).isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists!");

        userRepository.save(new User(username, password, provider, providerId, fullName, email));
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }

    public ResponseEntity<String> deleteUser(Integer id) {
       if(!userRepository.findById(id).isPresent())
           return ResponseEntity.status(HttpStatus.CONFLICT).body("User not found!");

       userRepository.deleteById(id);
       return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully!");
    }
}
