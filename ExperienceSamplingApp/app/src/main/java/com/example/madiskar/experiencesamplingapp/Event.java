package com.example.madiskar.experiencesamplingapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Joosep on 22.10.2016.
 */
public class Event implements Parcelable{
    private long id;
    private long studyId;
    private String name;
    private int startYear;
    private int startMonth;
    private int startDayOfMonth;
    private int startTimeHour;
    private int endTimeHour;
    private int startTimeMinute;
    private int endTimeMinute;
    private int controlTime;
    private String unit;
    private long startTimeInMillis;
    // TODO: make sure all "s", "d" and "m" units are implemented and working

    public Event(long id, long studyId, String name, int controlTime, String unit) {
        this.id = id;
        this.studyId = studyId;
        this.name = name;
        this.startTimeHour = -1;
        this.startTimeMinute = -1;
        this.endTimeHour = -1;
        this.endTimeMinute = -1;
        this.controlTime = controlTime;
        this.unit = unit;
        this.startTimeInMillis = -1;
    }

    public Event(long id, long studyId, String name, int startYear, int startMonth, int startDayOfMonth, int startTimeHour, int startTimeMinute, int controlTime, String unit, long startTimeInMillis) {
        this.id = id;
        this.studyId = studyId;
        this.name = name;
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDayOfMonth =  startDayOfMonth;
        this.startTimeHour = startTimeHour;
        this.startTimeMinute = startTimeMinute;
        this.startYear = -1;
        this.startMonth = -1;
        this.startDayOfMonth = -1;
        this.endTimeHour = -1;
        this.endTimeMinute = -1;
        this.controlTime = controlTime;
        this.unit = unit;
        this.startTimeInMillis = startTimeInMillis;
    }

    protected Event(Parcel in) {
        id = in.readLong();
        studyId = in.readLong();
        name = in.readString();
        startYear = in.readInt();
        startMonth = in.readInt();
        startDayOfMonth = in.readInt();
        startTimeHour = in.readInt();
        endTimeHour = in.readInt();
        startTimeMinute = in.readInt();
        endTimeMinute = in.readInt();
        controlTime = in.readInt();
        unit = in.readString();
        startTimeInMillis = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(studyId);
        dest.writeString(name);
        dest.writeInt(startYear);
        dest.writeInt(startMonth);
        dest.writeInt(startDayOfMonth);
        dest.writeInt(startTimeHour);
        dest.writeInt(endTimeHour);
        dest.writeInt(startTimeMinute);
        dest.writeInt(endTimeMinute);
        dest.writeInt(controlTime);
        dest.writeString(unit);
        dest.writeLong(startTimeInMillis);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStartYear() {
        return startYear;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public int getStartDayOfMonth() {
        return startDayOfMonth;
    }

    public int getStartTimeHour() {
        return startTimeHour;
    }

    public int getStartTimeMinute() {
        return startTimeMinute;
    }

    public int getEndTimeHour() {
        return endTimeHour;
    }

    public int getEndTimeMinute() {
        return endTimeMinute;
    }

    public int getControlTime() {
        return controlTime;
    }

    public String getUnit() {
        return unit;
    }

    public long getStartTimeInMillis() {
        return startTimeInMillis;
    }

    public long getStudyId() {
        return studyId;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public void setStartDayOfMonth(int startDayOfMonth) {
        this.startDayOfMonth = startDayOfMonth;
    }

    public void setStartTimeHour(int startTimeHour) {
        this.startTimeHour = startTimeHour;
    }

    public void setStartTimeMinute(int startTimeMinute) {
        this.startTimeMinute = startTimeMinute;
    }

    public void setEndTimeHour(int endTimeHour) {
        this.endTimeHour = endTimeHour;
    }

    public void setEndTimeMinute(int endTimeMinute) {
        this.endTimeMinute = endTimeMinute;
    }

    public void setStartTimeInMillis(long startTimeInMillis) {
        this.startTimeInMillis = startTimeInMillis;
    }
}
