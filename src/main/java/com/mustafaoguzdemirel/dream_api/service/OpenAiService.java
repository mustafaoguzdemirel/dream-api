package com.mustafaoguzdemirel.dream_api.service;

import com.mustafaoguzdemirel.dream_api.exception.OpenAiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for OpenAI API integration
 * Handles all communication with OpenAI's GPT models
 */
@Service
public class OpenAiService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4o-mini";

    /**
     * Sends a prompt to OpenAI and returns the response
     * @param prompt The prompt to send to OpenAI
     * @return The AI-generated response
     * @throws OpenAiException if API call fails or returns invalid response
     */
    public String generateCompletion(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL);
            requestBody.put("messages", new Object[]{message});

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + openAiApiKey);
            headers.set("Content-Type", "application/json");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            Map<String, Object> response = restTemplate.postForObject(OPENAI_URL, entity, Map.class);

            if (response == null || !response.containsKey("choices")) {
                throw new OpenAiException("Invalid response from OpenAI");
            }

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices.isEmpty()) {
                throw new OpenAiException("Empty choices from OpenAI");
            }

            Map<String, Object> messageMap = (Map<String, Object>) choices.get(0).get("message");
            return messageMap.get("content").toString();

        } catch (OpenAiException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenAiException("OpenAI API call failed: " + e.getMessage(), e);
        }
    }
}