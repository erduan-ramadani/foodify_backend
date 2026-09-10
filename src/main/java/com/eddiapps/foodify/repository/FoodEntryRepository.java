package com.eddiapps.foodify.repository;

import com.eddiapps.foodify.entity.FoodEntryEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodEntryRepository extends JpaRepository<FoodEntryEntity, Long> {
}
