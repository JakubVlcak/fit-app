package com.github.fit_app.service;

import com.github.fit_app.entity.Activity;
import com.github.fit_app.entity.Exercise;
import com.github.fit_app.persistence.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ExerciseService exerciseService;
    private final UserService userService;

    @Autowired
    public ActivityService(ActivityRepository activityRepository, ExerciseService exerciseService, UserService userService) {
        this.activityRepository = activityRepository;
        this.exerciseService = exerciseService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<Activity> findAll() {
        return activityRepository.findAll();
    }

    @Transactional
    public Activity create(Activity activity, Authentication authentication) {
        activity.setId(null);
        activity.setUser(userService.getUserFromAuthentication(authentication));

        List<Exercise> exercisesList = activity.getExercises();
        activity.setExercises(null);

        activity = activityRepository.save(activity);

        if (exercisesList != null) {
            activity.setExercises(exerciseService.saveAll(exercisesList, activity));
        }

        return activity;
    }
}
