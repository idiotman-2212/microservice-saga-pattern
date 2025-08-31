package com.example.paymentservice.service;

import com.example.paymentservice.event.OrderCreatedEvent;
import com.example.paymentservice.model.Payment;
import com.example.paymentservice.model.PaymentStatus;
import com.example.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.payment-completed}")
    private String paymentCompletedTopic;

    @Value("${kafka.topics.payment-failed}")
    private String paymentFailedTopic;

    public void processPayment(OrderCreatedEvent event) {
        Payment payment = new Payment();
        payment.setOrderId(event.getOrderId());
        payment.setCustomerId(event.getCustomerId());
        payment.setAmount(calculateTotalAmount(event));
        payment.setPaymentDate(LocalDateTime.now());

        try {
            // Simulate payment processing
            processPaymentWithExternalService(payment);
            
            payment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);
            
            // Notify successful payment
            kafkaTemplate.send(paymentCompletedTopic, event.getOrderId());
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            
            // Notify payment failure
            kafkaTemplate.send(paymentFailedTopic, event.getOrderId());
        }
    }

    private BigDecimal calculateTotalAmount(OrderCreatedEvent event) {
        return event.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void processPaymentWithExternalService(Payment payment) {
        // Simulate external payment service call
        // In real implementation, this would integrate with a payment gateway
        boolean simulateFailure = false; // Set to true to simulate payment failure
        if (simulateFailure) {
            throw new RuntimeException("Payment failed");
        }
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public void refundPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
    }
}
