package com.restaurant.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String KITCHEN_ORDERS_TOPIC = "kitchen-orders";

    @Bean
    public NewTopic kitchenOrdersTopic() {
        return TopicBuilder.name(KITCHEN_ORDERS_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
