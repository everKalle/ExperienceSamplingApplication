package com.example.madiskar.experiencesamplingapp;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joosep on 25.09.2016.
 */
public class Study {
    private long id;
    private Questionnaire qBatch;
    private String name;
    private Calendar beginDate;
    private Calendar endDate;
    private int studyLength;
    private int notificationsPerDay;
    private int notificationInterval;
    private int minTimeBetweenNotifications;
    private int postponeTime;
    private boolean postponable;

    public Study(long id, String name, Questionnaire qBatch, Calendar beginDate, Calendar endDate, int studyLength, int notificationsPerDay, int notificationInterval, int postponeTime, boolean postponable, int minTimeBetweenNotifications){
        this.id = id;
        this.name = name;
        this.qBatch = qBatch;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.studyLength = studyLength;
        this.notificationsPerDay = notificationsPerDay;
        this.notificationInterval = notificationInterval;
        this.postponeTime = postponeTime;
        this.postponable = postponable;
        this.minTimeBetweenNotifications = minTimeBetweenNotifications;
    }

    public String[] questionsAsText() {

        String[] textQuestions = new String[qBatch.getQuestions().size()];

        for (int i = 0; i < qBatch.getQuestions().size(); i++) {
            textQuestions[i] = qBatch.getQuestions().get(i).getText();
        }

        return textQuestions;
    }
    public String getName() {
        return name;
    }

    public Calendar getBeginDate() {
        return beginDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public int getStudyLength() {
        return studyLength;
    }

    public Questionnaire getQuesstionnaire() { return qBatch; }

    public long getId() {
        return id;
    }

    public int getNotificationsPerDay() {
        return notificationsPerDay;
    }

    public int getNotificationInterval() {
        return notificationInterval;
    }

    public int getPostponeTime() {
        return postponeTime;
    }

    public boolean getPostponable() {
        return postponable;
    }

    public int getMinTimeBetweenNotifications() { return minTimeBetweenNotifications; }

}
