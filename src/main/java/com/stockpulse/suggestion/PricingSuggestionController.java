package com.stockpulse.suggestion;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricing-suggestions")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class PricingSuggestionController {

    private final PricingSuggestionService pricingSuggestionService;

    @GetMapping
    public List<PricingSuggestion> getAllPricingSuggestions() {
        return pricingSuggestionService.getAllPricingSuggestions();
    }

    @GetMapping("/{id}")
    public PricingSuggestion getPricingSuggestionById(@PathVariable String id) {
        return pricingSuggestionService.getPricingSuggestionById(id);
    }

    @PatchMapping("/{id}")
    public PricingSuggestion updatePricingSuggestionStatus(
            @PathVariable String id,
            @RequestBody PricingSuggestionStatusUpdateRequest request) {
        return pricingSuggestionService.updatePricingSuggestionStatus(id, request);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex) {
        return ex.getMessage();
    }
}