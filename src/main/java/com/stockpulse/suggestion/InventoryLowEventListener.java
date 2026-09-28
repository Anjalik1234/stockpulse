package com.stockpulse.suggestion;

import com.stockpulse.advisor.SwitchableCommerceAdvisor;
import com.stockpulse.product.Product;
import com.stockpulse.product.ProductRepository;
import com.stockpulse.product.ProductInventoryLowEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryLowEventListener {

    private final ProductRepository productRepository;
    private final PricingSuggestionRepository pricingSuggestionRepository;
    private final ReorderSuggestionRepository reorderSuggestionRepository;
    private final SwitchableCommerceAdvisor commerceAdvisor;

    @Async
    @Transactional
    public void handleInventoryLowEvent(ProductInventoryLowEvent event) {
        log.info("Received inventory low event for product ID: {}", event.getProductId());

        // Load the product
        Optional<Product> productOpt = productRepository.findById(event.getProductId());
        
        if (productOpt.isEmpty()) {
            log.warn("Product not found for inventory low event: {}", event.getProductId());
            return;
        }

        Product product = productOpt.get();

        // Check for existing pending pricing suggestion
        List<PricingSuggestion> existingPricingSuggestions = pricingSuggestionRepository
                .findByProductIdAndStatusAndTriggerReason(
                        product.getId(),
                        SuggestionStatus.PENDING,
                        SuggestionTriggerReason.INVENTORY_LOW
                );

        if (!existingPricingSuggestions.isEmpty()) {
            log.info("Skipping pricing suggestion creation - pending suggestion already exists for product ID: {}", product.getId());
        } else {
            // Create pricing suggestion using the advisor
            PricingSuggestion pricingSuggestion = commerceAdvisor.suggestPricing(product, SuggestionTriggerReason.INVENTORY_LOW);
            pricingSuggestionRepository.save(pricingSuggestion);
            log.info("Created pricing suggestion ID: {} for product ID: {}", pricingSuggestion.getId(), product.getId());
        }

        // Check for existing pending reorder suggestion
        List<ReorderSuggestion> existingReorderSuggestions = reorderSuggestionRepository
                .findByProductIdAndStatusAndTriggerReason(
                        product.getId(),
                        SuggestionStatus.PENDING,
                        SuggestionTriggerReason.INVENTORY_LOW
                );

        if (!existingReorderSuggestions.isEmpty()) {
            log.info("Skipping reorder suggestion creation - pending suggestion already exists for product ID: {}", product.getId());
        } else {
            // Create reorder suggestion using the advisor
            ReorderSuggestion reorderSuggestion = commerceAdvisor.suggestReorder(product, SuggestionTriggerReason.INVENTORY_LOW);
            reorderSuggestionRepository.save(reorderSuggestion);
            log.info("Created reorder suggestion ID: {} for product ID: {}", reorderSuggestion.getId(), product.getId());
        }
    }
}