package com.mustafaoguzdemirel.dream_api.service;

import com.mustafaoguzdemirel.dream_api.entity.UserAnswer;
import com.mustafaoguzdemirel.dream_api.enums.QuestionType;
import com.mustafaoguzdemirel.dream_api.repository.UserAnswerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service responsible for building user profile descriptions
 * Used to personalize AI prompts based on user's questionnaire answers
 */
@Service
public class UserProfileService {

    private final UserAnswerRepository userAnswerRepository;

    public UserProfileService(UserAnswerRepository userAnswerRepository) {
        this.userAnswerRepository = userAnswerRepository;
    }

    /**
     * Builds a user profile prompt from questionnaire answers
     * @param userId The user ID
     * @return A descriptive string about the user for AI prompts
     */
    public String buildUserProfilePrompt(UUID userId) {
        List<UserAnswer> answers = userAnswerRepository.findByUser_Id(userId);

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
}