package com.abara.service;


import com.abara.entity.User;
import com.abara.model.ApplicationUserDetails;
import com.abara.repository.UserRepository;
import com.abara.validation.EntityValidator;
import com.abara.validation.ValidationException;
import com.abara.validation.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityValidator entityValidator;

    @Override
    public Long create(User user) {

        Optional<ValidationResult> validationResult = entityValidator.validate(user);
        if (validationResult.isPresent()) throw new ValidationException(validationResult.get());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    @Override
    public List<ApplicationUserDetails> list() {
        return userRepository.findAll().stream()
                .map(u -> new ApplicationUserDetails(u.getId(), u.getUsername(), u.getRoles()))
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationUserDetails getDetailsById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) throw new EntityNotFoundException("Could not find User by ID: " + id);

        return ApplicationUserDetails.fromUser(userOptional.get());
    }

    @Override
    public Long update(User user) {

        Optional<User> userOptional = userRepository.findById(user.getId());
        if (!userOptional.isPresent()) throw new EntityNotFoundException("Could not find User by ID: " + user.getId());

        User existingUser = userOptional.get();

        existingUser.setUsername(user.getUsername());
        existingUser.setRoles(user.getRoles());
        if (isNotBlank(user.getPassword())) {
            existingUser.setPassword(user.getPassword());
        }

        Optional<ValidationResult> validationResult = entityValidator.validate(existingUser);
        if (validationResult.isPresent()) throw new ValidationException(validationResult.get());

        User updatedUser = userRepository.save(existingUser);
        return updatedUser.getId();
    }

    @Override
    public void delete(Long id) {
        Optional<User> existingUser = userRepository.findById(id);
        if (!existingUser.isPresent()) throw new EntityNotFoundException("Could not find User by ID: " + id);

        userRepository.deleteById(id);
    }

}