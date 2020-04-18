package com.abara.service;

import com.abara.entity.User;
import com.abara.model.UserDetails;

import java.util.List;

public interface UserService {

    Long create(User user);

    List<UserDetails> list();

    UserDetails getDetailsById(Long id);

    void delete(Long id);

    Long update(User user);
}
