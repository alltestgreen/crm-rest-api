package com.abara.service;

import com.abara.entity.User;
import com.abara.model.ApplicationUserDetails;
import com.abara.validation.ValidationException;

import java.util.List;

public interface UserService {

    Long create(User user) throws ValidationException;

    List<ApplicationUserDetails> list();

    ApplicationUserDetails getDetailsById(Long id);

    void delete(Long id);

    Long update(User user) throws ValidationException;
}
