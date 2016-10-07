package com.example.madiskar.experiencesamplingapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Joosep on 25.09.2016.
 */
public class MultipleChoiceQuestion extends Question {

    private String[] choices;

    public MultipleChoiceQuestion() {
        super();
    }

    public MultipleChoiceQuestion(long studyId, String question, String[] choices) {
        super(studyId, question);
        this.choices = choices;
    }

    public String[] getChoices() {
        return choices;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out,flags);
        out.writeStringArray(choices);
    }

    public static final Parcelable.Creator<MultipleChoiceQuestion> CREATOR = new Parcelable.Creator<MultipleChoiceQuestion>() {
        @Override
        public MultipleChoiceQuestion createFromParcel(Parcel in) {
            return new MultipleChoiceQuestion(in);
        }

        @Override
        public MultipleChoiceQuestion[] newArray(int size) {
            return new MultipleChoiceQuestion[size];
        }
    };


    public MultipleChoiceQuestion(Parcel in) {
        super(in);
        choices = in.createStringArray();
    }

}
