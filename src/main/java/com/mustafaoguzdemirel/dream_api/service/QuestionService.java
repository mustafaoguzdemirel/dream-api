package com.mustafaoguzdemirel.dream_api.service;

import com.mustafaoguzdemirel.dream_api.dto.request.UserAnswerRequest;
import com.mustafaoguzdemirel.dream_api.dto.response.OptionResponse;
import com.mustafaoguzdemirel.dream_api.dto.response.QuestionResponse;
import com.mustafaoguzdemirel.dream_api.entity.AppUser;
import com.mustafaoguzdemirel.dream_api.entity.Option;
import com.mustafaoguzdemirel.dream_api.entity.Question;
import com.mustafaoguzdemirel.dream_api.entity.UserAnswer;
import com.mustafaoguzdemirel.dream_api.repository.OptionRepository;
import com.mustafaoguzdemirel.dream_api.repository.QuestionRepository;
import com.mustafaoguzdemirel.dream_api.repository.UserAnswerRepository;
import com.mustafaoguzdemirel.dream_api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final UserRepository appUserRepository;

    public QuestionService(
            QuestionRepository questionRepository,
            OptionRepository optionRepository,
            UserAnswerRepository userAnswerRepository,
            UserRepository appUserRepository
    ) {
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.appUserRepository = appUserRepository;
    }

    public List<QuestionResponse> getAllQuestionsForUser(UUID userId) {
        List<Question> questions = questionRepository.findAll();

        // kullanıcının verdiği cevapları çek
        List<UserAnswer> userAnswers = userAnswerRepository.findByUser_UserId(userId);

        // hangi option'lar seçilmiş onları set'e al
        Set<Long> selectedOptionIds = userAnswers.stream()
                .map(answer -> answer.getOption().getId())
                .collect(Collectors.toSet());

        // question -> option mapping
        return questions.stream().map(q ->
                new QuestionResponse(
                        q.getId(),
                        q.getContent(),
                        q.getOptions().stream()
                                .map(o -> new OptionResponse(
                                        o.getId(),
                                        o.getContent(),
                                        selectedOptionIds.contains(o.getId()) // isSelected
                                ))
                                .collect(Collectors.toList())
                )
        ).collect(Collectors.toList());
    }

    public void saveUserAnswer(UserAnswerRequest request) {
        UUID userId = request.getUserId();
        Long questionId = request.getQuestionId();
        Long optionId = request.getOptionId();

        // user kontrolü
        AppUser user = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // question & option kontrolü
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("Option not found"));

        // aynı soruya verilen yanıtı bul
        Optional<UserAnswer> existingAnswer =
                userAnswerRepository.findByUserAndQuestion(user, question);

        if (existingAnswer.isPresent()) {
            // update
            UserAnswer answer = existingAnswer.get();
            answer.setOption(option);
            userAnswerRepository.save(answer);
        } else {
            // yeni kayıt
            UserAnswer newAnswer = new UserAnswer(user, question, option);
            userAnswerRepository.save(newAnswer);
        }
    }
}
