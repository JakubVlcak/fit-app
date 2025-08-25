package com.github.fit_app.mapper;

import com.github.fit_app.dto.ExerciseDto;
import com.github.fit_app.entity.Exercise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

    ExerciseDto exerciseToExerciseDto(Exercise exercise);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activity", ignore = true)
    Exercise exerciseDtoToExercise(ExerciseDto exerciseRequest);
}
