package com.restaurant.service;

import com.restaurant.config.KafkaConfig;
import com.restaurant.dto.OrderNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderNotificationProducer {

    private final KafkaTemplate<String, OrderNotification> kafkaTemplate;

    public void sendOrderCreated(OrderNotification notification) {
        kafkaTemplate.send(KafkaConfig.KITCHEN_ORDERS_TOPIC,
                        String.valueOf(notification.orderId()), notification)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish notification for order {}: {}",
                                notification.orderId(), ex.getMessage());
                    }
                });
    }
}
