package com.mustafaoguzdemirel.dream_api.repository;

import com.mustafaoguzdemirel.dream_api.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
}