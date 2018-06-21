package com.abara.service;

import com.abara.entity.Role;
import com.abara.entity.User;
import com.abara.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    public void save() {
        String username = "test";
        String password = "testPassword";
        User user = new User(username, password, new Role("USER"));

        given(passwordEncoder.encode(password)).willReturn("testEncoded");

        service.save(user);

        verify(passwordEncoder, times(1)).encode(password);
        verify(repository, times(1)).save(user);
    }

    @Test
    public void list() {
        User user1 = new User("testUser1", "test", new Role("ADMIN"));
        User user2 = new User("testUser2", "test", new Role("USER"));

        given(repository.findAll()).willReturn(Stream.of(user1, user2).collect(Collectors.toList()));

        List<User> userList = service.list();
        assertEquals(2, userList.size());
        assertEquals(user1, userList.get(0));
        assertEquals(user2, userList.get(1));
    }

    @Test
    public void findById() {
        Long userId = 77L;
        User user = new User("test", "test", new Role("USER"));

        given(repository.findById(userId)).willReturn(Optional.of(user));

        Optional<User> result = service.findById(userId);
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    public void delete() {
        Long userId = 77L;

        service.delete(userId);

        verify(repository, times(1)).deleteById(userId);
    }
}