package com.github.fit_app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "exercise")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "reps", nullable = false)
    private Long reps;

    @Column(name = "weight", nullable = false)
    private Double weight;

    @Column(name = "execution", nullable = false)
    private String execution;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Column(name = "death_stop", nullable = false)
    private Boolean deathStop = false;

    public Boolean getDeathStop() {
        return deathStop;
    }

    public void setDeathStop(Boolean deathStop) {
        this.deathStop = deathStop;
    }

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

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", reps=" + reps +
                ", weight=" + weight +
                ", execution='" + execution + '\'' +
                ", activity=" + activity +
                ", deathStop=" + deathStop +
                '}';
    }
}