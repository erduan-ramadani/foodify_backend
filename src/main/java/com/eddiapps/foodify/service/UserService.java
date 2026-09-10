package com.eddiapps.foodify.service;

import com.eddiapps.foodify.dto.CreateUserRequest;
import com.eddiapps.foodify.dto.UserResponse;
import com.eddiapps.foodify.entity.UserEntity;
import com.eddiapps.foodify.exception.EmailAlreadyExistsException;
import com.eddiapps.foodify.exception.UserNotFoundException;
import com.eddiapps.foodify.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        UserEntity savedUser = userRepository.save(user);
        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName()
        );
    }

    public UserResponse getUserById(Long id) {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User mit id: " + id + " wurde nicht gefunden");
        }

        UserEntity foundUser = user.get();
        return new UserResponse(
                foundUser.getId(),
                foundUser.getEmail(),
                foundUser.getName()
        );
    }

}
