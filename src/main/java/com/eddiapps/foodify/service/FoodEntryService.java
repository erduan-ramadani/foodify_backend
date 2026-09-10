package com.eddiapps.foodify.service;

import com.eddiapps.foodify.dto.CreateFoodEntryRequest;
import com.eddiapps.foodify.dto.FoodEntryResponse;
import com.eddiapps.foodify.entity.FoodEntryEntity;
import com.eddiapps.foodify.entity.UserEntity;
import com.eddiapps.foodify.exception.UserNotFoundException;
import com.eddiapps.foodify.repository.FoodEntryRepository;
import com.eddiapps.foodify.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FoodEntryService {

    private final FoodEntryRepository foodEntryRepository;
    private final UserRepository userRepository;

    public FoodEntryService(
            FoodEntryRepository foodEntryRepository,
            UserRepository userRepository
    ) {
        this.foodEntryRepository = foodEntryRepository;
        this.userRepository = userRepository;
    }

    public FoodEntryResponse createFoodEntry(Long userId, CreateFoodEntryRequest request) {
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id: " + userId + " does not exist");
        }
        FoodEntryEntity foodEntryEntity = new FoodEntryEntity();
        foodEntryEntity.setName(request.getName());
        foodEntryEntity.setCalories(request.getCalories());
        foodEntryEntity.setProtein(request.getProtein());
        foodEntryEntity.setCarbs(request.getCarbs());
        foodEntryEntity.setFat(request.getFat());
        foodEntryEntity.setUser(user.get());

        FoodEntryEntity savedFoodEntry = foodEntryRepository.save(foodEntryEntity);
        return new FoodEntryResponse(
                savedFoodEntry.getId(),
                savedFoodEntry.getName(),
                savedFoodEntry.getCalories(),
                savedFoodEntry.getProtein(),
                savedFoodEntry.getCarbs(),
                savedFoodEntry.getFat()
        );
    }
}
