package com.restaurant.controller;

import com.restaurant.dto.DishRequest;
import com.restaurant.dto.DishResponse;
import com.restaurant.service.DishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

    @GetMapping
    public ResponseEntity<List<DishResponse>> findAll() {
        return ResponseEntity.ok(dishService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DishResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(dishService.findById(id));
    }

    @PostMapping
    public ResponseEntity<DishResponse> create(@Valid @RequestBody DishRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dishService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DishResponse> update(@PathVariable Long id, @Valid @RequestBody DishRequest request) {
        return ResponseEntity.ok(dishService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dishService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
