package com.abara.controller;

import com.abara.entity.User;
import com.abara.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public List<User> list() {
        return userService.list();
    }

    @GetMapping("/details/{userId}")
    public ResponseEntity<User> details(@PathVariable Long userId) {
        Optional<User> user = userService.findById(userId);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestBody User user) {
        if (user == null) return ResponseEntity.noContent().build();
        User newUser = userService.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/details/{id}").buildAndExpand(newUser.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(@RequestBody User user) {
        if (user == null) return ResponseEntity.noContent().build();
        Optional<User> existingUserOptional = userService.findById(user.getId());
        if (!existingUserOptional.isPresent()) return ResponseEntity.noContent().build();

        User existingUser = existingUserOptional.get();

        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(user.getPassword());
        existingUser.setRoles(user.getRoles());

        userService.save(existingUser);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        if (userId == null) return ResponseEntity.noContent().build();
        Optional<User> existingUser = userService.findById(userId);
        if (!existingUser.isPresent()) return ResponseEntity.noContent().build();
        userService.delete(userId);
        return ResponseEntity.ok().build();
    }
}
