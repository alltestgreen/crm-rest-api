package com.abara.controller;

import com.abara.entity.User;
import com.abara.model.ApplicationUserDetails;
import com.abara.service.UserService;
import com.abara.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public List<ApplicationUserDetails> list() {
        LOG.debug("Retrieving all User Details");

        return userService.list();
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ApplicationUserDetails> details(@PathVariable Long id) {
        LOG.debug("Getting details of User by id: " + id);

        ApplicationUserDetails userDetails = userService.getDetailsById(id);
        return ResponseEntity.ok(userDetails);
    }

    @PostMapping("/create")
    public ResponseEntity<ValidationResult> create(@RequestBody User user) {
        LOG.debug("Creating User: " + user);

        Long id = userService.create(user);

        return ResponseEntity.created(buildResourceUrl(id)).build();
    }

    @PutMapping("/update")
    public ResponseEntity<ValidationResult> update(@RequestBody User user) {
        LOG.debug("Updating User: " + user);

        Long id = userService.update(user);

        return ResponseEntity.ok().location(buildResourceUrl(id)).build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        LOG.debug("Deleting User by ID: " + id);

        userService.delete(id);
        return ResponseEntity.ok().build();
    }

    private URI buildResourceUrl(Long id) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/user/details/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
