package com.restaurant.repository;

import com.restaurant.entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishRepository extends JpaRepository<DishEntity, Long> {
    boolean existsByName(String name);
}
