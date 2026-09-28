package com.stockpulse.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@Slf4j
public class LLMGateway {

    @Value("${litellm.base-url:https://litellm-qc.zycus.net/v1/chat/completions}")
    private String baseUrl;

    @Value("${litellm.api-key}")
    private String apiKey;

    @Value("${litellm.model:qwen-cursor}")
    private String model;

    private final RestClient restClient;

    public LLMGateway(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public String callLLM(String prompt) {
        try {
            // Build the request body as a simple JSON string
            String requestBody = String.format(
                "{\"model\":\"%s\",\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}]}", 
                model, 
                escapeJson(prompt)
            );

            log.debug("Calling LLM with prompt");

            // Make the API call
            ResponseEntity<String> response = restClient.post()
                    .uri(baseUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("product", "PC1")
                    .body(requestBody)
                    .retrieve()
                    .toEntity(String.class);

            // Extract the assistant's text content from the response
            return extractAssistantContent(response.getBody());

        } catch (RestClientResponseException e) {
            log.error("HTTP error occurred while calling LLM: Status={}", e.getStatusCode());
            throw new RuntimeException("Failed to call LLM: HTTP " + e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while calling LLM", e);
            throw new RuntimeException("Failed to call LLM: " + e.getMessage(), e);
        }
    }

    private String extractAssistantContent(String jsonResponse) {
        // Simple JSON parsing to extract the assistant's content
        // In a production environment, you might want to use a proper JSON library like Jackson
        try {
            // This is a simplified extraction assuming a standard OpenAI response format
            // Looking for content in the first choice message
            String contentMarker = "\"content\":\"";
            int startIndex = jsonResponse.indexOf(contentMarker);
            if (startIndex == -1) {
                throw new RuntimeException("Unable to extract content from LLM response");
            }
            
            startIndex += contentMarker.length();
            int endIndex = jsonResponse.indexOf("\"", startIndex);
            if (endIndex == -1) {
                throw new RuntimeException("Unable to extract content from LLM response");
            }
            
            return unescapeJson(jsonResponse.substring(startIndex, endIndex));
        } catch (Exception e) {
            log.error("Error extracting content from LLM response");
            throw new RuntimeException("Failed to parse LLM response", e);
        }
    }

    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    private String unescapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\\"", "\"")
                  .replace("\\\\", "\\")
                  .replace("\\b", "\b")
                  .replace("\\f", "\f")
                  .replace("\\n", "\n")
                  .replace("\\r", "\r")
                  .replace("\\t", "\t");
    }
}