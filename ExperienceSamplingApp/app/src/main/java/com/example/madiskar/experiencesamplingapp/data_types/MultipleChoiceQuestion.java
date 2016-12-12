package com.example.madiskar.experiencesamplingapp.data_types;

import android.os.Parcel;
import android.os.Parcelable;


public class MultipleChoiceQuestion extends Question {

    private String[] choices;
    private int singleChoice;


    public MultipleChoiceQuestion(long studyId, int singleChoice, String question, String[] choices) {
        super(studyId, question);
        this.choices = choices;
        this.singleChoice = singleChoice;
    }

    public String[] getChoices() {
        return choices;
    }

    public int getSingleChoice() {
        return singleChoice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString("Multiplechoice");
        super.writeToParcel(out,flags);
        out.writeStringArray(choices);
        out.writeInt(singleChoice);
    }

    public static final Parcelable.Creator<MultipleChoiceQuestion> CREATOR = new Parcelable.Creator<MultipleChoiceQuestion>() {
        @Override
        public MultipleChoiceQuestion createFromParcel(Parcel in) {
            String type = in.readString();
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
        singleChoice = in.readInt();
    }

}
