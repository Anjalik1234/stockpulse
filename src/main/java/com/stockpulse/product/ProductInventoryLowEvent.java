package com.stockpulse.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInventoryLowEvent {
    private String productId;
    private Integer previousStock;
    private Integer currentStock;
    private Integer reorderThreshold;
}