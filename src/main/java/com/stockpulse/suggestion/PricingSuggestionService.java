package com.stockpulse.suggestion;

import com.stockpulse.product.Product;
import com.stockpulse.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingSuggestionService {

    private final PricingSuggestionRepository pricingSuggestionRepository;
    private final ProductRepository productRepository;

    public List<PricingSuggestion> getAllPricingSuggestions() {
        return pricingSuggestionRepository.findAll();
    }

    public PricingSuggestion getPricingSuggestionById(String id) {
        return pricingSuggestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pricing suggestion not found: " + id));
    }

    public PricingSuggestion updatePricingSuggestionStatus(String id, PricingSuggestionStatusUpdateRequest request) {
        PricingSuggestion pricingSuggestion = getPricingSuggestionById(id);

        // Validate that only ACCEPTED or REJECTED statuses are allowed for updates
        if (request.getStatus() != SuggestionStatus.ACCEPTED && request.getStatus() != SuggestionStatus.REJECTED) {
            throw new IllegalArgumentException("Status can only be updated to ACCEPTED or REJECTED");
        }

        // If the suggestion is being accepted, update the product price
        if (request.getStatus() == SuggestionStatus.ACCEPTED) {
            Product product = productRepository.findById(pricingSuggestion.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + pricingSuggestion.getProductId()));

            product.setCurrentPrice(pricingSuggestion.getRecommendedPrice());
            productRepository.save(product);
        }

        // Update the suggestion status
        pricingSuggestion.setStatus(request.getStatus());
        return pricingSuggestionRepository.save(pricingSuggestion);
    }
}