package com.stockpulse.product;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ProductEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public ProductEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishInventoryLowEvent(String productId, Integer previousStock, Integer currentStock, Integer reorderThreshold) {
        ProductInventoryLowEvent event = new ProductInventoryLowEvent(productId, previousStock, currentStock, reorderThreshold);
        eventPublisher.publishEvent(event);
    }

    public void publishDemandSpikeEvent(String productId, Integer previousDemandVelocity, Integer currentDemandVelocity, Integer categoryAverageDemandVelocity) {
        ProductDemandSpikeEvent event = new ProductDemandSpikeEvent(productId, previousDemandVelocity, currentDemandVelocity, categoryAverageDemandVelocity);
        eventPublisher.publishEvent(event);
    }
}