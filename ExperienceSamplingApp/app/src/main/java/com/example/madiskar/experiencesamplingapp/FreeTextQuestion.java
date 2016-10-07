package com.example.madiskar.experiencesamplingapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Joosep on 25.09.2016.
 */
public class FreeTextQuestion extends Question {

    public FreeTextQuestion(long studyId, String question) {
        super(studyId, question);
    }

    public static final Parcelable.Creator<FreeTextQuestion> CREATOR = new Parcelable.Creator<FreeTextQuestion>() {

        public FreeTextQuestion createFromParcel(Parcel in) {
            return new FreeTextQuestion(in);
        }

        public FreeTextQuestion[] newArray(int size) {
            return new FreeTextQuestion[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
    }

    private FreeTextQuestion(Parcel in) {
        super(in);
    }

}
