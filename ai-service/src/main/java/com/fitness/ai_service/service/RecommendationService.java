package com.fitness.ai_service.service;

import com.fitness.ai_service.model.Recommendation;
import com.fitness.ai_service.repository.RecommendationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;

    public List<Recommendation> getRecommendationsByUserId(Long userId) {
        return recommendationRepository.findByUserId(userId);
    }

    public Recommendation getRecommendationsByActivityId(Long activityId) {
        return recommendationRepository.findByActivityId(activityId)
                .orElseThrow( () -> new RuntimeException("Recommendation not found for activityId: " + activityId));
    }
}
