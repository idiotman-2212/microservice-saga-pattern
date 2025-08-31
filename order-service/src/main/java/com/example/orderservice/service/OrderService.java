package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderItem;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.saga.OrderSaga;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderSaga orderSaga;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        List<OrderItem> orderItems = request.getItems().stream()
                .map(item -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProductId(item.getProductId());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setPrice(item.getPrice());
                    orderItem.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    return orderItem;
                })
                .collect(Collectors.toList());

        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        order = orderRepository.save(order);
        
        // Start the saga
        orderSaga.startSaga(order);
        
        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
