package com.stockpulse.suggestion;

import com.stockpulse.product.ProductDemandSpikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DemandSpikeEventSubscriber {

    private final DemandSpikeEventListener demandSpikeEventListener;

    @EventListener
    public void handleDemandSpikeEvent(ProductDemandSpikeEvent event) {
        demandSpikeEventListener.handleDemandSpikeEvent(event);
    }
}