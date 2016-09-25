package com.example.madiskar.experiencesamplingapp;

import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by Joosep on 25.09.2016.
 */
public class Study {
    private ArrayList<Question> questions;
    private String name;
    private Time beginDate;
    private Time endDate;
    private Time studyLength;
    private int notificationsPerDay;
    private int notificationInterval;
    private int postponeTime;
    private boolean postponable;

    public Study(String name, ArrayList<Question> questions, Time beginDate, Time endDate, Time studyLength, int notificationsPerDay, int notificationInterval, int postponeTime, boolean postponable){
        this.name = name;
        this.questions = questions;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.studyLength = studyLength;
        this.notificationsPerDay = notificationsPerDay;
        this.notificationInterval = notificationInterval;
        this.postponeTime = postponeTime;
        this.postponable = postponable;
    }

    public String getName() {
        return name;
    }

    public Time getBeginDate() {
        return beginDate;
    }

    public Time endDate() {
        return endDate;
    }

    public Time studyLength() {
        return studyLength;
    }

    public int notificationsPerDay() {
        return notificationsPerDay;
    }

    public int notificationInterval() {
        return notificationInterval;
    }

    public int postponeTime() {
        return postponeTime;
    }

    public boolean postponable() {
        return postponable;
    }

}