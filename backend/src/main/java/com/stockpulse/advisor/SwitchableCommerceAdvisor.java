package com.stockpulse.advisor;

import com.stockpulse.product.Product;
import com.stockpulse.suggestion.PricingSuggestion;
import com.stockpulse.suggestion.ReorderSuggestion;
import com.stockpulse.suggestion.SuggestionTriggerReason;
import org.springframework.stereotype.Component;

@Component
public class SwitchableCommerceAdvisor implements CommerceAdvisor {

    private final RuleBasedCommerceAdvisor ruleBasedCommerceAdvisor;
    private final com.stockpulse.ai.AICommerceAdvisor aiCommerceAdvisor;

    private volatile String strategy = "AI";

    public SwitchableCommerceAdvisor(
            RuleBasedCommerceAdvisor ruleBasedCommerceAdvisor,
            com.stockpulse.ai.AICommerceAdvisor aiCommerceAdvisor) {

        this.ruleBasedCommerceAdvisor = ruleBasedCommerceAdvisor;
        this.aiCommerceAdvisor = aiCommerceAdvisor;
    }

    @Override
    public PricingSuggestion suggestPricing(
            Product product,
            SuggestionTriggerReason triggerReason) {

        if ("RULE".equalsIgnoreCase(strategy)) {
            return ruleBasedCommerceAdvisor.suggestPricing(product, triggerReason);
        }

        return aiCommerceAdvisor.suggestPricing(product, triggerReason);
    }

    @Override
    public ReorderSuggestion suggestReorder(
            Product product,
            SuggestionTriggerReason triggerReason) {

        if ("RULE".equalsIgnoreCase(strategy)) {
            return ruleBasedCommerceAdvisor.suggestReorder(product, triggerReason);
        }

        return aiCommerceAdvisor.suggestReorder(product, triggerReason);
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {

        if (!"AI".equalsIgnoreCase(strategy)
                && !"RULE".equalsIgnoreCase(strategy)) {

            throw new IllegalArgumentException(
                    "Strategy must be AI or RULE");
        }

        this.strategy = strategy.toUpperCase();
    }
}