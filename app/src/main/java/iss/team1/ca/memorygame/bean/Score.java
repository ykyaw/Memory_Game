package iss.team1.ca.memorygame.bean;

import java.io.Serializable;

public class Score implements Serializable {

    private int uid;
    private User user;
    private Long time;
    private Long score;

    public Score() {
    }

    public Score(int uid) {
        this.uid = uid;
    }

    public Score(int uid, User user, Long time, Long score) {
        this.uid = uid;
        this.user = user;
        this.time = time;
        this.score = score;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Score{" +
                "uid=" + uid +
                ", user=" + user +
                ", time=" + time +
                ", score=" + score +
                '}';
    }
}
