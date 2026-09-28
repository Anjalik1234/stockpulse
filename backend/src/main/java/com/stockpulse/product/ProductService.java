package com.stockpulse.product;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductEventPublisher productEventPublisher;

    public ProductService(ProductRepository productRepository,
                          ProductEventPublisher productEventPublisher) {
        this.productRepository = productRepository;
        this.productEventPublisher = productEventPublisher;
    }

    public Product createProduct(Product product) {

        if (product.getId() == null || product.getId().isBlank()) {
            product.setId(UUID.randomUUID().toString());
        }

        if (product.getStatus() == null) {
            product.setStatus(
                    product.getStockLevel() != null && product.getStockLevel() == 0
                            ? ProductStatus.OUT_OF_STOCK
                            : ProductStatus.ACTIVE
            );
        }

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(String id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found: " + id)
                );
    }

    public Product updateProductStock(String id, ProductStockUpdateRequest request) {

        Product product = getProduct(id);

        // Store previous stock level for inventory-low event detection
        Integer previousStock = product.getStockLevel();

        // Update stock level
        product.setStockLevel(request.getStockLevel());

        // Update status based on stock level
        if (request.getStockLevel() == 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        } else if (request.getStockLevel() > 0
                && product.getStatus() == ProductStatus.OUT_OF_STOCK) {
            product.setStatus(ProductStatus.ACTIVE);
        }

        // Save the product
        Product updatedProduct = productRepository.save(product);

        // Detect inventory crossing below reorder threshold
        if (previousStock != null
                && updatedProduct.getStockLevel() != null
                && updatedProduct.getReorderThreshold() != null
                && previousStock >= updatedProduct.getReorderThreshold()
                && updatedProduct.getStockLevel() < updatedProduct.getReorderThreshold()) {

            productEventPublisher.publishInventoryLowEvent(
                    updatedProduct.getId(),
                    previousStock,
                    updatedProduct.getStockLevel(),
                    updatedProduct.getReorderThreshold()
            );
        }

        return updatedProduct;
    }

    public Product processOrder(String id, OrderRequest request) {

        // Validate quantity
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be a positive integer"
            );
        }

        Product product = getProduct(id);

        // Store previous values for event detection
        Integer previousStock = product.getStockLevel();
        Integer previousDemandVelocity = product.getDemandVelocity();

        // Validate stock availability
        if (product.getStockLevel() < request.getQuantity()) {
            throw new IllegalStateException(
                    "Insufficient stock. Available: "
                            + product.getStockLevel()
                            + ", Requested: "
                            + request.getQuantity()
            );
        }

        // Decrease stock
        product.setStockLevel(
                product.getStockLevel() - request.getQuantity()
        );

        // Increase demand velocity
        product.setDemandVelocity(
                product.getDemandVelocity() + request.getQuantity()
        );

        // Update status if stock reaches zero
        if (product.getStockLevel() == 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        }

        // Save updated product
        Product updatedProduct = productRepository.save(product);

        /*
         * ============================================================
         * DEMAND SPIKE DETECTION
         * ============================================================
         *
         * Trigger only when demand crosses:
         *
         * <= 2 × category average
         *
         * to
         *
         * > 2 × category average
         *
         */

        int categoryAverageDemandVelocity =
                getCategoryAverageDemandVelocity(updatedProduct.getCategory());

        if (previousDemandVelocity != null
                && updatedProduct.getDemandVelocity() != null
                && previousDemandVelocity <= 2 * categoryAverageDemandVelocity
                && updatedProduct.getDemandVelocity() > 2 * categoryAverageDemandVelocity) {

            productEventPublisher.publishDemandSpikeEvent(
                    updatedProduct.getId(),
                    previousDemandVelocity,
                    updatedProduct.getDemandVelocity(),
                    categoryAverageDemandVelocity
            );
        }

        /*
         * ============================================================
         * INVENTORY LOW DETECTION
         * ============================================================
         *
         * Trigger when stock crosses from:
         *
         * >= reorder threshold
         *
         * to
         *
         * < reorder threshold
         *
         */

        if (previousStock != null
                && updatedProduct.getStockLevel() != null
                && updatedProduct.getReorderThreshold() != null
                && previousStock >= updatedProduct.getReorderThreshold()
                && updatedProduct.getStockLevel() < updatedProduct.getReorderThreshold()) {

            productEventPublisher.publishInventoryLowEvent(
                    updatedProduct.getId(),
                    previousStock,
                    updatedProduct.getStockLevel(),
                    updatedProduct.getReorderThreshold()
            );
        }

        return updatedProduct;
    }

    /**
     * Category demand averages used by both rule-based
     * and demand-spike detection logic.
     */
    private int getCategoryAverageDemandVelocity(Category category) {

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