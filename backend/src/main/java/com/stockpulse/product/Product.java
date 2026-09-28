package com.stockpulse.product;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private Double currentPrice;

    @Column(nullable = false)
    private Integer stockLevel;

    @Column(nullable = false)
    private Integer reorderThreshold;

    @Column(nullable = false)
    private Integer demandVelocity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    private Double costPrice;

    private String supplierId;
}