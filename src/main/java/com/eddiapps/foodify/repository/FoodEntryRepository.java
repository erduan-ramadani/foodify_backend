package com.eddiapps.foodify.repository;

import com.eddiapps.foodify.entity.FoodEntryEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodEntryRepository extends JpaRepository<FoodEntryEntity, Long> {

    Page<FoodEntryEntity> findByUser_Id(Long userId, Pageable pageable);

    void deleteByUser_Id(Long userId);
}
