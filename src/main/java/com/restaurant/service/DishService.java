package com.restaurant.service;

import com.restaurant.dto.DishRequest;
import com.restaurant.dto.DishResponse;
import com.restaurant.entity.DishEntity;
import com.restaurant.exception.ConflictException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DishService {

    private final DishRepository dishRepository;

    public List<DishResponse> findAll() {
        return dishRepository.findAll().stream().map(this::toResponse).toList();
    }

    public DishResponse findById(Long id) {
        return toResponse(getDish(id));
    }

    public DishResponse create(DishRequest request) {
        if (dishRepository.existsByName(request.name())) {
            throw new ConflictException("Dish '" + request.name() + "' already exists");
        }
        DishEntity dish = new DishEntity();
        dish.setName(request.name());
        dish.setPrice(request.price());
        return toResponse(dishRepository.save(dish));
    }

    public DishResponse update(Long id, DishRequest request) {
        DishEntity dish = getDish(id);
        dish.setName(request.name());
        dish.setPrice(request.price());
        return toResponse(dishRepository.save(dish));
    }

    public void delete(Long id) {
        dishRepository.delete(getDish(id));
    }

    private DishEntity getDish(Long id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish with id " + id + " not found"));
    }

    private DishResponse toResponse(DishEntity dish) {
        return new DishResponse(dish.getId(), dish.getName(), dish.getPrice());
    }
}
