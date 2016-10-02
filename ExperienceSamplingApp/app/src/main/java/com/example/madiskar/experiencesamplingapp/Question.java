package com.example.madiskar.experiencesamplingapp;

/**
 * Created by Joosep on 25.09.2016.
 */
public class Question {
	
    private String text;
    private long studyId;

    public Question(long studyId, String text) {
        this.text = text;
        this.studyId = studyId;
    }

    public String getText() {
        return text;
    }

    public long getStudyId() {
        return studyId;
    }
}
