package com.springvuegradle.team200.model;

import javax.persistence.*;
import java.time.Instant;

/**
 * Defines relationship between User and Activity
 * <p>
 * User X did ACTION on Activity Y at Timestamp Z
 */
@Entity
public class UserActivityHistory {

    @Id
    @GeneratedValue
    @Column(name = "history_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    private Action action;

    public UserActivityHistory() {
        //left empty intentionally
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
