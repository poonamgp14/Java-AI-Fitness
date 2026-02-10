package com.fitness.activity_service.repository;


import com.fitness.activity_service.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByUserId(String userId);
}
