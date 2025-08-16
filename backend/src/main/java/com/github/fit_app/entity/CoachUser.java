package com.github.fit_app.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "coach_users")
public class CoachUser {
    @EmbeddedId
    private CoachUserId id;

    public CoachUserId getId() {
        return id;
    }

    public void setId(CoachUserId id) {
        this.id = id;
    }

}