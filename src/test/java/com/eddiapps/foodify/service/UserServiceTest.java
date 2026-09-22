package com.eddiapps.foodify.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.eddiapps.foodify.dto.*;
import com.eddiapps.foodify.entity.UserEntity;
import com.eddiapps.foodify.exception.EmailAlreadyExistsException;
import com.eddiapps.foodify.exception.InvalidCredentialsException;
import com.eddiapps.foodify.exception.UserNotFoundException;
import com.eddiapps.foodify.repository.FoodEntryRepository;
import com.eddiapps.foodify.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private FoodEntryRepository foodEntryRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private UserService userService;

    @Test
    void getUserByIdSuccessful() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setName("Eddi");
        user.setEmail("eddi@web.de");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserById(1L);

        assertEquals(1L, response.getId());

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_userNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(99L));

        verify(userRepository).findById(99L);
    }

    @Test
    void updateUserSuccessful() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setName("Eddi");
        user.setEmail("eddi@web.de");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Anna");
        request.setEmail("anna@web.de");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse userResponse = userService.updateUser(1L, request);
        assertEquals("Anna", userResponse.getName());
        assertEquals("anna@web.de", userResponse.getEmail());

        verify(userRepository).findById(1L);
    }

    @Test
    void updateUser_userNotFound() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Anna");
        request.setEmail("anna@web.de");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(99L, request));

        verify(userRepository).findById(99L);
    }

    @Test
    void deleteUserSuccessful() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setName("Eddi");
        user.setEmail("eddi@web.de");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(foodEntryRepository).deleteByUser_Id(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_userNotFound() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(99L));

        verify(userRepository).findById(99L);
        verify(foodEntryRepository, never()).deleteByUser_Id(99L);
        verify(userRepository, never()).deleteById(99L);
    }

    @Test
    void getAllUsers() {
        Pageable pageable = PageRequest.of(0, 20);

        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setName("Eddi");
        user1.setEmail("eddi@web.de");

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setName("Steff");
        user2.setEmail("steffi@web.de");

        when(userRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(user1, user2)));

        Page<UserResponse> users = userService.getAllUsers(pageable);

        assertEquals(2L, users.getTotalElements());
        assertEquals("Eddi", users.getContent().getFirst().getName());
        assertEquals(2L, users.getContent().get(1).getId());

        verify(userRepository).findAll(pageable);
    }

    @Test
    void createUser_emailAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Eddi");
        request.setPassword("Secret123");
        request.setEmail("eddi@web.de");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.createUser(request));

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void createUserSuccessful() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setEmail("eddi@web.de");
        createUserRequest.setPassword("Secret123");
        createUserRequest.setName("Eddi");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("eddi@web.de");
        user.setName("Eddi");

        when(userRepository.existsByEmail(createUserRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(createUserRequest.getPassword())).thenReturn("hashed-password");
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        UserResponse userResponse = userService.createUser(createUserRequest);

        assertEquals(1L, userResponse.getId());
        assertEquals("eddi@web.de", userResponse.getEmail());
        assertEquals("Eddi", userResponse.getName());
    }

    @Test
    void loginSuccessful() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("eddi@web.de");
        loginRequest.setPassword("Secret123");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("eddi@web.de");
        user.setPasswordHash("hashed-password");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("test-token");

        LoginResponse loginResponse = userService.login(loginRequest);

        assertEquals("test-token", loginResponse.getToken());
        assertEquals("Bearer", loginResponse.getTokenType());

        verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPasswordHash());
        verify(jwtService).generateToken(user);
    }

    @Test
    void wrongEmail() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("eddo@web.de");
        loginRequest.setPassword("Secret123");

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login(loginRequest)
        );

        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateToken(any(UserEntity.class));
    }

    @Test
    void wrongPassword() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("eddi@web.de");
        loginRequest.setPassword("Secret1234");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("eddi@web.de");
        user.setPasswordHash("hashed-password");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login(loginRequest)
        );

        verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPasswordHash());
        verify(jwtService, never()).generateToken(any(UserEntity.class));
    }
}
