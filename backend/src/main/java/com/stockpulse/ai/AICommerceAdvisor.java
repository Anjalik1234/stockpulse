package com.stockpulse.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockpulse.advisor.CommerceAdvisor;
import com.stockpulse.advisor.RuleBasedCommerceAdvisor;
import com.stockpulse.product.Category;
import com.stockpulse.product.Product;
import com.stockpulse.suggestion.PricingDirection;
import com.stockpulse.suggestion.PricingSuggestion;
import com.stockpulse.suggestion.ReorderSuggestion;
import com.stockpulse.suggestion.SuggestionStatus;
import com.stockpulse.suggestion.SuggestionTriggerReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AICommerceAdvisor implements CommerceAdvisor {

    private static final Logger log =
            LoggerFactory.getLogger(AICommerceAdvisor.class);

    private final LLMGateway llmGateway;
    private final RuleBasedCommerceAdvisor ruleBasedCommerceAdvisor;
    private final ObjectMapper objectMapper;

    public AICommerceAdvisor(
            LLMGateway llmGateway,
            RuleBasedCommerceAdvisor ruleBasedCommerceAdvisor) {

        this.llmGateway = llmGateway;
        this.ruleBasedCommerceAdvisor = ruleBasedCommerceAdvisor;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public PricingSuggestion suggestPricing(
            Product product,
            SuggestionTriggerReason triggerReason) {

        try {
            String prompt = buildPricingPrompt(product, triggerReason);

            String response = llmGateway.callLLM(prompt);

            return parsePricingResponse(
                    product,
                    triggerReason,
                    response
            );

        } catch (Exception e) {

            log.warn(
                    "AI pricing failed for product {}. Falling back to rule-based advisor: {}",
                    product.getId(),
                    e.getMessage()
            );

            return ruleBasedCommerceAdvisor.suggestPricing(
                    product,
                    triggerReason
            );
        }
    }

    @Override
    public ReorderSuggestion suggestReorder(
            Product product,
            SuggestionTriggerReason triggerReason) {

        try {
            String prompt = buildReorderPrompt(product, triggerReason);

            String response = llmGateway.callLLM(prompt);

            return parseReorderResponse(
                    product,
                    triggerReason,
                    response
            );

        } catch (Exception e) {

            log.warn(
                    "AI reorder failed for product {}. Falling back to rule-based advisor: {}",
                    product.getId(),
                    e.getMessage()
            );

            return ruleBasedCommerceAdvisor.suggestReorder(
                    product,
                    triggerReason
            );
        }
    }

    private String buildPricingPrompt(
            Product product,
            SuggestionTriggerReason triggerReason) {

        int categoryAverage =
                getCategoryAverageDemandVelocity(product.getCategory());

        String context;

        if (triggerReason == SuggestionTriggerReason.INVENTORY_LOW) {

            context =
                    "This is an INVENTORY_LOW trigger. " +
                    "Stock is below the reorder threshold. " +
                    "Consider the merchandising tradeoff between increasing " +
                    "price to protect limited inventory/revenue and keeping " +
                    "the product attractive to customers.";

        } else if (triggerReason == SuggestionTriggerReason.DEMAND_SPIKE) {

            context =
                    "This is a DEMAND_SPIKE trigger. " +
                    "Demand velocity is unusually high compared with the " +
                    "category average. Consider whether pricing should respond " +
                    "to increased demand while avoiding an excessive price change.";

        } else {

            context =
                    "This is a general pricing recommendation. " +
                    "Consider current inventory and demand conditions.";
        }

        return """
                You are a commerce pricing advisor.

                Product:
                Name: %s
                Category: %s
                Current Price: %.2f
                Current Stock: %d
                Reorder Threshold: %d
                Demand Velocity: %d
                Category Average Demand Velocity: %d
                Trigger: %s

                Trigger context:
                %s

                Return ONLY valid JSON. Do not include markdown.

                Required format:
                {
                  "recommendedPrice": 29.99,
                  "direction": "INCREASE",
                  "confidence": 0.82,
                  "reasoning": "Explain the recommendation."
                }

                Rules:
                - recommendedPrice must be greater than 0
                - direction must be INCREASE, DECREASE, or HOLD
                - confidence must be between 0 and 1
                - reasoning must not be empty
                - Keep recommendedPrice within 0.1x and 10x of current price.
                """.formatted(
                product.getName(),
                product.getCategory(),
                product.getCurrentPrice(),
                product.getStockLevel(),
                product.getReorderThreshold(),
                product.getDemandVelocity(),
                categoryAverage,
                triggerReason,
                context
        );
    }

    private String buildReorderPrompt(
            Product product,
            SuggestionTriggerReason triggerReason) {

        int categoryAverage =
                getCategoryAverageDemandVelocity(product.getCategory());

        String context;

        if (triggerReason == SuggestionTriggerReason.INVENTORY_LOW) {

            context =
                    "This is an INVENTORY_LOW trigger. " +
                    "Stock is below the reorder threshold. " +
                    "Focus on replenishing enough inventory to avoid stockout.";

        } else if (triggerReason == SuggestionTriggerReason.DEMAND_SPIKE) {

            context =
                    "This is a DEMAND_SPIKE trigger. " +
                    "Demand velocity is significantly higher than the " +
                    "category average. Consider the risk of future stock depletion " +
                    "and recommend sufficient replenishment.";

        } else {

            context =
                    "This is a general replenishment recommendation. " +
                    "Consider current stock, reorder threshold and demand.";
        }

        return """
                You are a supply chain and inventory advisor.

                Product:
                Name: %s
                Category: %s
                Current Price: %.2f
                Current Stock: %d
                Reorder Threshold: %d
                Demand Velocity: %d
                Category Average Demand Velocity: %d
                Trigger: %s

                Trigger context:
                %s

                Return ONLY valid JSON. Do not include markdown.

                Required format:
                {
                  "recommendedQuantity": 150,
                  "confidence": 0.78,
                  "reasoning": "Explain the replenishment recommendation."
                }

                Rules:
                - recommendedQuantity must be a positive integer
                - confidence must be between 0 and 1
                - reasoning must not be empty
                """.formatted(
                product.getName(),
                product.getCategory(),
                product.getCurrentPrice(),
                product.getStockLevel(),
                product.getReorderThreshold(),
                product.getDemandVelocity(),
                categoryAverage,
                triggerReason,
                context
        );
    }

    private PricingSuggestion parsePricingResponse(
            Product product,
            SuggestionTriggerReason triggerReason,
            String response) {

        try {

            String json = extractJson(response);

            JsonNode root = objectMapper.readTree(json);

            if (!root.has("recommendedPrice")
                    || !root.has("direction")
                    || !root.has("confidence")
                    || !root.has("reasoning")) {

                throw new IllegalArgumentException(
                        "Missing required pricing fields"
                );
            }

            double recommendedPrice =
                    root.get("recommendedPrice").asDouble();

            String directionValue =
                    root.get("direction").asText();

            double confidence =
                    root.get("confidence").asDouble();

            String reasoning =
                    root.get("reasoning").asText();

            if (recommendedPrice <= 0) {
                throw new IllegalArgumentException(
                        "Recommended price must be positive"
                );
            }

            double currentPrice = product.getCurrentPrice();

            if (recommendedPrice < currentPrice * 0.1
                    || recommendedPrice > currentPrice * 10) {

                throw new IllegalArgumentException(
                        "Recommended price outside allowed bounds"
                );
            }

            if (confidence < 0 || confidence > 1) {
                throw new IllegalArgumentException(
                        "Confidence must be between 0 and 1"
                );
            }

            if (reasoning == null || reasoning.isBlank()) {
                throw new IllegalArgumentException(
                        "Reasoning cannot be empty"
                );
            }

            PricingDirection direction;

            try {

                direction =
                        PricingDirection.valueOf(
                                directionValue.toUpperCase()
                        );

            } catch (IllegalArgumentException e) {

                throw new IllegalArgumentException(
                        "Invalid pricing direction: " + directionValue
                );
            }

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

        } catch (Exception e) {

            log.warn(
                    "Invalid AI pricing response. Falling back to rule-based advisor: {}",
                    e.getMessage()
            );

            return ruleBasedCommerceAdvisor.suggestPricing(
                    product,
                    triggerReason
            );
        }
    }

    private ReorderSuggestion parseReorderResponse(
            Product product,
            SuggestionTriggerReason triggerReason,
            String response) {

        try {

            String json = extractJson(response);

            JsonNode root = objectMapper.readTree(json);

            if (!root.has("recommendedQuantity")
                    || !root.has("confidence")
                    || !root.has("reasoning")) {

                throw new IllegalArgumentException(
                        "Missing required reorder fields"
                );
            }

            int recommendedQuantity =
                    root.get("recommendedQuantity").asInt();

            double confidence =
                    root.get("confidence").asDouble();

            String reasoning =
                    root.get("reasoning").asText();

            if (recommendedQuantity <= 0) {
                throw new IllegalArgumentException(
                        "Recommended quantity must be positive"
                );
            }

            if (confidence < 0 || confidence > 1) {
                throw new IllegalArgumentException(
                        "Confidence must be between 0 and 1"
                );
            }

            if (reasoning == null || reasoning.isBlank()) {
                throw new IllegalArgumentException(
                        "Reasoning cannot be empty"
                );
            }

            return ReorderSuggestion.builder()
                    .productId(product.getId())
                    .currentStock(product.getStockLevel())
                    .recommendedQuantity(recommendedQuantity)
                    .suggestedLeadTimeDays(7)
                    .confidence(confidence)
                    .reasoning(reasoning)
                    .status(SuggestionStatus.PENDING)
                    .triggerReason(triggerReason)
                    .build();

        } catch (Exception e) {

            log.warn(
                    "Invalid AI reorder response. Falling back to rule-based advisor: {}",
                    e.getMessage()
            );

            return ruleBasedCommerceAdvisor.suggestReorder(
                    product,
                    triggerReason
            );
        }
    }

    private String extractJson(String response) {

        if (response == null || response.isBlank()) {
            throw new IllegalArgumentException(
                    "Empty LLM response"
            );
        }

        String cleaned = response.trim();

        if (cleaned.startsWith("```")) {

            int firstNewLine = cleaned.indexOf('\n');

            if (firstNewLine != -1) {
                cleaned = cleaned.substring(firstNewLine + 1);
            }

            int closingFence = cleaned.lastIndexOf("```");

            if (closingFence != -1) {
                cleaned = cleaned.substring(0, closingFence);
            }
        }

        return cleaned.trim();
    }

    private int getCategoryAverageDemandVelocity(
            Category category) {

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