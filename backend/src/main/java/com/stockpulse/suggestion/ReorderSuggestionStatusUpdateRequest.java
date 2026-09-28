package com.stockpulse.suggestion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReorderSuggestionStatusUpdateRequest {
    private SuggestionStatus status;
}