package com.example.madiskar.experiencesamplingapp;

import android.os.Parcel;
import android.os.Parcelable;


public class FreeTextQuestion extends Question {

    public FreeTextQuestion(long studyId, String question) {
        super(studyId, question);
    }

    public static final Parcelable.Creator<FreeTextQuestion> CREATOR = new Parcelable.Creator<FreeTextQuestion>() {

        public FreeTextQuestion createFromParcel(Parcel in) {
            String type = in.readString();
            return new FreeTextQuestion(in);
        }

        public FreeTextQuestion[] newArray(int size) {
            return new FreeTextQuestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString("Freetext");
        super.writeToParcel(out, flags);
    }

    public FreeTextQuestion(Parcel in) {
        super(in);
    }
}
