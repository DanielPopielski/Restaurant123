package com.restaurant.service;

import com.restaurant.dto.DishRequest;
import com.restaurant.dto.DishResponse;
import com.restaurant.entity.DishEntity;
import com.restaurant.exception.ConflictException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.repository.DishRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishServiceTest {

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private DishService dishService;

    @Test
    void createReturnsSavedDish() {
        when(dishRepository.existsByName("Pierogi")).thenReturn(false);
        when(dishRepository.save(any(DishEntity.class))).thenAnswer(inv -> {
            DishEntity dish = inv.getArgument(0);
            dish.setId(1L);
            return dish;
        });

        DishResponse response = dishService.create(new DishRequest("Pierogi", new BigDecimal("24.50")));

        assertEquals(1L, response.id());
        assertEquals("Pierogi", response.name());
        assertEquals(new BigDecimal("24.50"), response.price());
    }

    @Test
    void createRejectsDuplicateName() {
        when(dishRepository.existsByName("Pierogi")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> dishService.create(new DishRequest("Pierogi", new BigDecimal("24.50"))));
        verify(dishRepository, never()).save(any());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(dishRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dishService.findById(99L));
    }

    @Test
    void updateModifiesExistingDish() {
        DishEntity dish = new DishEntity();
        dish.setId(1L);
        dish.setName("Pierogi");
        dish.setPrice(new BigDecimal("24.50"));
        when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));
        when(dishRepository.save(any(DishEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        DishResponse response = dishService.update(1L, new DishRequest("Pierogi ruskie", new BigDecimal("26.00")));

        assertEquals("Pierogi ruskie", response.name());
        assertEquals(new BigDecimal("26.00"), response.price());
    }
}
