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
    private static DBHandler mInstance = null;
    private SQLiteDatabase db = null;

    private Context mCxt;

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
                    + QuestionEntry.COLUMN_STUDYID + " INTEGER NOT NULL, "
                    + "FOREIGN KEY (" + QuestionEntry.COLUMN_STUDYID + ") REFERENCES " + ActiveStudyEntry.TABLE_NAME + "(" + ActiveStudyEntry._ID + "))";
    public static final String SQL_CREATE_TABLE_ANSWERS =
            "CREATE TABLE IF NOT EXISTS " + AnswerEntry.TABLE_NAME + " ("
                    + AnswerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + AnswerEntry.COLUMN_ANSWER + " TEXT NOT NULL, "
                    + AnswerEntry.COLUMN_TIMESTAMP + " TEXT NOT NULL, "
                    + AnswerEntry.COLUMN_STUDYID + " INTEGER NOT NULL, "
                    + "FOREIGN KEY (" + AnswerEntry.COLUMN_STUDYID + ") REFERENCES " + ActiveStudyEntry.TABLE_NAME + "(" + ActiveStudyEntry._ID + "))";
    public static final String SQL_DELETE_TABLE_STUDY = "DROP TABLE IF EXISTS " + ActiveStudyEntry.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_QUESTION = "DROP TABLE IF EXISTS " + QuestionEntry.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_ANSWERS = "DROP TABLE IF EXISTS " + AnswerEntry.TABLE_NAME;

    public static DBHandler getInstance(Context context) {
        // Use application context
        if (mInstance == null)
            mInstance = new DBHandler(context.getApplicationContext());
        return mInstance;
    }

    private SQLiteDatabase getDbInstance() {
        if (db == null)
            db = mInstance.getWritableDatabase();
        return db;
    }

    private DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mCxt = context;
    }


    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_STUDY);
        db.execSQL(SQL_CREATE_TABLE_QUESTION);
        db.execSQL(SQL_CREATE_TABLE_ANSWERS);
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE_STUDY);
        db.execSQL(SQL_DELETE_TABLE_QUESTION);
        db.execSQL(SQL_DELETE_TABLE_ANSWERS);
        onCreate(db);
    }


    public void clearTables() {
        SQLiteDatabase db = getDbInstance();
        db.execSQL("DELETE FROM " + ActiveStudyEntry.TABLE_NAME);
        db.execSQL("DELETE FROM " + QuestionEntry.TABLE_NAME);
        db.execSQL("DELETE FROM " + AnswerEntry.TABLE_NAME);
    }


    public ArrayList<Study> getAllStudies() {
        SQLiteDatabase db = getDbInstance();
        Cursor cur = db.rawQuery("SELECT * FROM " + ActiveStudyEntry.TABLE_NAME, null);
        cur.moveToFirst();

        ArrayList<Study> studies = new ArrayList<>();
        while (!cur.isAfterLast()) {
            long id = cur.getLong(cur.getColumnIndex(ActiveStudyEntry._ID));
            int studyLength = cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_STUDYLENGTH));
            int notificationsPerDay = cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_NOTIFICATIONSPERDAY));
            int notificationInterval = cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_NOTIFICATIONINTERVAL));
            int minTimeBetweenNotification= cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_MINTIMEBETWEENNOTIFICATIONS));
            int postponeTime = cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_POSTPONETIME));
            String name = cur.getString(cur.getColumnIndex(ActiveStudyEntry.COLUMN_NAME));
            Questionnaire qnaire = new Questionnaire(id, getStudyQuestions(id));
            Calendar beginDate = stringToCalendar(cur.getString(cur.getColumnIndex(ActiveStudyEntry.COLUMN_BEGINDATE)));
            Calendar endDate = stringToCalendar(cur.getString(cur.getColumnIndex(ActiveStudyEntry.COLUMN_ENDDATE)));
            boolean postPonable = ((cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_POSTPONETIME))) == 1);

            Study newStudy = new Study (
                    id, name, qnaire, beginDate, endDate, studyLength,
                    notificationsPerDay, notificationInterval, postponeTime, postPonable, minTimeBetweenNotification);
            studies.add(newStudy);
            cur.moveToNext();
        }
        cur.close();
        return studies;
    }


    private ArrayList<Question> getStudyQuestions(long studyID) {
        SQLiteDatabase db = getDbInstance();
        Cursor cur = db.rawQuery("SELECT * FROM " + QuestionEntry.TABLE_NAME + " WHERE " + QuestionEntry.COLUMN_STUDYID + " = " + studyID + " ORDER BY " + QuestionEntry._ID, null);
        cur.moveToFirst();

        ArrayList<Question> questions = new ArrayList<>();
        while(!cur.isAfterLast()) {
            long id = cur.getLong(cur.getColumnIndex(QuestionEntry._ID));
            //System.out.println(id);
            String text = cur.getString(cur.getColumnIndex(QuestionEntry.COLUMN_TEXT));
            String qType = cur.getString(cur.getColumnIndex(QuestionEntry.COLUMN_TYPE));
            // String[] choices = ...
            //TODO: more stuff here later...
            Question q;
            if(qType.equals("multiple_choice"))
                q = new MultipleChoiceQuestion(studyID, text, new String[] {"choice1", "choice2", "choice3"});
            else
                q = new FreeTextQuestion(studyID, text);
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
        TODO: Implement separate public getStudyQuestions
    }
    */

    public long insertStudy(Study study) {
        SQLiteDatabase db = getDbInstance();
        ContentValues values = new ContentValues();
        values.put(ActiveStudyEntry._ID, study.getId());
        values.put(ActiveStudyEntry.COLUMN_NAME, study.getName());
        values.put(ActiveStudyEntry.COLUMN_BEGINDATE, calendarToString(study.getBeginDate()));
        values.put(ActiveStudyEntry.COLUMN_ENDDATE, calendarToString(study.getEndDate()));
        values.put(ActiveStudyEntry.COLUMN_STUDYLENGTH, study.getStudyLength());
        values.put(ActiveStudyEntry.COLUMN_NOTIFICATIONSPERDAY, study.getNotificationsPerDay());
        values.put(ActiveStudyEntry.COLUMN_NOTIFICATIONINTERVAL, study.getNotificationInterval());
        values.put(ActiveStudyEntry.COLUMN_MINTIMEBETWEENNOTIFICATIONS, study.getMinTimeBetweenNotifications());
        values.put(ActiveStudyEntry.COLUMN_POSTPONETIME, study.getPostponeTime());
        values.put(ActiveStudyEntry.COLUMN_POSTPONABLE, ((study.getPostponable()) ? 1 : 0));
        for(Question q : study.getQuesstionnaire().getQuestions())
            insertQuestion(q, study.getId());
        return db.insert(ActiveStudyEntry.TABLE_NAME, null, values);
    }


    public long insertStudy(/*arguments here*/) {
        // TODO: Implement inserting a study using only its data
        return -1;
    }


    private long insertQuestion(Question question, long studyID) {
        SQLiteDatabase db = getDbInstance();
        ContentValues values = new ContentValues();
        values.put(QuestionEntry.COLUMN_TEXT, question.getText());
        if(question instanceof MultipleChoiceQuestion)
            values.put(QuestionEntry.COLUMN_TYPE, "multiple_choice");
        else
            values.put(QuestionEntry.COLUMN_TYPE, "free_text");
        values.put(QuestionEntry.COLUMN_STUDYID, studyID);
        return db.insert(QuestionEntry.TABLE_NAME, null, values);
    }


    public long insertAnswer(long studyId, String answer, String timestamp) {
        SQLiteDatabase db = getDbInstance();
        ContentValues values = new ContentValues();
        values.put(AnswerEntry.COLUMN_STUDYID, studyId);
        values.put(AnswerEntry.COLUMN_ANSWER, answer); // DANGEROUS, must check input and escape characters if necessary //// ANSWER GOES IN CSV FORMAT: answer1,answer2,answer3,...
        values.put(AnswerEntry.COLUMN_TIMESTAMP, timestamp);
        return db.insert(AnswerEntry.TABLE_NAME, null, values);
    }


    public int deleteStudyEntry(long studyID) {
        SQLiteDatabase db = getDbInstance();
        deleteQuestionEntries(studyID);
        deleteAnswerEntries(studyID);
        return db.delete(ActiveStudyEntry.TABLE_NAME, ActiveStudyEntry._ID + " = ? ", new String[] { Long.toString(studyID) });
    }


    private int deleteQuestionEntries(long studyID) {
        SQLiteDatabase db = getDbInstance();
        return db.delete(QuestionEntry.TABLE_NAME, QuestionEntry.COLUMN_STUDYID + " = ? ", new String[] { Long.toString(studyID) });
    }

    private int deleteAnswerEntries(long studyID) {
        SQLiteDatabase db = getDbInstance();
        return db.delete(AnswerEntry.TABLE_NAME, AnswerEntry.COLUMN_STUDYID + " = ? ", new String[] { Long.toString(studyID) });
    }


    public static String calendarToString(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }


    public static Calendar stringToCalendar(String str) {
        //only from yyyy-MM-dd HH:mm:ss format
        Calendar cal = Calendar.getInstance();
        try {
            String[] parts = str.split(Pattern.quote(" "));
            String[] date = parts[0].split(Pattern.quote("-"));
            String[] time = parts[1].split(Pattern.quote(":"));
            cal.set(Integer.parseInt(date[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(date[2]),
                    Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
            return cal;
        } catch (Exception e) {
            Log.d("DBHandler class", "Wrong date format");
        }
        return cal;
    }

}
