package com.mustafaoguzdemirel.dream_api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mustafaoguzdemirel.dream_api.dto.response.DreamCalendarResponse;
import com.mustafaoguzdemirel.dream_api.dto.response.DreamDetailResponse;
import com.mustafaoguzdemirel.dream_api.dto.request.DreamSaveRequest;
import com.mustafaoguzdemirel.dream_api.entity.AppUser;
import com.mustafaoguzdemirel.dream_api.entity.Dream;
import com.mustafaoguzdemirel.dream_api.entity.MoodAnalysis;
import com.mustafaoguzdemirel.dream_api.entity.UserAnswer;
import com.mustafaoguzdemirel.dream_api.enums.QuestionType;
import com.mustafaoguzdemirel.dream_api.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DreamService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    private final UserAnswerRepository userAnswerRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;
    private final DreamRepository dreamRepository;
    private final MoodAnalysisRepository moodAnalysisRepository;

    public DreamService(
            UserAnswerRepository userAnswerRepository,
            QuestionRepository questionRepository,
            OptionRepository optionRepository,
            UserRepository userRepository,
            DreamRepository dreamRepository,
            MoodAnalysisRepository moodAnalysisRepository
    ) {
        this.userAnswerRepository = userAnswerRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.userRepository = userRepository;
        this.dreamRepository = dreamRepository;
        this.moodAnalysisRepository = moodAnalysisRepository;
    }

    public String interpretDream(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

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

    public DreamCalendarResponse getDreamCalendar(UUID userId) {
        AppUser user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Dream> dreams = dreamRepository.findByUser(user);

        int currentYear = LocalDate.now().getYear();
        List<DreamCalendarResponse.MonthData> monthList = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(currentYear, month);
            int daysInMonth = yearMonth.lengthOfMonth();

            List<DreamCalendarResponse.DayData> days = new ArrayList<>();

            for (int day = 1; day <= daysInMonth; day++) { // ← sadece gerçek günler
                LocalDate date = LocalDate.of(currentYear, month, day);
                UUID dreamId = dreams.stream()
                        .filter(d -> d.getCreatedAt().toLocalDate().isEqual(date))
                        .map(Dream::getId)
                        .findFirst()
                        .orElse(null);

                days.add(new DreamCalendarResponse.DayData(day, dreamId));
            }

            monthList.add(new DreamCalendarResponse.MonthData(month, days));
        }


        return new DreamCalendarResponse(currentYear, monthList);
    }

    public DreamDetailResponse getDreamDetail(UUID dreamId) {
        Dream dream = dreamRepository.findById(dreamId)
                .orElseThrow(() -> new EntityNotFoundException("Dream not found"));

        return new DreamDetailResponse(
                dream.getId(),
                dream.getDreamText(),
                dream.getInterpretation(),
                dream.getDetailedInterpretation(),
                dream.getCreatedAt()
        );
    }

    public DreamDetailResponse interpretDreamForUser(UUID userId, String dreamText) {
        AppUser user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();

        // Eğer kullanıcı bugün zaten rüya yorumlattıysa hata fırlat
        //   if (today.equals(user.getLastDreamInterpretedDate())) {
        //     throw new RuntimeException("User has already interpreted a dream today.");
        //} //TODO!!!! AÇ

        // ✅ Kullanıcının cevaplarından kişisel prompt oluştur
        String userProfilePrompt = buildUserProfilePrompt(userId);

        // ✅ GPT'ye gönderilecek tam prompt'u oluştur

        String fullPrompt = "Give a short and emotionally warm interpretation of this dream " +
                userProfilePrompt + ". Write naturally, like giving comforting advice, but keep it under 150 words. Dream text: " + dreamText;


        // ✅ GPT'den yorum al (artık fullPrompt gönderiyoruz)
        String interpretation = interpretDream(fullPrompt);

        // Yeni dream kaydını oluştur
        Dream dream = new Dream();
        dream.setUser(user);
        dream.setDreamText(dreamText);
        dream.setInterpretation(interpretation);
        dream.setCreatedAt(LocalDateTime.now());

        dreamRepository.save(dream);

        // Kullanıcının son yorum tarihini bugüne güncelle
        user.setLastDreamInterpretedDate(today);
        userRepository.save(user);

        DreamDetailResponse dreamDetailResponse = new DreamDetailResponse(
                dream.getId(),
                dream.getDreamText(),
                interpretation,
                "",
                dream.getCreatedAt()
        );

        // Response oluştur (old response)
        //   Map<String, Object> data = new HashMap<>();
        //   data.put("interpretation", interpretation);
        //   data.put("dreamId", dream.getId());
        //   data.put("createdAt", dream.getCreatedAt().toString());
        //   data.put("prompt", fullPrompt); // istersen debug için ekleyebilirsin

        return dreamDetailResponse;
    }

    public DreamDetailResponse getDetailedInterpretation(UUID dreamId) {
        Dream dream = dreamRepository.findById(dreamId)
                .orElseThrow(() -> new RuntimeException("Dream not found"));

        AppUser user = dream.getUser();
        String userProfilePrompt = buildUserProfilePrompt(user.getUserId());

        String fullPrompt = "You are an experienced dream analyst. Provide a detailed and insightful interpretation " +
                "of the following dream, taking into account the user's personality and emotional profile " + userProfilePrompt +
                ". Expand upon the short interpretation provided below, adding psychological, emotional, and symbolic analysis. " +
                "Write in a compassionate and inspiring tone, around 300–500 words. " +
                "\n\nDream text: " + dream.getDreamText() +
                "\n\nShort interpretation: " + dream.getInterpretation();

        String detailedInterpretation = interpretDream(fullPrompt);

        // 💾 kaydı güncelle
        dream.setDetailedInterpretation(detailedInterpretation);
        dreamRepository.save(dream);

        DreamDetailResponse dreamDetailResponse = new DreamDetailResponse(
                dream.getId(),
                dream.getDreamText(),
                dream.getInterpretation(),
                detailedInterpretation,
                dream.getCreatedAt()
        );

        // Response oluştur (old response)
        //   Map<String, Object> result = new HashMap<>();
     //   result.put("dreamId", dream.getId());
     //   result.put("detailedInterpretation", detailedInterpretation);
     //   result.put("createdAt", dream.getCreatedAt().toString());

        return dreamDetailResponse;
    }

    public Map<String, Object> analyzeRecentDreams(UUID userId) {
        AppUser user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Dream> dreams = dreamRepository.findTop5ByUserOrderByCreatedAtDesc(user);
        if (dreams.isEmpty()) {
            throw new RuntimeException("No dreams found for this user.");
        }

        String allDreams = dreams.stream()
                .map(Dream::getDreamText)
                .collect(Collectors.joining("\n---\n"));

        String userProfilePrompt = buildUserProfilePrompt(userId);

        String prompt =
                "You are an expert dream psychologist. Analyze the following dreams as a whole " +
                        "for a person who fits this description: " + userProfilePrompt + ". " +
                        "Find recurring emotional patterns, symbols, or themes across them. " +
                        "Summarize the user's current emotional and psychological state in around 150–200 words. " +
                        "Be empathetic, insightful, and write naturally. " +
                        "Respond ONLY in the following JSON format without any extra text:\n\n" +
                        "{\n" +
                        "  \"dominant_emotions\": [\"emotion1\", \"emotion2\", ...],\n" +
                        "  \"recurring_symbols\": [\"symbol1\", \"symbol2\", ...],\n" +
                        "  \"analysis\": \"Your 150–200 word empathetic summary here\"\n" +
                        "}\n\n" +
                        "Dreams:\n" + allDreams;

        String jsonResponse = interpretDream(prompt);

        jsonResponse = jsonResponse.replaceAll("(?s)^.*?\\{", "{").replaceAll("}.*$", "}");

        Map<String, Object> parsed;
        try {
            ObjectMapper mapper = new ObjectMapper();
            parsed = mapper.readValue(jsonResponse, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            parsed = Map.of("raw_response", jsonResponse);
        }

        // ✅ JSON’dan alanları çıkar
        List<String> dominantEmotions = (List<String>) parsed.get("dominant_emotions");
        List<String> recurringSymbols = (List<String>) parsed.get("recurring_symbols");
        String analysis = (String) parsed.get("analysis");

        // ✅ MoodAnalysis tablosuna kaydet
        MoodAnalysis moodAnalysis = new MoodAnalysis(user, dominantEmotions, recurringSymbols, analysis);
        moodAnalysisRepository.save(moodAnalysis);

        parsed.put("savedId", moodAnalysis.getId());
        parsed.put("dreamCount", dreams.size());
        return parsed;
    }


    private String buildUserProfilePrompt(UUID userId) {
        List<UserAnswer> answers = userAnswerRepository.findByUser_UserId(userId);

        if (answers.isEmpty()) {
            return "for a general audience with no specific profile";
        }

        StringBuilder profile = new StringBuilder("for a person who ");

        Map<QuestionType, String> answerMap = answers.stream()
                .collect(Collectors.toMap(a -> a.getQuestion().getType(), a -> a.getOption().getContent()));

        for (Map.Entry<QuestionType, String> entry : answerMap.entrySet()) {
            QuestionType type = entry.getKey();
            String option = entry.getValue();

            switch (type) {
                case AGE_RANGE -> profile.append("is ").append(option).append(" years old, ");
                case GENDER_IDENTITY -> profile.append("identifies as ").append(option).append(", ");
                case PERSONALITY -> profile.append("has a ").append(option.toLowerCase()).append(" personality, ");
                case DREAM_RECALL -> profile.append("remembers dreams ").append(option.toLowerCase()).append(", ");
                case VIEW_ON_DREAMS -> profile.append("views dreams ").append(option.toLowerCase()).append(", ");
                case LIFE_FOCUS -> profile.append("is mainly focused on ").append(option.toLowerCase()).append(", ");
                case EMOTIONAL_STATE -> profile.append("feels ").append(option.toLowerCase()).append(" lately, ");
                case SPIRITUALITY -> profile.append("is ").append(option.toLowerCase()).append(" spiritual, ");
                case SELF_UNDERSTANDING ->
                        profile.append("believes they understand themselves ").append(option.toLowerCase()).append(", ");
            }
        }

        String finalText = profile.toString().trim();
        if (finalText.endsWith(",")) finalText = finalText.substring(0, finalText.length() - 1);
        return finalText;
    }

    public List<MoodAnalysis> getMoodHistory(UUID userId) {
        AppUser user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return moodAnalysisRepository.findAllByUserOrderByCreatedAtDesc(user);
    }

}
