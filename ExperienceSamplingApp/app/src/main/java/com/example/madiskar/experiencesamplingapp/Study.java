package com.example.madiskar.experiencesamplingapp;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joosep on 25.09.2016.
 */
public class Study {
    private ArrayList<Question> questions;
    private String name;
    private Calendar beginDate;
    private Calendar endDate;
    private int studyLength;
    private int notificationsPerDay;
    private int notificationInterval;
    private int minTimeBetweenNotifications;
    private int postponeTime;
    private boolean postponable;
    private int currentQuestion; 

    public Study(String name, ArrayList<Question> questions, Calendar beginDate, Calendar endDate, int studyLength, int notificationsPerDay, int notificationInterval, int postponeTime, boolean postponable, int minTimeBetweenNotifications){
        this.name = name;
        this.questions = questions;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.studyLength = studyLength;
        this.notificationsPerDay = notificationsPerDay;
        this.notificationInterval = notificationInterval;
        this.postponeTime = postponeTime;
        this.postponable = postponable;
        this.minTimeBetweenNotifications = minTimeBetweenNotifications;
        currentQuestion = 0;
    }

    public String[] questionsAsText() {

        String[] textQuestions = new String[questions.size()];

        for (int i = 0; i < questions.size(); i++) {
            textQuestions[i] = questions.get(i).getText();
        }

        return textQuestions;
    }
    public String getName() {
        return name;
    }

    public Calendar getBegimnDate() {
        return beginDate;
    }

    public Calendar getEndDatendDate() {
        return endDate;
    }

    public String nextQuestion(){
        if (currentQuestion < questions.size())
            return questions.get(currentQuestion++).getText();
        return null;
    }
    public int getStudyLength() {
        return studyLength;
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
