package com.stockpulse.suggestion;

import com.stockpulse.product.ProductInventoryLowEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryLowEventSubscriber {

    private final InventoryLowEventListener inventoryLowEventListener;

    @EventListener
    public void handleInventoryLowEvent(ProductInventoryLowEvent event) {
        inventoryLowEventListener.handleInventoryLowEvent(event);
    }
}