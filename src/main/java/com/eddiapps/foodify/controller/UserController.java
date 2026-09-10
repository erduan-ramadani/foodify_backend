package com.eddiapps.foodify.controller;

import com.eddiapps.foodify.dto.CreateUserRequest;
import com.eddiapps.foodify.entity.UserEntity;
import com.eddiapps.foodify.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserEntity createUser(@Valid @RequestBody CreateUserRequest user) {
        return userService.createUser(user);
    }
}
