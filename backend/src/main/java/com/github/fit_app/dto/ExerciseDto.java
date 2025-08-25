package com.github.fit_app.dto;

public class ExerciseDto {
    private Long id;
    private String name;
    private Long reps;
    private Double weight;
    private String execution;
    private Boolean deathStop = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getReps() {
        return reps;
    }

    public void setReps(Long reps) {
        this.reps = reps;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getExecution() {
        return execution;
    }

    public void setExecution(String execution) {
        this.execution = execution;
    }

    public Boolean getDeathStop() {
        return deathStop;
    }

    public void setDeathStop(Boolean deathStop) {
        this.deathStop = deathStop;
    }
}
