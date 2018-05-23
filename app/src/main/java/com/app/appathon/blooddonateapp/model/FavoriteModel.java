package com.app.appathon.blooddonateapp.model;

/**
 * Created by IMRAN on 3/3/2018.
 */

public class FavoriteModel {
    private String uid;
    private String name;
    private String blood;

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
