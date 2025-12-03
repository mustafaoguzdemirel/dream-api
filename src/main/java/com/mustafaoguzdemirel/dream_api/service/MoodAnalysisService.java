package com.mustafaoguzdemirel.dream_api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mustafaoguzdemirel.dream_api.dto.response.DreamListItemResponse;
import com.mustafaoguzdemirel.dream_api.dto.response.MoodAnalysisResponse;
import com.mustafaoguzdemirel.dream_api.entity.AppUser;
import com.mustafaoguzdemirel.dream_api.entity.Dream;
import com.mustafaoguzdemirel.dream_api.entity.MoodAnalysis;
import com.mustafaoguzdemirel.dream_api.exception.DreamNotFoundException;
import com.mustafaoguzdemirel.dream_api.exception.UserNotFoundException;
import com.mustafaoguzdemirel.dream_api.repository.DreamRepository;
import com.mustafaoguzdemirel.dream_api.repository.MoodAnalysisRepository;
import com.mustafaoguzdemirel.dream_api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service responsible for mood analysis operations
 * Analyzes dreams to identify emotional patterns and themes
 */
@Service
public class MoodAnalysisService {

    private final UserRepository userRepository;
    private final DreamRepository dreamRepository;
    private final MoodAnalysisRepository moodAnalysisRepository;
    private final OpenAiService openAiService;
    private final UserProfileService userProfileService;

    public MoodAnalysisService(
            UserRepository userRepository,
            DreamRepository dreamRepository,
            MoodAnalysisRepository moodAnalysisRepository,
            OpenAiService openAiService,
            UserProfileService userProfileService
    ) {
        this.userRepository = userRepository;
        this.dreamRepository = dreamRepository;
        this.moodAnalysisRepository = moodAnalysisRepository;
        this.openAiService = openAiService;
        this.userProfileService = userProfileService;
    }

    /**
     * Analyzes the user's recent dreams (up to 5) and generates mood analysis
     * @param userId The user ID
     * @return Map containing mood analysis data
     */
    public Map<String, Object> analyzeRecentDreams(UUID userId) {
        AppUser user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Dream> dreams = dreamRepository.findTop5ByUserOrderByCreatedAtDesc(user);
        if (dreams.isEmpty()) {
            throw new DreamNotFoundException("No dreams found for this user.");
        }

        String allDreams = dreams.stream()
                .map(Dream::getDreamText)
                .collect(Collectors.joining("\n---\n"));

        String userProfilePrompt = userProfileService.buildUserProfilePrompt(userId);

        String prompt =
                "You are an expert dream psychologist. Analyze the following dreams as a whole " +
                        "for a person who fits this description: " + userProfilePrompt + ". " +
                        "Find recurring emotional patterns, symbols, or themes across them. " +
                        "Summarize the user's current emotional and psychological state in around 150–200 words. " +
                        "Be empathetic, insightful, and write naturally. " +
                        "Respond ONLY in the following JSON format without any extra text:\n\n" +
                        "{\n" +
                        "  \"dominantEmotions\": [\"emotion1\", \"emotion2\", ...],\n" +
                        "  \"recurringSymbols\": [\"symbol1\", \"symbol2\", ...],\n" +
                        "  \"analysis\": \"Your 150–200 word empathetic summary here\"\n" +
                        "}\n\n" +
                        "Dreams:\n" + allDreams;

        String jsonResponse = openAiService.generateCompletion(prompt);

        // Clean up JSON response
        jsonResponse = jsonResponse.replaceAll("(?s)^.*?\\{", "{").replaceAll("}.*$", "}");

        Map<String, Object> parsed;
        try {
            ObjectMapper mapper = new ObjectMapper();
            parsed = mapper.readValue(jsonResponse, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            parsed = Map.of("raw_response", jsonResponse);
        }

        // Extract JSON fields
        List<String> dominantEmotions = (List<String>) parsed.get("dominantEmotions");
        List<String> recurringSymbols = (List<String>) parsed.get("recurringSymbols");
        String analysis = (String) parsed.get("analysis");

        List<DreamListItemResponse> analyzedDreamList = dreams.stream()
                .map(dream -> new DreamListItemResponse(
                        dream.getCreatedAt().getDayOfMonth(),
                        dream.getCreatedAt().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                        dream.getDreamText(),
                        dream.getId()
                ))
                .collect(Collectors.toList());

        // Save to MoodAnalysis table
        MoodAnalysis moodAnalysis = new MoodAnalysis(user, dominantEmotions, recurringSymbols, analysis);
        moodAnalysis.setAnalyzedDreams(analyzedDreamList);
        moodAnalysisRepository.save(moodAnalysis);

        LocalDateTime createdAt = moodAnalysis.getCreatedAt();
        String fullDate = createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String day = String.valueOf(createdAt.getDayOfMonth());
        String monthShort = createdAt.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

        parsed.put("id", moodAnalysis.getId());
        parsed.put("analyzedDreams", analyzedDreamList);
        parsed.put("fullDate", fullDate);
        parsed.put("day", day);
        parsed.put("month", monthShort);
        return parsed;
    }

    /**
     * Gets the mood analysis history for a user
     * @param userId The user ID
     * @return List of mood analyses
     */
    public List<MoodAnalysisResponse> getMoodHistory(UUID userId) {
        AppUser user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<MoodAnalysis> list = moodAnalysisRepository.findAllByUserOrderByCreatedAtDesc(user);

        return list.stream()
                .map(ma -> new MoodAnalysisResponse(
                        ma.getId(),
                        ma.getDominantEmotions(),
                        ma.getRecurringSymbols(),
                        ma.getAnalysis(),
                        ma.getAnalyzedDreams(),
                        ma.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}