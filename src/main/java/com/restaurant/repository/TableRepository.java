package com.restaurant.repository;

import com.restaurant.entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepository extends JpaRepository<TableEntity, Long> {
    boolean existsByTableNumber(int tableNumber);
}
