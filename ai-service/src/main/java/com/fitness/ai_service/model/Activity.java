package com.fitness.ai_service.model;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fitness.ai_service.model.ActivityType;

import java.time.LocalDateTime;
import java.util.Map;


@Data
public class Activity {
    private Long id;

    private Long userId;
    private ActivityType type;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime startTime;

    @JdbcTypeCode(SqlTypes.JSON) // Or SqlTypes.JSON in some versions of Hibernate
    private Map<String, Object> additionalMetrics;

    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;
}