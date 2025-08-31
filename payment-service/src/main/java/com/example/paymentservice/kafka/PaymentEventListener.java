package com.example.paymentservice.kafka;

import com.example.paymentservice.event.OrderCreatedEvent;
import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final PaymentService paymentService;

    @KafkaListener(topics = "${kafka.topics.order-created}")
    public void handleOrderCreated(OrderCreatedEvent event) {
        paymentService.processPayment(event);
    }

    @KafkaListener(topics = "${kafka.topics.saga-rollback}")
    public void handleSagaRollback(Long orderId) {
        // Implement compensation logic if needed
    }
}
