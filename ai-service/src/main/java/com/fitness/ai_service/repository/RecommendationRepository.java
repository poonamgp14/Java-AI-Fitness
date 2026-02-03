package com.fitness.ai_service.repository;

import com.fitness.ai_service.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByUserId(Long userId);

    Optional<Recommendation> findByActivityId(Long activityId);
}
