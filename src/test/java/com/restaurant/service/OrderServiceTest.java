package com.restaurant.service;

import com.restaurant.dto.OrderItemRequest;
import com.restaurant.dto.OrderRequest;
import com.restaurant.dto.OrderResponse;
import com.restaurant.entity.DishEntity;
import com.restaurant.entity.OrderEntity;
import com.restaurant.entity.TableEntity;
import com.restaurant.entity.UserEntity;
import com.restaurant.enums.OrderStatus;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.repository.DishRepository;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.TableRepository;
import com.restaurant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TableRepository tableRepository;
    @Mock
    private DishRepository dishRepository;
    @Mock
    private OrderNotificationProducer orderNotificationProducer;

    @InjectMocks
    private OrderService orderService;

    private UserEntity user;
    private TableEntity table;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setUsername("daniel");

        table = new TableEntity();
        table.setId(5L);
        table.setTableNumber(1);
        table.setSeats(4);
    }

    private DishEntity dish(long id, String name, String price) {
        DishEntity dish = new DishEntity();
        dish.setId(id);
        dish.setName(name);
        dish.setPrice(new BigDecimal(price));
        return dish;
    }

    @Test
    void createOrderComputesTotalPrice() {
        when(userRepository.findByUsername("daniel")).thenReturn(Optional.of(user));
        when(tableRepository.findById(5L)).thenReturn(Optional.of(table));
        when(dishRepository.findById(1L)).thenReturn(Optional.of(dish(1L, "Pierogi", "10.50")));
        when(dishRepository.findById(2L)).thenReturn(Optional.of(dish(2L, "Kompot", "5.00")));
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse response = orderService.createOrder("daniel", new OrderRequest(5L, List.of(
                new OrderItemRequest(1L, 2),
                new OrderItemRequest(2L, 1))));

        assertEquals(new BigDecimal("26.00"), response.totalPrice());
        assertEquals(OrderStatus.NEW, response.status());
        assertEquals(5L, response.tableId());
        assertEquals(2, response.items().size());
        assertEquals("Pierogi", response.items().get(0).dishName());
        verify(orderNotificationProducer).sendOrderCreated(any());
    }

    @Test
    void createOrderThrowsWhenDishMissing() {
        when(userRepository.findByUsername("daniel")).thenReturn(Optional.of(user));
        when(tableRepository.findById(5L)).thenReturn(Optional.of(table));
        when(dishRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.createOrder("daniel", new OrderRequest(5L, List.of(
                        new OrderItemRequest(99L, 1)))));
        verify(orderRepository, never()).save(any());
        verify(orderNotificationProducer, never()).sendOrderCreated(any());
    }

    @Test
    void createOrderThrowsWhenTableMissing() {
        when(userRepository.findByUsername("daniel")).thenReturn(Optional.of(user));
        when(tableRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.createOrder("daniel", new OrderRequest(5L, List.of(
                        new OrderItemRequest(1L, 1)))));
    }

    @Test
    void updateStatusChangesStatus() {
        OrderEntity order = new OrderEntity();
        order.setTable(table);
        order.setStatus(OrderStatus.NEW);
        order.setTotalPrice(BigDecimal.TEN);
        when(orderRepository.findById(7L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse response = orderService.updateStatus(7L, OrderStatus.IN_PROGRESS);

        assertEquals(OrderStatus.IN_PROGRESS, response.status());
    }

    @Test
    void updateStatusThrowsWhenOrderMissing() {
        when(orderRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.updateStatus(7L, OrderStatus.READY));
    }
}
