package com.stockpulse.advisor;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/advisor")
@CrossOrigin(origins = "http://localhost:5173")
public class AdvisorStrategyController {

    private final SwitchableCommerceAdvisor advisor;

    public AdvisorStrategyController(SwitchableCommerceAdvisor advisor) {
        this.advisor = advisor;
    }

    @GetMapping("/strategy")
    public Map<String, String> getStrategy() {
        return Map.of("strategy", advisor.getStrategy());
    }

    @PostMapping("/strategy")
    public Map<String, String> setStrategy(
            @RequestBody Map<String, String> request) {

        String strategy = request.get("strategy");

        advisor.setStrategy(strategy);

        return Map.of(
                "strategy", advisor.getStrategy(),
                "message", "Commerce advisor strategy updated successfully"
        );
    }
}