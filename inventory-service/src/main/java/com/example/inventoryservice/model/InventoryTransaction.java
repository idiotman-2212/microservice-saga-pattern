package com.example.inventoryservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime transactionDate;
    
    @Enumerated(EnumType.STRING)
    private TransactionType type;
}
