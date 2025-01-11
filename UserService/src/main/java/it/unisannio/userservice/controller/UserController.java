package it.unisannio.userservice.controller;

import it.unisannio.userservice.entity.User;
import it.unisannio.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        return userService.registerUser(user.getUsername(), user.getEmail(), user.getPassword(), user.getProvider(), user.getProviderId(), user.getFullName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        return userService.deleteUser(id);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity getUserByEmail(@PathVariable String email) {
        if(userService.getUserByEmail(email) == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(userService.getUserByEmail(email).getId());
    }
}
