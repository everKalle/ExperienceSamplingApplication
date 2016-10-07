package com.example.madiskar.experiencesamplingapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Joosep on 25.09.2016.
 */
public class Question implements Parcelable{

    private long studyId;
    private String text;

    public Question() {

    }
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


    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(studyId);
        out.writeString(text);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    protected Question(Parcel in) {
        studyId = in.readLong();
        text = in.readString();
    }
}
