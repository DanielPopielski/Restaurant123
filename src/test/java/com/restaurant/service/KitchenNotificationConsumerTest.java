package com.restaurant.service;

import com.restaurant.dto.OrderItemNotification;
import com.restaurant.dto.OrderNotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KitchenNotificationConsumerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private KitchenNotificationConsumer consumer;

    @Test
    void forwardsNotificationToWebSocketTopic() {
        OrderNotification notification = new OrderNotification(
                42L, 1, "NEW", Instant.now(),
                List.of(new OrderItemNotification("Pierogi", 2)));

        consumer.onOrderCreated(notification);

        verify(messagingTemplate).convertAndSend(
                KitchenNotificationConsumer.KITCHEN_WEBSOCKET_DESTINATION, notification);
    }
}
