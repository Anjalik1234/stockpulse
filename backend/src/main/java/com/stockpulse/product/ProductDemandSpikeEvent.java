package com.stockpulse.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDemandSpikeEvent {
    private String productId;
    private Integer previousDemandVelocity;
    private Integer currentDemandVelocity;
    private Integer categoryAverageDemandVelocity;
}