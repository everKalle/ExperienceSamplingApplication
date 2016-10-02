package com.example.madiskar.experiencesamplingapp;

import java.util.ArrayList;

/**
 * Created by madiskar on 02/10/2016.
 */
public class Questionnaire {

    long studyId;
    ArrayList<Question> questions;

    public Questionnaire(long studyId, ArrayList<Question> questions) {
        this.studyId = studyId;
        this.questions = questions;
    }

    public long getStudyId() {
        return this.studyId;
    }

    public ArrayList<Question> getQuestions() {
        return this.questions;
    }
}
