package com.stockpulse.suggestion;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "pricing_suggestions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingSuggestion {

    @Id
    private String id;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private Double currentPrice;

    @Column(nullable = false)
    private Double recommendedPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PricingDirection direction;

    @Column(nullable = false)
    private Double confidence;

    @Column(nullable = false)
    private String reasoning;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SuggestionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SuggestionTriggerReason triggerReason;

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
    }
}