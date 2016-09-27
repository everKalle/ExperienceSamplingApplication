package com.example.madiskar.experiencesamplingapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.madiskar.experiencesamplingapp.ActiveStudyContract.*;

/**
 * Created by madiskar on 27/09/2016.
 */
public class DBHandler extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ActiveStudies.db";
    public static final String SQL_CREATE_TABLE_STUDY =
            "CREATE TABLE " + ActiveStudyEntry.TABLE_NAME + " ("
                    + ActiveStudyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ActiveStudyEntry.COLUMN_NAME + " TEXT NOT NULL, "
                    + ActiveStudyEntry.COLUMN_BEGINDATE + " TEXT NOT NULL, "
                    + ActiveStudyEntry.COLUMN_ENDDATE + " TEXT NOT NULL, "
                    + ActiveStudyEntry.COLUMN_STUDYLENGTH + " INTEGER NOT NULL, "  // Currently holding dates as text, perhaps use some other format?
                    + ActiveStudyEntry.COLUMN_NOTIFICATIONSPERDAY + " INTEGER NOT NULL, "
                    + ActiveStudyEntry.COLUMN_NOTIFICATIONINTERVAL + " INTEGER NOT NULL, "
                    + ActiveStudyEntry.COLUMN_MINTIMEBETWEENNOTIFICATIONS + " INTEGER NOT NULL, "
                    + ActiveStudyEntry.COLUMN_POSTPONETIME + " INTEGER NOT NULL, "
                    + ActiveStudyEntry.COLUMN_POSTPONABLE + " INTEGER NOT NULL, "
                    + ActiveStudyEntry.COLUMN_CURRENTQUESTION + " INTEGER NOT NULL"
                    + ")";
    public static final String SQL_CREATE_TABLE_QUESTION =
            "CREATE TABLE " + QuestionEntry.TABLE_NAME + " ("
                    + QuestionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + QuestionEntry.COLUMN_TEXT + " TEXT NOT NULL, "
                    + QuestionEntry.COLUMN_ANSWER + " TEXT, "
                    + QuestionEntry.COLUMN_STUDYID + " INTEGER NOT NULL, "
                    + "FOREIGN KEY (" + QuestionEntry.COLUMN_STUDYID + ") REFERENCES " + ActiveStudyEntry.TABLE_NAME + "(" + ActiveStudyEntry._ID + "));";
    public static final String SQL_DELETE_TABLE_STUDY = "DROP TABLE IF EXISTS " + ActiveStudyEntry.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_QUESTION = "DROP TABLE IF EXISTS " + QuestionEntry.TABLE_NAME;


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_STUDY);
        db.execSQL(SQL_CREATE_TABLE_QUESTION);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE_STUDY);
        db.execSQL(SQL_DELETE_TABLE_QUESTION);
        onCreate(db);
    }

    public boolean insertStudy(Study study) {
        // TODO: Implement inserting a study using a Study object
        return false;
    }

    public boolean insertStudy(/*arguments here*/) {
        // TODO: Implement inserting a study using only its data
        return false;
    }

    public int deleteStudyEntry(int studyID) {
        SQLiteDatabase db = this.getWritableDatabase();
        deleteQuestionEntries(studyID);
        return db.delete(ActiveStudyEntry.TABLE_NAME, ActiveStudyEntry._ID + " = ? ", new String[] { Integer.toString(studyID) });
    }

    private int deleteQuestionEntries(int studyID) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(QuestionEntry.TABLE_NAME, QuestionEntry.COLUMN_STUDYID + " = ? ", new String[] { Integer.toString(studyID) });
    }
}
