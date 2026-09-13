package com.eddiapps.foodify.service;

import com.eddiapps.foodify.dto.*;
import com.eddiapps.foodify.entity.UserEntity;
import com.eddiapps.foodify.exception.EmailAlreadyExistsException;
import com.eddiapps.foodify.exception.InvalidCredentialsException;
import com.eddiapps.foodify.exception.UserNotFoundException;
import com.eddiapps.foodify.repository.FoodEntryRepository;
import com.eddiapps.foodify.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final FoodEntryRepository foodEntryRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public UserService(
            UserRepository userRepository,
            FoodEntryRepository foodEntryRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.foodEntryRepository = foodEntryRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        String passwordHash = passwordEncoder.encode(request.getPassword());
        user.setPasswordHash(passwordHash);

        UserEntity savedUser = userRepository.save(user);
        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName()
        );
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id: " + id + " not found");
        }

        UserEntity foundUser = user.get();
        return new UserResponse(
                foundUser.getId(),
                foundUser.getEmail(),
                foundUser.getName()
        );
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getName()))
                .toList();
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id: " + id + " not found");
        }
        UserEntity userEntity = user.get();
        userEntity.setName(request.getName());
        userEntity.setEmail(request.getEmail());
        return new UserResponse(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getName()
        );
    }

    @Transactional
    public void deleteUser(Long id) {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id: " + id + " not found");
        }
        foodEntryRepository.deleteByUser_Id(id);
        userRepository.deleteById(id);
    }

    public LoginResponse login(LoginRequest request) {
        Optional<UserEntity> user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            log.warn("User login not successful");
            throw new InvalidCredentialsException("Invalid credentials");
        }

        UserEntity userEntity = user.get();
        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPasswordHash())) {
            log.warn("User login not successful");
            throw new InvalidCredentialsException("Invalid credentials");
        }

        log.info("User logged in: {}", userEntity.getEmail());

        return new LoginResponse(
                jwtService.generateToken(userEntity), "Bearer"
        );
    }

}
