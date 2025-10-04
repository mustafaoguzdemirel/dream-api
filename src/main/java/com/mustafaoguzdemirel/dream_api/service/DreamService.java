package com.mustafaoguzdemirel.dream_api.service;

import com.mustafaoguzdemirel.dream_api.dto.DreamSaveRequest;
import com.mustafaoguzdemirel.dream_api.entity.AppUser;
import com.mustafaoguzdemirel.dream_api.entity.Dream;
import com.mustafaoguzdemirel.dream_api.repository.DreamRepository;
import com.mustafaoguzdemirel.dream_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DreamService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    private final DreamRepository dreamRepository;
    private final UserRepository userRepository;

    public DreamService(DreamRepository dreamRepository, UserRepository userRepository) {
        this.dreamRepository = dreamRepository;
        this.userRepository = userRepository;
    }

    public String interpretDream(String dreamText) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", "Interpret this dream in a positive and empathetic way: " + dreamText);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o-mini");
            requestBody.put("messages", new Object[]{message});

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + openAiApiKey);
            headers.put("Content-Type", "application/json");

            org.springframework.http.HttpEntity<Map<String, Object>> entity =
                    new org.springframework.http.HttpEntity<>(requestBody, new org.springframework.http.HttpHeaders() {{
                        setAll(headers);
                    }});

            Map<String, Object> response = restTemplate.postForObject(OPENAI_URL, entity, Map.class);

            if (response == null || !response.containsKey("choices")) {
                throw new RuntimeException("Invalid response from OpenAI");
            }

            var choices = (java.util.List<Map<String, Object>>) response.get("choices");
            if (choices.isEmpty()) {
                throw new RuntimeException("Empty choices from OpenAI");
            }

            var messageMap = (Map<String, Object>) choices.get(0).get("message");
            return messageMap.get("content").toString();

        } catch (Exception e) {
            throw new RuntimeException("Dream interpretation failed: " + e.getMessage(), e);
        }
    }

    public Dream saveDream(DreamSaveRequest request) {
        Optional<AppUser> userOpt = userRepository.findByUserId(request.getUserId());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + request.getUserId());
        }

        AppUser user = userOpt.get();

        Dream dream = new Dream();
        dream.setUser(user);
        dream.setDreamText(request.getDreamText());
        dream.setInterpretation(request.getInterpretation());
        dream.setCreatedAt(LocalDateTime.now());

        return dreamRepository.save(dream);
    }

    public List<Dream> getDreamHistory(UUID userId) {
        AppUser user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return dreamRepository.findByUser(user);
    }
}
