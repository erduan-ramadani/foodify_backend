package com.eddiapps.foodify.service;

import com.eddiapps.foodify.dto.CreateUserRequest;
import com.eddiapps.foodify.dto.UserResponse;
import com.eddiapps.foodify.entity.UserEntity;
import com.eddiapps.foodify.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(CreateUserRequest request) {
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
}
