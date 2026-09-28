package com.stockpulse.suggestion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReorderSuggestionRepository extends JpaRepository<ReorderSuggestion, String> {
    List<ReorderSuggestion> findByProductIdAndStatus(String productId, SuggestionStatus status);
    List<ReorderSuggestion> findByProductIdAndStatusAndTriggerReason(String productId, SuggestionStatus status, SuggestionTriggerReason triggerReason);
    Optional<ReorderSuggestion> findFirstByProductIdAndStatusOrderByConfidenceDesc(String productId, SuggestionStatus status);
}