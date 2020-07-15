package iss.team1.ca.memorygame.modal;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Img implements Serializable {

    private int uid;
    private String url;
    private Bitmap res;

    public Img() {
    }

    public Img(int uid) {
        this.uid = uid;
    }

    public Img(int uid, String url) {
        this.uid = uid;
        this.url = url;
    }

    public Img(int uid, Bitmap res) {
        this.uid = uid;
        this.res = res;
    }

    public Img(int uid, String url, Bitmap res) {
        this.uid = uid;
        this.url = url;
        this.res = res;
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

    public Bitmap getRes() {
        return res;
    }

    public void setRes(Bitmap res) {
        this.res = res;
    }

    @Override
    public String toString() {
        return "Img{" +
                "uid=" + uid +
                ", url='" + url + '\'' +
                ", res=" + res +
                '}';
    }
}
