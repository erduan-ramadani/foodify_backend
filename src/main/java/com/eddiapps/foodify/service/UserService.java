package com.eddiapps.foodify.service;

import com.eddiapps.foodify.entity.UserEntity;
import com.eddiapps.foodify.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity createUser(UserEntity user) {
        return userRepository.save(user);
    }
}
