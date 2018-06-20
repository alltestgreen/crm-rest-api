package com.abara.service;

import com.abara.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User save(User user);

    List<User> list();

    Optional<User> findById(Long id);

    void delete(Long id);
}
