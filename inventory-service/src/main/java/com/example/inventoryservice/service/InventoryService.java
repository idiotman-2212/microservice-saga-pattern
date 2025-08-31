package com.example.inventoryservice.service;

import com.example.inventoryservice.event.OrderItemEvent;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.model.InventoryTransaction;
import com.example.inventoryservice.model.TransactionType;
import com.example.inventoryservice.repository.InventoryRepository;
import com.example.inventoryservice.repository.InventoryTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.inventory-updated}")
    private String inventoryUpdatedTopic;

    @Value("${kafka.topics.inventory-failed}")
    private String inventoryFailedTopic;

    @Transactional
    public void processOrder(Long orderId, List<OrderItemEvent> items) {
        try {
            for (OrderItemEvent item : items) {
                updateInventory(orderId, item.getProductId(), item.getQuantity());
            }
            // Notify successful inventory update
            kafkaTemplate.send(inventoryUpdatedTopic, orderId);
        } catch (Exception e) {
            // Notify inventory update failure
            kafkaTemplate.send(inventoryFailedTopic, orderId);
            throw e;
        }
    }

    @Transactional
    public void updateInventory(Long orderId, Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new RuntimeException("Product not found in inventory"));

        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient inventory for product: " + productId);
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);

        // Record transaction
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setOrderId(orderId);
        transaction.setProductId(productId);
        transaction.setQuantity(quantity);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setType(TransactionType.STOCK_OUT);
        transactionRepository.save(transaction);
    }

    @Transactional
    public void rollbackOrder(Long orderId, List<OrderItemEvent> items) {
        for (OrderItemEvent item : items) {
            Inventory inventory = inventoryRepository.findByProductIdWithLock(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found in inventory"));

            inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
            inventoryRepository.save(inventory);

            // Record rollback transaction
            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setOrderId(orderId);
            transaction.setProductId(item.getProductId());
            transaction.setQuantity(item.getQuantity());
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setType(TransactionType.ROLLBACK);
            transactionRepository.save(transaction);
        }
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Inventory getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
    }

    @Transactional
    public Inventory createInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public Inventory updateInventoryQuantity(Long productId, Integer quantity) {
        Inventory inventory = getInventoryByProductId(productId);
        inventory.setQuantity(quantity);
        return inventoryRepository.save(inventory);
    }

    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }
}
