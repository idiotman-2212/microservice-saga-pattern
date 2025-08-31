package com.example.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.order-created}")
    private String orderCreatedTopic;

    @Value("${kafka.topics.payment-completed}")
    private String paymentCompletedTopic;

    @Value("${kafka.topics.payment-failed}")
    private String paymentFailedTopic;

    @Value("${kafka.topics.inventory-updated}")
    private String inventoryUpdatedTopic;

    @Value("${kafka.topics.inventory-failed}")
    private String inventoryFailedTopic;

    @Value("${kafka.topics.saga-rollback}")
    private String sagaRollbackTopic;

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(orderCreatedTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentCompletedTopic() {
        return TopicBuilder.name(paymentCompletedTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentFailedTopic() {
        return TopicBuilder.name(paymentFailedTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic inventoryUpdatedTopic() {
        return TopicBuilder.name(inventoryUpdatedTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic inventoryFailedTopic() {
        return TopicBuilder.name(inventoryFailedTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic sagaRollbackTopic() {
        return TopicBuilder.name(sagaRollbackTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
