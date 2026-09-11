package com.eddiapps.foodify.repository;

import com.eddiapps.foodify.entity.FoodEntryEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodEntryRepository extends JpaRepository<FoodEntryEntity, Long> {

    List<FoodEntryEntity> findByUser_Id(Long userId);
}
