package com.fitness.activity_service.service;

import com.fitness.activity_service.dto.ActivityRequest;
import com.fitness.activity_service.dto.ActivityResponse;
import com.fitness.activity_service.model.Activity;
import com.fitness.activity_service.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final TrackRepository trackRepository;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ActivityResponse trackActivity(ActivityRequest activityRequest) {
        boolean isValidUser = userValidationService.validateUser(activityRequest.getUserId());
        if (!isValidUser) {
            throw new RuntimeException("Invalid user ID: " + activityRequest.getUserId());
        }
        Activity mappedActivity = mapToActivity(activityRequest);
        Activity savedActivity =  trackRepository.save(mappedActivity);

        try{
            rabbitTemplate.convertAndSend(exchange,routingKey,savedActivity);
        } catch (Exception e){
            System.out.println("Failed to send activity to RabbitMQ: " + e.getMessage());
        }
        return mapToActivityResponse(savedActivity);
    }

    private ActivityResponse mapToActivityResponse(Activity activity) {
        ActivityResponse activityResponse = new ActivityResponse();
        BeanUtils.copyProperties(activity, activityResponse);
        return activityResponse;
    }

    private Activity mapToActivity(ActivityRequest activityRequest) {
        Activity response = new Activity();
        BeanUtils.copyProperties(activityRequest, response);
        return response;
    }

    public ActivityResponse getActivityById(Long id) {
        Activity activity = trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found with id: " + id));
        return mapToActivityResponse(activity);
    }

    public List<ActivityResponse> getActivitiesByUserId(Long userId) {
        List<Activity> activities = trackRepository.findByUserId(userId);
        System.out.println(activities.toArray().length);
        return activities.stream()
                .map(this::mapToActivityResponse)
                .collect(Collectors.toList());
    }
}
