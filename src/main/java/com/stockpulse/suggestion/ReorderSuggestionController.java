package com.stockpulse.suggestion;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reorder-suggestions")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ReorderSuggestionController {

    private final ReorderSuggestionService reorderSuggestionService;

    @GetMapping
    public List<ReorderSuggestion> getAllReorderSuggestions() {
        return reorderSuggestionService.getAllReorderSuggestions();
    }

    @GetMapping("/{id}")
    public ReorderSuggestion getReorderSuggestionById(@PathVariable String id) {
        return reorderSuggestionService.getReorderSuggestionById(id);
    }

    @PatchMapping("/{id}")
    public ReorderSuggestion updateReorderSuggestionStatus(
            @PathVariable String id,
            @RequestBody ReorderSuggestionStatusUpdateRequest request) {
        return reorderSuggestionService.updateReorderSuggestionStatus(id, request);
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