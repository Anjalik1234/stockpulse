package com.stockpulse.suggestion;

import com.stockpulse.product.Product;
import com.stockpulse.product.ProductRepository;
import com.stockpulse.product.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReorderSuggestionService {

    private final ReorderSuggestionRepository reorderSuggestionRepository;
    private final ProductRepository productRepository;

    public List<ReorderSuggestion> getAllReorderSuggestions() {
        return reorderSuggestionRepository.findAll();
    }

    public ReorderSuggestion getReorderSuggestionById(String id) {
        return reorderSuggestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reorder suggestion not found: " + id));
    }

    public ReorderSuggestion updateReorderSuggestionStatus(String id, ReorderSuggestionStatusUpdateRequest request) {
        ReorderSuggestion reorderSuggestion = getReorderSuggestionById(id);

        // Validate that only ACCEPTED or REJECTED statuses are allowed for updates
        if (request.getStatus() != SuggestionStatus.ACCEPTED && request.getStatus() != SuggestionStatus.REJECTED) {
            throw new IllegalArgumentException("Status can only be updated to ACCEPTED or REJECTED");
        }

        // If the suggestion is being accepted, update the product stock
        if (request.getStatus() == SuggestionStatus.ACCEPTED) {
            Product product = productRepository.findById(reorderSuggestion.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + reorderSuggestion.getProductId()));

            // Increase stock level by the recommended quantity
            int newStockLevel = product.getStockLevel() + reorderSuggestion.getRecommendedQuantity();
            product.setStockLevel(newStockLevel);

            // If the product was OUT_OF_STOCK and now has stock, update its status
            if (product.getStatus() == ProductStatus.OUT_OF_STOCK && newStockLevel > 0) {
                product.setStatus(ProductStatus.ACTIVE);
            }

            productRepository.save(product);
        }

        // Update the suggestion status
        reorderSuggestion.setStatus(request.getStatus());
        return reorderSuggestionRepository.save(reorderSuggestion);
    }
}