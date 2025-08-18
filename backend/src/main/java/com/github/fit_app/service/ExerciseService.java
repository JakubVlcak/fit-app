package com.github.fit_app.service;


import com.github.fit_app.entity.Activity;
import com.github.fit_app.entity.Exercise;
import com.github.fit_app.persistence.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExerciseService {


    private final ExerciseRepository exerciseRepository;

    @Autowired
    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Transactional(readOnly = true)
    public List<Exercise> findAll() {
        return exerciseRepository.findAll();
    }

    @Transactional
    public Exercise create(Exercise exercise) {
        exercise.setId(null);
        return exerciseRepository.save(exercise);
    }

    @Transactional
    public List<Exercise> saveAll(List<Exercise> exercises, Activity activity) {
        for (Exercise exercise : exercises) {
            exercise.setActivity(activity);
            exerciseRepository.save(exercise);
        }
        return exercises;
    }
}
