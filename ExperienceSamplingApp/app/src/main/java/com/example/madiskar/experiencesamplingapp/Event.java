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
    private int startTimeHour;
    private int endTimeHour;
    private int startTimeMinute;
    private int endTimeMinute;
    private int controlTime;
    private String unit;
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
    }

    public Event(long id, long studyId, String name, int startTimeHour, int startTimeMinute, int controlTime, String unit) {
        this.id = id;
        this.studyId = studyId;
        this.name = name;
        this.startTimeHour = startTimeHour;
        this.startTimeMinute = startTimeMinute;
        this.endTimeHour = -1;
        this.endTimeMinute = -1;
        this.controlTime = controlTime;
        this.unit = unit;
    }

    protected Event(Parcel in) {
        id = in.readLong();
        studyId = in.readLong();
        name = in.readString();
        startTimeHour = in.readInt();
        endTimeHour = in.readInt();
        startTimeMinute = in.readInt();
        endTimeMinute = in.readInt();
        controlTime = in.readInt();
        unit = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(studyId);
        dest.writeString(name);
        dest.writeInt(startTimeHour);
        dest.writeInt(endTimeHour);
        dest.writeInt(startTimeMinute);
        dest.writeInt(endTimeMinute);
        dest.writeInt(controlTime);
        dest.writeString(unit);
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

    public void setEndTimeHour(int endTimeHour) {
        this.endTimeHour = endTimeHour;
    }

    public void setEndTimeMinute(int endTimeMinute) {
        this.endTimeMinute = endTimeMinute;
    }
}
