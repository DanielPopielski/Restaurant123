package com.restaurant.service;

import com.restaurant.config.KafkaConfig;
import com.restaurant.dto.OrderItemNotification;
import com.restaurant.dto.OrderNotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderNotificationProducerTest {

    @Mock
    private KafkaTemplate<String, OrderNotification> kafkaTemplate;

    @InjectMocks
    private OrderNotificationProducer producer;

    @Test
    void sendsNotificationToKitchenTopic() {
        when(kafkaTemplate.send(any(), any(), any(OrderNotification.class)))
                .thenReturn(CompletableFuture.completedFuture(null));
        OrderNotification notification = new OrderNotification(
                42L, 1, "NEW", Instant.now(),
                List.of(new OrderItemNotification("Pierogi", 2)));

        producer.sendOrderCreated(notification);

        verify(kafkaTemplate).send(eq(KafkaConfig.KITCHEN_ORDERS_TOPIC), eq("42"), eq(notification));
    }
}
