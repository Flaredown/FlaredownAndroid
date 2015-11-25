package com.flaredown.flaredownApp.FlareDown;

import java.util.Random;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by squigge on 11/17/2015.
 */
public class Alarm  extends RealmObject{
    @PrimaryKey
    private int id;
    private Long time;
    private String title;
    private String dayOfWeek;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
