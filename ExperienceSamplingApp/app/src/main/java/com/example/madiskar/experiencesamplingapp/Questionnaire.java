package com.example.madiskar.experiencesamplingapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by madiskar on 02/10/2016.
 */
public class Questionnaire implements Parcelable{

    long studyId;
    Question[] questions;
    FreeTextQuestion[] freeTextQuestions;
    MultipleChoiceQuestion[] multipleChoiceQuestions;
   // ArrayList<Question> questions;

    public Questionnaire(long studyId, Question[] questions) {
        this.studyId = studyId;
        this.questions = questions;
        int freeTextQCount = 0;
        int multipleChoiceQCount = 0;

        for (int i = 0; i < questions.length; i++) {
            if (questions[i] instanceof FreeTextQuestion) {
                freeTextQCount++;
            } else
                multipleChoiceQCount++;
        }
        freeTextQuestions = new FreeTextQuestion[freeTextQCount];
        multipleChoiceQuestions = new MultipleChoiceQuestion[multipleChoiceQCount];

        int freeCount = 0;
        int multipleCount = 0;
        Log.v("TEEMA", String.valueOf(questions.length));
        for (int i = 0; i < questions.length; i++) {
            if (questions[i] instanceof FreeTextQuestion) {
                freeTextQuestions[freeCount++] = (FreeTextQuestion) questions[i];
            } else
                multipleChoiceQuestions[multipleCount++] = (MultipleChoiceQuestion) questions[i];
        }
    }

    public MultipleChoiceQuestion[] getMultipleChoiceQuestions() {
        return multipleChoiceQuestions;
    }

    public FreeTextQuestion[] getFreeTextQuestions() {
        return freeTextQuestions;
    }

    public long getStudyId() {
        return this.studyId;
    }

    public Question[] getQuestions() {
        return this.questions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(studyId);
        out.writeTypedArray(questions,0);
        out.writeTypedArray(freeTextQuestions,0);
        out.writeTypedArray(multipleChoiceQuestions,0);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Questionnaire> CREATOR = new Parcelable.Creator<Questionnaire>() {
        public Questionnaire createFromParcel(Parcel in) {
            return new Questionnaire(in);
        }

        public Questionnaire[] newArray(int size) {
            return new Questionnaire[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Questionnaire(Parcel in) {
        studyId = in.readLong();
        questions = in.createTypedArray(Question.CREATOR);
        freeTextQuestions = in.createTypedArray(FreeTextQuestion.CREATOR);
        multipleChoiceQuestions = in.createTypedArray(MultipleChoiceQuestion.CREATOR);
    }

    @Override
    public String toString(){
        return getQuestions().toString();
    }
}
