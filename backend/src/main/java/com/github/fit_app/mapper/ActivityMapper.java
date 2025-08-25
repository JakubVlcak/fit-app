package com.github.fit_app.mapper;

import com.github.fit_app.dto.ActivityRequest;
import com.github.fit_app.dto.ActivityResponse;
import com.github.fit_app.entity.Activity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = ExerciseMapper.class)
public interface ActivityMapper {
    @Mapping(source = "name", target = "name")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "date")
    @Mapping(ignore = true, target = "user")
    Activity activityRequestToActivity(ActivityRequest source);

    @Mapping(source = "exercises", target = "exercises")
    ActivityResponse activityToActivityResponse(Activity activity);

    @AfterMapping
    default void linkExercises(@MappingTarget Activity activity) {
        if (activity.getExercises() != null) {
            activity.getExercises().forEach(exercise -> exercise.setActivity(activity));
        }
    }
}
