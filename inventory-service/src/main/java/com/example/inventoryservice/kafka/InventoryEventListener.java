package com.example.inventoryservice.kafka;

import com.example.inventoryservice.event.OrderCreatedEvent;
import com.example.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventListener {
    private final InventoryService inventoryService;

    @KafkaListener(topics = "${kafka.topics.payment-completed}")
    public void handlePaymentCompleted(OrderCreatedEvent event) {
        try {
            inventoryService.processOrder(event.getOrderId(), event.getItems());
        } catch (Exception e) {
            // Error handling is done in the service layer
            log.error("Error while processing order: {}", event.getOrderId(), e);
            throw new RuntimeException("Failed to process order: " + event.getOrderId(), e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.saga-rollback}")
    public void handleSagaRollback(OrderCreatedEvent event) {
        try {
            inventoryService.rollbackOrder(event.getOrderId(), event.getItems());
        } catch (Exception e) {
            log.error("Error while rolling back order: {}", event.getOrderId(), e);
            throw new RuntimeException("Failed to rollback order: " + event.getOrderId(), e);
        }
    }
}
