package com.example.madiskar.experiencesamplingapp;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;


public class Study {
    private long id;
    private Questionnaire qBatch;
    private String name;
    private Calendar beginDate;
    private Calendar endDate;
    private int studyLength;
    private int notificationsPerDay;
    private int minTimeBetweenNotifications;
    private int postponeTime;
    private boolean postponable;
    private Event[] events;
    private BeepFerePeriod defaultBeepFree;
    private boolean isPublic;

    public Study(long id, String name, Questionnaire qBatch, Calendar beginDate, Calendar endDate, int studyLength,
                 int notificationsPerDay, int postponeTime, boolean postponable,
                 int minTimeBetweenNotifications, Event[] events, BeepFerePeriod defaultBeepFree, boolean isPublic) {
        this.id = id;
        this.name = name;
        this.qBatch = qBatch;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.studyLength = studyLength;
        this.notificationsPerDay = notificationsPerDay;
        this.postponeTime = postponeTime;
        this.postponable = postponable;
        this.minTimeBetweenNotifications = minTimeBetweenNotifications;
        this.events = events;
        this.defaultBeepFree = defaultBeepFree;
        this.isPublic = isPublic;
    }


    public String[] questionsAsText() {

        String[] textQuestions = new String[qBatch.getQuestions().length];

        for (int i = 0; i < qBatch.getQuestions().length; i++) {
            textQuestions[i] = qBatch.getQuestions()[i].getText();
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

    public int getPostponeTime() {
        return postponeTime;
    }

    public boolean getPostponable() {
        return postponable;
    }

    public BeepFerePeriod getDefaultBeepFree() {
        return this.defaultBeepFree;
    }

    public int getMinTimeBetweenNotifications() { return minTimeBetweenNotifications; }

    public Event[] getEvents() {
        return events;
    }

    public boolean isPublic() {
        return this.isPublic;
    }
}
