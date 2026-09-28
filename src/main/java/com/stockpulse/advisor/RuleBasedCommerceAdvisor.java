package com.stockpulse.advisor;

import com.stockpulse.product.Category;
import com.stockpulse.product.Product;
import com.stockpulse.suggestion.PricingDirection;
import com.stockpulse.suggestion.PricingSuggestion;
import com.stockpulse.suggestion.ReorderSuggestion;
import com.stockpulse.suggestion.SuggestionStatus;
import com.stockpulse.suggestion.SuggestionTriggerReason;
import org.springframework.stereotype.Component;

@Component
public class RuleBasedCommerceAdvisor implements CommerceAdvisor {

    @Override
    public PricingSuggestion suggestPricing(Product product, SuggestionTriggerReason triggerReason) {
        double recommendedPrice;
        PricingDirection direction;
        double confidence;
        String reasoning;

        // Rule-based pricing logic
        if (product.getStockLevel() < product.getReorderThreshold()) {
            // If stock is below reorder threshold, increase price
            recommendedPrice = product.getCurrentPrice() * 1.10;
            direction = PricingDirection.INCREASE;
            confidence = 0.9;
            reasoning = "Stock level (" + product.getStockLevel() + ") is below reorder threshold (" + 
                       product.getReorderThreshold() + "). Increasing price by 10% to optimize revenue.";
        } else if (product.getDemandVelocity() > getCategoryAverageDemandVelocity(product.getCategory()) * 2) {
            // If demand velocity is more than twice the category average, increase price
            recommendedPrice = product.getCurrentPrice() * 1.05;
            direction = PricingDirection.INCREASE;
            confidence = 0.8;
            reasoning = "Demand velocity (" + product.getDemandVelocity() + ") is more than twice the category average (" + 
                       (getCategoryAverageDemandVelocity(product.getCategory()) * 2) + "). Increasing price by 5% to capitalize on high demand.";
        } else {
            // Otherwise, hold the current price
            recommendedPrice = product.getCurrentPrice();
            direction = PricingDirection.HOLD;
            confidence = 0.7;
            reasoning = "No significant factors affecting pricing. Maintaining current price.";
        }

        // Create and return the pricing suggestion (not saved to database)
        return PricingSuggestion.builder()
                .productId(product.getId())
                .currentPrice(product.getCurrentPrice())
                .recommendedPrice(recommendedPrice)
                .direction(direction)
                .confidence(confidence)
                .reasoning(reasoning)
                .status(SuggestionStatus.PENDING)
                .triggerReason(triggerReason)
                .build();
    }

    @Override
    public ReorderSuggestion suggestReorder(Product product, SuggestionTriggerReason triggerReason) {
        // Rule-based reorder logic
        int recommendedQuantity = Math.max((product.getReorderThreshold() * 3) - product.getStockLevel(), 1);
        int suggestedLeadTimeDays = 7;
        double confidence = 0.9;
        String reasoning = "Current stock (" + product.getStockLevel() + ") compared to reorder threshold (" + 
                          product.getReorderThreshold() + "). Recommended quantity calculated as max((reorderThreshold * 3) - currentStock, 1) = " + 
                          recommendedQuantity + ".";

        // Create and return the reorder suggestion (not saved to database)
        return ReorderSuggestion.builder()
                .productId(product.getId())
                .currentStock(product.getStockLevel())
                .recommendedQuantity(recommendedQuantity)
                .suggestedLeadTimeDays(suggestedLeadTimeDays)
                .confidence(confidence)
                .reasoning(reasoning)
                .status(SuggestionStatus.PENDING)
                .triggerReason(triggerReason)
                .build();
    }

    /**
     * Placeholder method for category average demand velocity.
     * In a real implementation, this would query historical data or use configured values.
     */
    private int getCategoryAverageDemandVelocity(Category category) {
        // For now, return a fixed value for demonstration purposes
        // In a real implementation, this would calculate actual averages based on category
        switch (category) {
            case ELECTRONICS:
                return 15;
            case APPAREL:
                return 20;
            case HOME:
                return 10;
            default:
                return 10;
        }
    }
}