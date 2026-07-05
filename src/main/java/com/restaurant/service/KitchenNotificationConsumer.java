package com.restaurant.service;

import com.restaurant.config.KafkaConfig;
import com.restaurant.dto.OrderNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KitchenNotificationConsumer {

    public static final String KITCHEN_WEBSOCKET_DESTINATION = "/topic/kitchen";

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = KafkaConfig.KITCHEN_ORDERS_TOPIC,
                   groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderCreated(OrderNotification notification) {
        log.info("Nowe zamowienie {} -> ekran kuchni", notification.orderId());
        messagingTemplate.convertAndSend(KITCHEN_WEBSOCKET_DESTINATION, notification);
    }
}
