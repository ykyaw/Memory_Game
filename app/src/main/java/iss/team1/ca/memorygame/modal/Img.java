package iss.team1.ca.memorygame.modal;

public class Img {

    private int uid;
    private String url;

    public Img() {
    }

    public Img(int uid) {
        this.uid = uid;
    }

    public Img(int uid, String url) {
        this.uid = uid;
        this.url = url;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
