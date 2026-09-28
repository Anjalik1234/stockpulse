package com.stockpulse.suggestion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PricingSuggestionRepository extends JpaRepository<PricingSuggestion, String> {
    List<PricingSuggestion> findByProductIdAndStatus(String productId, SuggestionStatus status);
    List<PricingSuggestion> findByProductIdAndStatusAndTriggerReason(String productId, SuggestionStatus status, SuggestionTriggerReason triggerReason);
    Optional<PricingSuggestion> findFirstByProductIdAndStatusOrderByConfidenceDesc(String productId, SuggestionStatus status);
}