package com.abara.service;

import com.abara.entity.User;
import com.abara.model.ApplicationUserDetails;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User save(User user);

    List<ApplicationUserDetails> list();

    Optional<User> findById(Long id);

    void delete(Long id);
}
