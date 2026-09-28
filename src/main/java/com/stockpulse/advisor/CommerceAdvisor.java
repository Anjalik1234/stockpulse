package com.stockpulse.advisor;

import com.stockpulse.product.Product;
import com.stockpulse.suggestion.PricingSuggestion;
import com.stockpulse.suggestion.ReorderSuggestion;
import com.stockpulse.suggestion.SuggestionTriggerReason;

public interface CommerceAdvisor {
    PricingSuggestion suggestPricing(Product product, SuggestionTriggerReason triggerReason);
    ReorderSuggestion suggestReorder(Product product, SuggestionTriggerReason triggerReason);
}