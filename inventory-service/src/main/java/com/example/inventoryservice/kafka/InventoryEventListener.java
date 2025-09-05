package com.example.inventoryservice.kafka;

import com.example.inventoryservice.event.OrderCreatedEvent;
import com.example.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventListener {
    private final InventoryService inventoryService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.inventory-failed}")
    private String inventoryFailedTopic;

    @KafkaListener(topics = "${kafka.topics.payment-completed}")
    public void handlePaymentCompleted(OrderCreatedEvent event) {
        try {
            log.info("Processing order after payment completed: {}", event.getOrderId());
            inventoryService.processOrder(event.getOrderId(), event.getItems());
        } catch (Exception e) {
            log.error("Error while processing order: {}", event.getOrderId(), e);
            // Send inventory.failed event to trigger saga rollback
            kafkaTemplate.send(inventoryFailedTopic, event.getOrderId());
        }
    }

    @KafkaListener(topics = "${kafka.topics.saga-rollback}")
    public void handleSagaRollback(OrderCreatedEvent event) {
        try {
            log.info("Rolling back order: {}", event.getOrderId());
            inventoryService.rollbackOrder(event.getOrderId(), event.getItems());
        } catch (Exception e) {
            log.error("Error while rolling back order: {}", event.getOrderId(), e);
            throw new RuntimeException("Failed to rollback order: " + event.getOrderId(), e);
        }
    }
}
