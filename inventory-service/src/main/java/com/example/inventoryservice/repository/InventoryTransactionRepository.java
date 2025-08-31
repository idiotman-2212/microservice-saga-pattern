package com.example.inventoryservice.repository;

import com.example.inventoryservice.model.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
}
