package com.mustafaoguzdemirel.dream_api.repository;

import com.mustafaoguzdemirel.dream_api.entity.AppUser;
import com.mustafaoguzdemirel.dream_api.entity.MoodAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MoodAnalysisRepository extends JpaRepository<MoodAnalysis, UUID> {
    List<MoodAnalysis> findAllByUserOrderByCreatedAtDesc(AppUser user);
}
