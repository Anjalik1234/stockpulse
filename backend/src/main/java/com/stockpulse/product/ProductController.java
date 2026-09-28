package com.stockpulse.product;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @GetMapping
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable String id) {
        return productService.getProduct(id);
    }

    @PatchMapping("/{id}/stock")
    public Product updateProductStock(@PathVariable String id, @RequestBody ProductStockUpdateRequest request) {
        return productService.updateProductStock(id, request);
    }

    @PostMapping("/{id}/orders")
    public Product processOrder(@PathVariable String id, @RequestBody OrderRequest request) {
        return productService.processOrder(id, request);
    }
}