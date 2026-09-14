package com.eddiapps.foodify.controller;

import com.eddiapps.foodify.dto.CreateFoodEntryRequest;
import com.eddiapps.foodify.dto.FoodEntryResponse;
import com.eddiapps.foodify.dto.UpdateFoodEntryRequest;
import com.eddiapps.foodify.entity.UserEntity;
import com.eddiapps.foodify.service.FoodEntryService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/food-entries")
public class FoodEntryController {

    private final FoodEntryService foodEntryService;

    public FoodEntryController(FoodEntryService foodEntryService) {
        this.foodEntryService = foodEntryService;
    }

    @PostMapping
    public FoodEntryResponse createFoodEntry(
            Authentication authentication,
            @Valid @RequestBody CreateFoodEntryRequest request) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return foodEntryService.createFoodEntry(user.getId(), request);
    }

    @GetMapping
    public Page<FoodEntryResponse> getFoodEntries(
            Authentication authentication,
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return foodEntryService.getFoodEntriesByUserId(user.getId(), pageable);
    }

    @PutMapping("/{foodEntryId}")
    public FoodEntryResponse updateFoodEntry(
            Authentication authentication,
            @PathVariable Long foodEntryId,
            @Valid @RequestBody UpdateFoodEntryRequest request) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return foodEntryService.updateFoodEntry(user.getId(), foodEntryId, request);
    }

    @DeleteMapping("/{foodEntryId}")
    public void deleteFoodEntry(
            Authentication authentication,
            @PathVariable Long foodEntryId) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        foodEntryService.deleteFoodEntry(user.getId(), foodEntryId);
    }
}
