package com.github.fit_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CoachUserId implements Serializable {
    private static final long serialVersionUID = 7344928230488585926L;
    @Column(name = "coach_id", nullable = false)
    private Long coachId;

    @Column(name = "users_id", nullable = false)
    private Long usersId;

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public Long getUsersId() {
        return usersId;
    }

    public void setUsersId(Long usersId) {
        this.usersId = usersId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CoachUserId entity = (CoachUserId) o;
        return Objects.equals(this.usersId, entity.usersId) &&
                Objects.equals(this.coachId, entity.coachId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usersId, coachId);
    }

}