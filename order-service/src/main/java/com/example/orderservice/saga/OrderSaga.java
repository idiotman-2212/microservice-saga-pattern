package com.example.orderservice.saga;

import com.example.orderservice.event.OrderCreatedEvent;
import com.example.orderservice.event.OrderItemEvent;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderItem;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSaga {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.order-created}")
    private String orderCreatedTopic;

    @Value("${kafka.topics.saga-rollback}")
    private String sagaRollbackTopic;

    public void startSaga(Order order) {
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(order.getId());
        event.setCustomerId(order.getCustomerId());
        event.setItems(order.getItems().stream()
                .map(this::mapToOrderItemEvent)
                .collect(Collectors.toList()));

        kafkaTemplate.send(orderCreatedTopic, event);
    }

    @KafkaListener(topics = "${kafka.topics.payment-completed}", containerFactory = "orderEventKafkaListenerContainerFactory")
    public void handlePaymentCompleted(com.example.orderservice.event.OrderCreatedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.INVENTORY_PENDING);
        orderRepository.save(order);
    }

    @KafkaListener(topics = "${kafka.topics.payment-failed}", containerFactory = "longKafkaListenerContainerFactory")
    public void handlePaymentFailed(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.PAYMENT_FAILED);
        orderRepository.save(order);
    }

    @KafkaListener(topics = "${kafka.topics.inventory-updated}", containerFactory = "longKafkaListenerContainerFactory")
    public void handleInventoryUpdated(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);
    }

    @KafkaListener(topics = "${kafka.topics.inventory-failed}", containerFactory = "longKafkaListenerContainerFactory")
    public void handleInventoryFailed(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.INVENTORY_FAILED);
        orderRepository.save(order);
        
        // Create rollback event with full order details
        OrderCreatedEvent rollbackEvent = new OrderCreatedEvent();
        rollbackEvent.setOrderId(order.getId());
        rollbackEvent.setCustomerId(order.getCustomerId());
        
        // Fetch order items in a separate query to avoid LazyInitializationException
        Order orderWithItems = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        rollbackEvent.setItems(orderWithItems.getItems().stream()
                .map(this::mapToOrderItemEvent)
                .collect(Collectors.toList()));
        
        // Trigger compensation transaction with full event data
        kafkaTemplate.send(sagaRollbackTopic, rollbackEvent);
    }


    private OrderItemEvent mapToOrderItemEvent(OrderItem orderItem) {
        return new OrderItemEvent(
                orderItem.getProductId(),
                orderItem.getQuantity(),
                orderItem.getPrice()
        );
    }
}
