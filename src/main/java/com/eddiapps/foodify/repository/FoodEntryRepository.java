package com.eddiapps.foodify.repository;

import com.eddiapps.foodify.entity.FoodEntryEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface FoodEntryRepository extends JpaRepository<FoodEntryEntity, Long> {

    Page<FoodEntryEntity> findByUser_Id(Long userId, Pageable pageable);

    Page<FoodEntryEntity> findByUser_IdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            Long userId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    void deleteByUser_Id(Long userId);
}
