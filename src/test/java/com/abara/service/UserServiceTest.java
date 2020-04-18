package com.abara.service;

import com.abara.entity.Role;
import com.abara.entity.User;
import com.abara.model.UserDetails;
import com.abara.repository.UserRepository;
import com.abara.validation.EntityValidator;
import com.abara.validation.ValidationException;
import com.abara.validation.ValidationResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EntityValidator entityValidator;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    public void create() {
        Long userID = 2L;
        String username = "test";
        String password = "testPassword";
        User user = new User(username, password, new Role("USER"));

        given(entityValidator.validate(user)).willReturn(Optional.empty());
        given(passwordEncoder.encode(password)).willReturn("testEncoded");
        User mock = mock(User.class);
        given(mock.getId()).willReturn(userID);
        given(repository.save(user)).willReturn(mock);

        Long createdId = service.create(user);

        verify(entityValidator, times(1)).validate(user);
        verify(passwordEncoder, times(1)).encode(password);
        verify(repository, times(1)).save(user);

        assertEquals(userID, createdId);
    }

    @Test
    public void list() {
        User user1 = new User("testUser1", "test", new Role("ADMIN"));
        User user2 = new User("testUser2", "test", new Role("USER"));

        given(repository.findAll()).willReturn(Stream.of(user1, user2).collect(Collectors.toList()));

        List<UserDetails> userList = service.list();
        assertEquals(2, userList.size());

        UserDetails userDetails1 = userList.get(0);
        UserDetails userDetails2 = userList.get(1);

        assertEquals(user1.getId(), userDetails1.getId());
        assertEquals(user1.getUsername(), userDetails1.getUsername());
        assertEquals(user1.getRoles(), userDetails1.getRoles());
        assertEquals(user2.getId(), userDetails2.getId());
        assertEquals(user2.getUsername(), userDetails2.getUsername());
        assertEquals(user2.getRoles(), userDetails2.getRoles());
    }

    @Test
    public void getDetailsById() {
        Long userId = 77L;
        User user = new User("test", "test", new Role("USER"));

        given(repository.findById(userId)).willReturn(Optional.of(user));

        UserDetails userDetails = service.getDetailsById(userId);

        assertNotNull(userDetails);
        assertEquals(user.getId(), userDetails.getId());
        assertEquals(user.getUsername(), userDetails.getUsername());
        assertEquals(user.getRoles(), userDetails.getRoles());
    }

    @Test
    public void update() {
        Long userId = 77L;
        User existingUser = new User("test1", "test1", new Role("USER"));
        existingUser.setId(userId);
        User updateUser = new User("test2", "test2", new Role("ADMIN"));
        updateUser.setId(userId);

        given(repository.findById(userId)).willReturn(Optional.of(existingUser));
        given(repository.save(existingUser)).willReturn(existingUser);

        Long updatedId = service.update(updateUser);

        verify(repository, times(1)).save(updateUser);

        assertEquals(userId, updatedId);
    }

    @Test
    public void delete() {
        Long userId = 77L;
        User user = new User("test", "test", new Role("USER"));
        user.setId(userId);

        given(repository.findById(userId)).willReturn(Optional.of(user));

        service.delete(userId);

        verify(repository, times(1)).deleteById(userId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testEntityNotFound() {
        Long userId = 77L;

        given(repository.findById(userId)).willReturn(Optional.empty());

        service.delete(userId);
    }

    @Test(expected = ValidationException.class)
    public void testEntityNotValid() {
        User user = mock(User.class);
        ValidationResult mockValidationResult = mock(ValidationResult.class);

        given(entityValidator.validate(user)).willReturn(Optional.of(mockValidationResult));

        service.create(user);
    }
}