package com.eddiapps.foodify.service;

import com.eddiapps.foodify.dto.CreateFoodEntryRequest;
import com.eddiapps.foodify.dto.FoodEntryResponse;
import com.eddiapps.foodify.dto.UpdateFoodEntryRequest;
import com.eddiapps.foodify.entity.FoodEntryEntity;
import com.eddiapps.foodify.entity.UserEntity;
import com.eddiapps.foodify.exception.FoodEntryNotFoundException;
import com.eddiapps.foodify.exception.UserNotFoundException;
import com.eddiapps.foodify.repository.FoodEntryRepository;
import com.eddiapps.foodify.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;
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
        UserEntity userEntity = getUserOrThrow(userId);

        FoodEntryEntity foodEntryEntity = new FoodEntryEntity();
        foodEntryEntity.setName(request.getName());
        foodEntryEntity.setCalories(request.getCalories());
        foodEntryEntity.setProtein(request.getProtein());
        foodEntryEntity.setCarbs(request.getCarbs());
        foodEntryEntity.setFat(request.getFat());
        foodEntryEntity.setUser(userEntity);

        FoodEntryEntity savedFoodEntry = foodEntryRepository.save(foodEntryEntity);
        return toFoodEntryResponse(savedFoodEntry);
    }

    public List<FoodEntryResponse> getFoodEntriesByUserId(Long userId) {
        getUserOrThrow(userId);
        List<FoodEntryEntity> foodEntries = foodEntryRepository.findByUser_Id(userId);
        return foodEntries.stream().map(this::toFoodEntryResponse).toList();
    }

    public FoodEntryResponse updateFoodEntry(
            Long userId,
            Long foodEntryId,
            UpdateFoodEntryRequest request) {

        FoodEntryEntity foodEntryEntity = getFoodEntryOrThrow(userId, foodEntryId);
        foodEntryEntity.setName(request.getName());
        foodEntryEntity.setCalories(request.getCalories());
        foodEntryEntity.setProtein(request.getProtein());
        foodEntryEntity.setCarbs(request.getCarbs());
        foodEntryEntity.setFat(request.getFat());

        FoodEntryEntity savedFoodEntry = foodEntryRepository.save(foodEntryEntity);
        return toFoodEntryResponse(savedFoodEntry);
    }

    public void deleteFoodEntry(Long userId, Long foodEntryId) {
        FoodEntryEntity foodEntryEntity = getFoodEntryOrThrow(userId, foodEntryId);
        foodEntryRepository.delete(foodEntryEntity);
    }

    private FoodEntryEntity getFoodEntryOrThrow(Long userId, Long foodEntryId) {
        getUserOrThrow(userId);

        Optional<FoodEntryEntity> foodEntry = foodEntryRepository.findById(foodEntryId);
        if (foodEntry.isEmpty()) {
            throw new FoodEntryNotFoundException("Food entry with id: " + foodEntryId + " not found");
        }

        if (!foodEntry.get().getUser().getId().equals(userId)) {
            throw new FoodEntryNotFoundException("Food entry with id: " + foodEntryId + " not found");
        }

        return foodEntry.get();
    }

    private UserEntity getUserOrThrow(Long userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id: " + userId + " does not exist");
        }
        return user.get();
    }

    private FoodEntryResponse toFoodEntryResponse(FoodEntryEntity foodEntry) {
        return new FoodEntryResponse(
                foodEntry.getId(),
                foodEntry.getName(),
                foodEntry.getCalories(),
                foodEntry.getProtein(),
                foodEntry.getCarbs(),
                foodEntry.getFat()
        );
    }
}
