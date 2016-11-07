package com.example.madiskar.experiencesamplingapp;


public class BeepFerePeriod {

    private int id;
    private int startTimeHour = 0;
    private int startTimeMinute = 0;
    private int endTimeHour = 0;
    private int endTimeMinute = 0;

    public BeepFerePeriod() {
    }
    public BeepFerePeriod(int id, int startTimeHour, int startTimeMinute, int endTimeHour, int endTimeMinute) {
        this.id = id;
        this.startTimeHour = startTimeHour;
        this.startTimeMinute = startTimeMinute;
        this.endTimeHour = endTimeHour;
        this.endTimeMinute = endTimeMinute;
    }

    public int getId() {
        return id;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setStartTimeHour(int time) {
        startTimeHour = time;
    }

    public void setStartTimeMinute(int time) {
        startTimeMinute = time;
    }
    public void setEndTimeHour(int time) {
        endTimeHour = time;
    }

    public void setEndTimeMinute(int time) {
        endTimeMinute = time;
    }

    public String toString() {
        return this.startTimeHour + "." + this.startTimeMinute + ":" + this.endTimeHour + "." + this.endTimeMinute;
    }

    public String getPeriodAsString() {
        return startTimeHour + ":" + startTimeMinute + ":00" + " " + endTimeHour + ":" + endTimeMinute + ":00";
    }

}
