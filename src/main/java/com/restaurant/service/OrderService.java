package com.restaurant.service;

import com.restaurant.dto.OrderItemNotification;
import com.restaurant.dto.OrderItemRequest;
import com.restaurant.dto.OrderNotification;
import com.restaurant.dto.OrderItemResponse;
import com.restaurant.dto.OrderRequest;
import com.restaurant.dto.OrderResponse;
import com.restaurant.entity.DishEntity;
import com.restaurant.entity.OrderEntity;
import com.restaurant.entity.OrderItemEntity;
import com.restaurant.entity.TableEntity;
import com.restaurant.entity.UserEntity;
import com.restaurant.enums.OrderStatus;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.repository.DishRepository;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.TableRepository;
import com.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TableRepository tableRepository;
    private final DishRepository dishRepository;
    private final OrderNotificationProducer orderNotificationProducer;

    @Transactional
    public OrderResponse createOrder(String username, OrderRequest request) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User '" + username + "' not found"));
        TableEntity table = tableRepository.findById(request.tableId())
                .orElseThrow(() -> new ResourceNotFoundException("Table with id " + request.tableId() + " not found"));

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setTable(table);
        order.setStatus(OrderStatus.NEW);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.items()) {
            DishEntity dish = dishRepository.findById(itemRequest.dishId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Dish with id " + itemRequest.dishId() + " not found"));

            OrderItemEntity item = new OrderItemEntity();
            item.setOrder(order);
            item.setDish(dish);
            item.setQuantity(itemRequest.quantity());
            item.setPrice(dish.getPrice());
            order.getItems().add(item);

            total = total.add(dish.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
        }
        order.setTotalPrice(total);

        OrderEntity saved = orderRepository.save(order);
        orderNotificationProducer.sendOrderCreated(toNotification(saved));
        return toResponse(saved);
    }

    private OrderNotification toNotification(OrderEntity order) {
        List<OrderItemNotification> items = order.getItems().stream()
                .map(item -> new OrderItemNotification(item.getDish().getName(), item.getQuantity()))
                .toList();
        return new OrderNotification(
                order.getId(),
                order.getTable().getTableNumber(),
                order.getStatus().name(),
                order.getCreatedAt(),
                items);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findMyOrders(String username) {
        return orderRepository.findAllByUserUsernameOrderByCreatedAtDesc(username).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus status) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));
        order.setStatus(status);
        return toResponse(orderRepository.save(order));
    }

    private OrderResponse toResponse(OrderEntity order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getDish().getId(),
                        item.getDish().getName(),
                        item.getQuantity(),
                        item.getPrice()))
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getTable().getId(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                items);
    }
}
