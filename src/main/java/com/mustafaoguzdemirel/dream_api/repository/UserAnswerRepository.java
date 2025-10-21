package com.mustafaoguzdemirel.dream_api.repository;

import com.mustafaoguzdemirel.dream_api.entity.AppUser;
import com.mustafaoguzdemirel.dream_api.entity.Question;
import com.mustafaoguzdemirel.dream_api.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    List<UserAnswer> findByUserId(Long userId);
    List<UserAnswer> findByUser_UserId(UUID userId);
    Optional<UserAnswer> findByUserAndQuestion(AppUser user, Question question);

}