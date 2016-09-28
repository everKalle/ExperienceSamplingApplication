package com.example.madiskar.experiencesamplingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.madiskar.experiencesamplingapp.ActiveStudyContract.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Created by madiskar on 27/09/2016.
 */
public class DBHandler extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ActiveStudies.db";
    public static final String SQL_CREATE_TABLE_STUDY =
            "CREATE TABLE IF NOT EXISTS " + ActiveStudyEntry.TABLE_NAME + " ("
                    + ActiveStudyEntry._ID + " INTEGER PRIMARY KEY, "
                    + ActiveStudyEntry.COLUMN_NAME + " TEXT NOT NULL, "
                    + ActiveStudyEntry.COLUMN_BEGINDATE + " TEXT NOT NULL, "
                    + ActiveStudyEntry.COLUMN_ENDDATE + " TEXT NOT NULL, "
                    + ActiveStudyEntry.COLUMN_STUDYLENGTH + " INTEGER NOT NULL, "
                    + ActiveStudyEntry.COLUMN_NOTIFICATIONSPERDAY + " INTEGER NOT NULL, "
                    + ActiveStudyEntry.COLUMN_NOTIFICATIONINTERVAL + " INTEGER NOT NULL, "
                    + ActiveStudyEntry.COLUMN_MINTIMEBETWEENNOTIFICATIONS + " INTEGER NOT NULL, "
                    + ActiveStudyEntry.COLUMN_POSTPONETIME + " INTEGER NOT NULL, "
                    + ActiveStudyEntry.COLUMN_POSTPONABLE + " INTEGER NOT NULL "
                    + ")";
    public static final String SQL_CREATE_TABLE_QUESTION =
            "CREATE TABLE IF NOT EXISTS " + QuestionEntry.TABLE_NAME + " ("
                    + QuestionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + QuestionEntry.COLUMN_TEXT + " TEXT NOT NULL, "
                    + QuestionEntry.COLUMN_TYPE + " TEXT NOT NULL, "
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


    public void clearTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + ActiveStudyEntry.TABLE_NAME);
        db.execSQL("DELETE FROM " + QuestionEntry.TABLE_NAME);
    }


    public ArrayList<Study> getAllStudies() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + ActiveStudyEntry.TABLE_NAME, null);
        cur.moveToFirst();

        ArrayList<Study> studies = new ArrayList<>();
        while (!cur.isAfterLast()) {
            int id = cur.getInt(cur.getColumnIndex(ActiveStudyEntry._ID));
            int studyLength = cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_STUDYLENGTH));
            int notificationsPerDay = cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_NOTIFICATIONSPERDAY));
            int notificationInterval = cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_NOTIFICATIONINTERVAL));
            int minTimeBetweenNotification= cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_MINTIMEBETWEENNOTIFICATIONS));
            int postponeTime = cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_POSTPONETIME));
            String name = cur.getString(cur.getColumnIndex(ActiveStudyEntry.COLUMN_NAME));
            ArrayList<Question> studyQuestions = getStudyQuestions(db, id);
            Calendar beginDate = stringToCalendar(cur.getString(cur.getColumnIndex(ActiveStudyEntry.COLUMN_BEGINDATE)));
            Calendar endDate = stringToCalendar(cur.getString(cur.getColumnIndex(ActiveStudyEntry.COLUMN_ENDDATE)));
            boolean postPonable = ((cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_POSTPONETIME))) == 1);

            Study newStudy = new Study (
                    id, name, studyQuestions, beginDate, endDate, studyLength,
                    notificationsPerDay, notificationInterval, postponeTime, postPonable, minTimeBetweenNotification);
            studies.add(newStudy);
            cur.moveToNext();
        }
        cur.close();
        //db.close();
        return studies;
    }


    private ArrayList<Question> getStudyQuestions(SQLiteDatabase db, int studyID) {
        Cursor cur = db.rawQuery("SELECT * FROM " + QuestionEntry.TABLE_NAME + " WHERE " + QuestionEntry.COLUMN_STUDYID + " = " + studyID, null);
        cur.moveToFirst();

        ArrayList<Question> questions = new ArrayList<>();
        while(!cur.isAfterLast()) {
            String text = cur.getString(cur.getColumnIndex(QuestionEntry.COLUMN_TEXT));
            String qType = cur.getString(cur.getColumnIndex(QuestionEntry.COLUMN_TYPE));
            //TODO: more stuff here later...
            Question q;
            if(qType.equals("multiple_choice"))
                q = new MultipleChoiceQuestion(text);
            else
                q = new FreeTextQuestion(text);
            questions.add(q);
            cur.moveToNext();
        }
        cur.close();
        return questions;
    }


    /*
    public ArrayList<Question> getStudyQuestions(int studyID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor query = db.rawQuery("SELECT * FROM " + QuestionEntry.TABLE_NAME + " WHERE " + QuestionEntry.COLUMN_STUDYID + " = " + studyID, null);
        TODO: Implement separate getStudyQuestions
    }
    */

    public long insertStudy(Study study) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ActiveStudyEntry._ID, study.getId());
        values.put(ActiveStudyEntry.COLUMN_NAME, study.getName());
        values.put(ActiveStudyEntry.COLUMN_BEGINDATE, calendarToString(study.getBegimnDate()));
        values.put(ActiveStudyEntry.COLUMN_ENDDATE, calendarToString(study.getEndDatendDate()));
        values.put(ActiveStudyEntry.COLUMN_STUDYLENGTH, study.getStudyLength());
        values.put(ActiveStudyEntry.COLUMN_NOTIFICATIONSPERDAY, study.getNotificationsPerDay());
        values.put(ActiveStudyEntry.COLUMN_NOTIFICATIONINTERVAL, study.getNotificationInterval());
        values.put(ActiveStudyEntry.COLUMN_MINTIMEBETWEENNOTIFICATIONS, study.getMinTimeBetweenNotifications());
        values.put(ActiveStudyEntry.COLUMN_POSTPONETIME, study.getPostponeTime());
        values.put(ActiveStudyEntry.COLUMN_POSTPONABLE, ((study.getPostponable()) ? 1 : 0));
        for(Question q : study.getQuestions())
            insertQuestion(db, q, study.getId());
        //db.close();
        return db.insert(ActiveStudyEntry.TABLE_NAME, null, values);
    }


    public long insertStudy(/*arguments here*/) {
        // TODO: Implement inserting a study using only its data
        return -1;
    }


    private long insertQuestion(SQLiteDatabase db, Question question, long studyID) {
        ContentValues values = new ContentValues();
        values.put(QuestionEntry.COLUMN_TEXT, question.getText());
        if(question instanceof MultipleChoiceQuestion)
            values.put(QuestionEntry.COLUMN_TYPE, "multiple_choice");
        else
            values.put(QuestionEntry.COLUMN_TYPE, "free_text");
        values.put(QuestionEntry.COLUMN_STUDYID, studyID);
        return db.insert(QuestionEntry.TABLE_NAME, null, values);
    }


    //TODO: Implement a method to add answers to questions the database


    public int deleteStudyEntry(int studyID) {
        SQLiteDatabase db = this.getWritableDatabase();
        deleteQuestionEntries(studyID);
        //db.close();
        return db.delete(ActiveStudyEntry.TABLE_NAME, ActiveStudyEntry._ID + " = ? ", new String[] { Integer.toString(studyID) });
    }


    private int deleteQuestionEntries(int studyID) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.close();
        return db.delete(QuestionEntry.TABLE_NAME, QuestionEntry.COLUMN_STUDYID + " = ? ", new String[] { Integer.toString(studyID) });
    }


    //TODO: add time support, currently only date
    public static String calendarToString(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy"); //perhaps get local dateformat
        return sdf.format(cal.getTime());
    }


    //TODO: add time support, currently only date
    public static Calendar stringToCalendar(String str) {
        //only from dd.MM.yyyy format
        Calendar cal = Calendar.getInstance();
        try {
            String[] parts = str.split(Pattern.quote("."));
            cal.set(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[0]));
            return cal;
        } catch (Exception e) {
            Log.d("DBHandler class", "Wrong date format");
        }
        return cal;
    }
}
