package com.fitness.ai_service.model;

import jakarta.persistence.*;
import lombok.Builder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Builder
@Entity(name="recommendations")
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long activityId;
    private Long userId;
    private ActivityType activityType;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @JdbcTypeCode(value = SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<Map<String,String>> improvements;

    @JdbcTypeCode(value = SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<Map<String,String>> suggestions;;

    @Column(columnDefinition = "TEXT")
    private String safety;
    @CreatedDate
    private LocalDateTime createdAt;;


}
