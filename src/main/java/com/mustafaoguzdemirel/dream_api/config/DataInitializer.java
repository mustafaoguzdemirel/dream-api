package com.mustafaoguzdemirel.dream_api.config;

import com.mustafaoguzdemirel.dream_api.entity.Option;
import com.mustafaoguzdemirel.dream_api.entity.Question;
import com.mustafaoguzdemirel.dream_api.enums.QuestionType;
import com.mustafaoguzdemirel.dream_api.repository.OptionRepository;
import com.mustafaoguzdemirel.dream_api.repository.QuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initQuestions(QuestionRepository questionRepository, OptionRepository optionRepository) {
        return args -> {
            if (questionRepository.count() > 0) return; // daha önce eklendiyse atla

            // 1️⃣ Question 1
            Question q1 = new Question();
            q1.setContent("What is your age range?");
            q1.setType(QuestionType.AGE_RANGE);
            questionRepository.save(q1);

            List<Option> q1Options = Arrays.asList(
                    new Option(q1, "Under 18"),
                    new Option(q1, "18–25"),
                    new Option(q1, "26–35"),
                    new Option(q1, "36–50"),
                    new Option(q1, "50+")
            );
            optionRepository.saveAll(q1Options);

            // 2️⃣ Question 2
            Question q2 = new Question();
            q2.setContent("What is your gender identity?");
            q2.setType(QuestionType.GENDER_IDENTITY);
            questionRepository.save(q2);

            List<Option> q2Options = Arrays.asList(
                    new Option(q2, "Female"),
                    new Option(q2, "Male"),
                    new Option(q2, "Prefer not to say")
            );
            optionRepository.saveAll(q2Options);

            // 3️⃣ Question 3
            Question q3 = new Question();
            q3.setContent("Which of these best describes your personality?");
            q3.setType(QuestionType.PERSONALITY);
            questionRepository.save(q3);

            List<Option> q3Options = Arrays.asList(
                    new Option(q3, "Calm and reflective"),
                    new Option(q3, "Emotional and intuitive"),
                    new Option(q3, "Analytical and logical"),
                    new Option(q3, "Adventurous and curious")
            );
            optionRepository.saveAll(q3Options);

            // 4️⃣ Question 4
            Question q4 = new Question();
            q4.setContent("How often do you remember your dreams?");
            q4.setType(QuestionType.DREAM_RECALL);
            questionRepository.save(q4);

            List<Option> q4Options = Arrays.asList(
                    new Option(q4, "Almost every night"),
                    new Option(q4, "Sometimes"),
                    new Option(q4, "Rarely"),
                    new Option(q4, "Hardly ever")
            );
            optionRepository.saveAll(q4Options);

            // 5️⃣ Question 5
            Question q5 = new Question();
            q5.setContent("How do you view dreams?");
            q5.setType(QuestionType.VIEW_ON_DREAMS);
            questionRepository.save(q5);

            List<Option> q5Options = Arrays.asList(
                    new Option(q5, "As symbolic messages"),
                    new Option(q5, "As reflections of daily thoughts"),
                    new Option(q5, "As random brain activity"),
                    new Option(q5, "Not sure")
            );
            optionRepository.saveAll(q5Options);

            // 6️⃣ Question 6
            Question q6 = new Question();
            q6.setContent("What are you mainly focused on in life right now?");
            q6.setType(QuestionType.LIFE_FOCUS);
            questionRepository.save(q6);

            List<Option> q6Options = Arrays.asList(
                    new Option(q6, "Career or studies"),
                    new Option(q6, "Relationships or love"),
                    new Option(q6, "Personal growth"),
                    new Option(q6, "Mental health or peace"),
                    new Option(q6, "Financial stability")
            );
            optionRepository.saveAll(q6Options);

            // 7️⃣ Question 7
            Question q7 = new Question();
            q7.setContent("How would you describe your emotional state lately?");
            q7.setType(QuestionType.EMOTIONAL_STATE);
            questionRepository.save(q7);

            List<Option> q7Options = Arrays.asList(
                    new Option(q7, "Calm and balanced"),
                    new Option(q7, "Slightly stressed"),
                    new Option(q7, "Emotionally intense"),
                    new Option(q7, "Often anxious")
            );
            optionRepository.saveAll(q7Options);

            // 8️⃣ Question 8
            Question q8 = new Question();
            q8.setContent("Do you consider yourself a spiritual person?");
            q8.setType(QuestionType.SPIRITUALITY);
            questionRepository.save(q8);

            List<Option> q8Options = Arrays.asList(
                    new Option(q8, "Yes"),
                    new Option(q8, "Somewhat"),
                    new Option(q8, "Not really")
            );
            optionRepository.saveAll(q8Options);

            // 9️⃣ Question 9
            Question q9 = new Question();
            q9.setContent("How well do you think you understand yourself?");
            q9.setType(QuestionType.SELF_UNDERSTANDING);
            questionRepository.save(q9);

            List<Option> q9Options = Arrays.asList(
                    new Option(q9, "Very well"),
                    new Option(q9, "Somewhat"),
                    new Option(q9, "Not much"),
                    new Option(q9, "I’m still exploring")
            );
            optionRepository.saveAll(q9Options);

            System.out.println("✅ Questions initialized successfully!");
        };
    }
}
