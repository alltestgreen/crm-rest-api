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

        validateUser(user);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    @Override
    public List<ApplicationUserDetails> list() {
        return userRepository.findAll().stream()
                .map(ApplicationUserDetails::fromUser).collect(Collectors.toList());
    }

    @Override
    public ApplicationUserDetails getDetailsById(Long id) {
        User user = getUserById(id);

        return ApplicationUserDetails.fromUser(user);
    }

    @Override
    public Long update(User user) {

        User existingUser = getUserById(user.getId());

        existingUser.setUsername(user.getUsername());
        existingUser.setRoles(user.getRoles());
        if (isNotBlank(user.getPassword())) {
            existingUser.setPassword(user.getPassword());
        }

        validateUser(existingUser);

        User updatedUser = userRepository.save(existingUser);
        return updatedUser.getId();
    }

    @Override
    public void delete(Long id) {
        User user = getUserById(id);

        userRepository.deleteById(user.getId());
    }

    private void validateUser(User user) {
        Optional<ValidationResult> validationResult = entityValidator.validate(user);
        if (validationResult.isPresent()) throw new ValidationException(validationResult.get());
    }

    private User getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) throw new EntityNotFoundException("Could not find User by ID: " + id);
        return userOptional.get();
    }

}