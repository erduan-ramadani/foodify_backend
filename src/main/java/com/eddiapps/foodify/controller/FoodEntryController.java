package com.eddiapps.foodify.controller;

import com.eddiapps.foodify.dto.CreateFoodEntryRequest;
import com.eddiapps.foodify.dto.FoodEntryResponse;
import com.eddiapps.foodify.service.FoodEntryService;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/{userId}/food-entries")
public class FoodEntryController {

    private final FoodEntryService foodEntryService;

    public FoodEntryController(FoodEntryService foodEntryService) {
        this.foodEntryService = foodEntryService;
    }

    @PostMapping
    public FoodEntryResponse createFoodEntry(
            @PathVariable Long userId,
            @Valid @RequestBody CreateFoodEntryRequest request) {
        return foodEntryService.createFoodEntry(userId, request);
    }
}
