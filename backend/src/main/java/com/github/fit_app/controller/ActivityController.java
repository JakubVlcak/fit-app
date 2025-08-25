package com.github.fit_app.controller;

import com.github.fit_app.dto.ActivityRequest;
import com.github.fit_app.dto.ActivityResponse;
import com.github.fit_app.entity.Activity;
import com.github.fit_app.mapper.ActivityMapper;
import com.github.fit_app.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityMapper activityMapper;

    @Autowired
    public ActivityController(ActivityService activityService, ActivityMapper activityMapper) {
        this.activityService = activityService;
        this.activityMapper = activityMapper;
    }

    @GetMapping
    public List<ActivityResponse> getActivities() {
        return activityService.findAll().stream().map(activityMapper::activityToActivityResponse).toList();
    }

    @PostMapping
    public Activity createActivity(@RequestBody ActivityRequest activity, Authentication authentication) {
        return activityService.create(activityMapper.activityRequestToActivity(activity), authentication);
    }
}
