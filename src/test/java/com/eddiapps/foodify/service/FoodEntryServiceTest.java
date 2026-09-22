package com.eddiapps.foodify.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.eddiapps.foodify.dto.CreateFoodEntryRequest;
import com.eddiapps.foodify.dto.FoodEntryResponse;
import com.eddiapps.foodify.dto.UpdateFoodEntryRequest;
import com.eddiapps.foodify.entity.FoodEntryEntity;
import com.eddiapps.foodify.entity.UserEntity;
import com.eddiapps.foodify.exception.FoodEntryNotFoundException;
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

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class FoodEntryServiceTest {
    @Mock
    private FoodEntryRepository foodEntryRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private FoodEntryService foodEntryService;

    @Test
    void createFoodEntry() {

        CreateFoodEntryRequest foodEntryRequest = new CreateFoodEntryRequest();
        foodEntryRequest.setName("Chicken");
        foodEntryRequest.setCalories(100);
        foodEntryRequest.setCarbs(50.0);
        foodEntryRequest.setFat(20.0);
        foodEntryRequest.setProtein(60.0);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("eddi@example.com");
        userEntity.setName("Eddi");


        FoodEntryEntity savedFoodEntry = new FoodEntryEntity();
        savedFoodEntry.setId(1L);
        savedFoodEntry.setName("Chicken");
        savedFoodEntry.setCalories(100);
        savedFoodEntry.setCarbs(50.0);
        savedFoodEntry.setFat(20.0);
        savedFoodEntry.setProtein(60.0);
        savedFoodEntry.setUser(userEntity);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(userEntity));

        when(foodEntryRepository.save(any(FoodEntryEntity.class)))
                .thenReturn(savedFoodEntry);

        FoodEntryResponse response =
                foodEntryService.createFoodEntry(1L, foodEntryRequest);

        assertEquals(1L, response.getId());
        assertEquals("Chicken", response.getName());
        assertEquals(100, response.getCalories());
        assertEquals(60.0, response.getProtein());
        assertEquals(50.0, response.getCarbs());
        assertEquals(20.0, response.getFat());

        verify(userRepository, times(1)).findById(1L);
        verify(foodEntryRepository, times(1))
                .save(any(FoodEntryEntity.class));
    }

    @Test
    void createFoodEntry_userNotFound() {
        CreateFoodEntryRequest foodEntryRequest = new CreateFoodEntryRequest();
        foodEntryRequest.setName("Döner");
        foodEntryRequest.setCalories(600);
        foodEntryRequest.setCarbs(150.0);
        foodEntryRequest.setFat(40.0);
        foodEntryRequest.setProtein(20.0);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> foodEntryService.createFoodEntry(99L, foodEntryRequest));

        verify(foodEntryRepository, never()).save(any(FoodEntryEntity.class));
    }

    @Test
    void getFoodEntriesByUserId() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        FoodEntryEntity foodEntry1 = new FoodEntryEntity();
        foodEntry1.setName("Chicken");

        FoodEntryEntity foodEntry2 = new FoodEntryEntity();
        foodEntry2.setName("Döner");

        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(foodEntryRepository.findByUser_Id(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(foodEntry1, foodEntry2)));

        Page<FoodEntryResponse> response = foodEntryService.getFoodEntriesByUserId(1L, pageable);

        assertEquals(2, response.getTotalElements());
        assertEquals("Chicken", response.getContent().getFirst().getName());

        verify(foodEntryRepository).findByUser_Id(1L, pageable);
    }

    @Test
    void getFoodEntriesByUserId_userNotFound() {
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> foodEntryService.getFoodEntriesByUserId(99L, pageable)
        );

        verify(foodEntryRepository, never()).findByUser_Id(99L, pageable);
    }

    @Test
    void updateFoodEntry() {
        UserEntity user = new UserEntity();
        user.setId(1L);

        FoodEntryEntity foodEntry = new FoodEntryEntity();
        foodEntry.setId(10L);
        foodEntry.setName("Döner");
        foodEntry.setUser(user);

        UpdateFoodEntryRequest request = new UpdateFoodEntryRequest();
        request.setName("Pommes");
        request.setCalories(800);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(foodEntryRepository.findById(10L)).thenReturn(Optional.of(foodEntry));
        when(foodEntryRepository.save(foodEntry)).thenReturn(foodEntry);

        FoodEntryResponse updatedFoodEntry = foodEntryService.updateFoodEntry(
                user.getId(), foodEntry.getId(), request
        );

        assertEquals("Pommes", updatedFoodEntry.getName());
        assertEquals(800, updatedFoodEntry.getCalories());

        verify(foodEntryRepository).save(foodEntry);
    }

    @Test
    void updateFoodEntry_wrongUser() {
        UserEntity user = new UserEntity();
        user.setId(1L);

        UserEntity user2 = new UserEntity();
        user2.setId(2L);

        FoodEntryEntity foodEntry = new FoodEntryEntity();
        foodEntry.setId(10L);
        foodEntry.setName("Döner");
        foodEntry.setUser(user2);

        UpdateFoodEntryRequest request = new UpdateFoodEntryRequest();
        request.setName("Pommes");
        request.setCalories(800);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(foodEntryRepository.findById(10L)).thenReturn(Optional.of(foodEntry));

        assertThrows(FoodEntryNotFoundException.class,
                () -> foodEntryService.updateFoodEntry(user.getId(), foodEntry.getId(), request)
        );

        verify(foodEntryRepository, never()).save(any(FoodEntryEntity.class));
    }

    @Test
    void deleteFoodEntry() {
        UserEntity user = new UserEntity();
        user.setId(1L);

        FoodEntryEntity foodEntry = new FoodEntryEntity();
        foodEntry.setId(10L);
        foodEntry.setName("Döner");
        foodEntry.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(foodEntryRepository.findById(10L)).thenReturn(Optional.of(foodEntry));

        foodEntryService.deleteFoodEntry(user.getId(), foodEntry.getId());

        verify(foodEntryRepository).delete(foodEntry);
    }

    @Test
    void deleteFoodEntry_wrongUser() {
        UserEntity user1 = new UserEntity();
        user1.setId(1L);

        UserEntity user2 = new UserEntity();
        user2.setId(2L);

        FoodEntryEntity foodEntry = new FoodEntryEntity();
        foodEntry.setId(10L);
        foodEntry.setUser(user2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(foodEntryRepository.findById(10L)).thenReturn(Optional.of(foodEntry));

        assertThrows(FoodEntryNotFoundException.class,
                () -> foodEntryService.deleteFoodEntry(user1.getId(), foodEntry.getId())
        );

        verify(foodEntryRepository, never()).delete(foodEntry);
    }
}
