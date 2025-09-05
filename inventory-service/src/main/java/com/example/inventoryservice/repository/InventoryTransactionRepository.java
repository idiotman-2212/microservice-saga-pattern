package com.example.inventoryservice.repository;

import com.example.inventoryservice.model.InventoryTransaction;
import com.example.inventoryservice.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    boolean existsByOrderIdAndType(Long orderId, TransactionType type);
}
