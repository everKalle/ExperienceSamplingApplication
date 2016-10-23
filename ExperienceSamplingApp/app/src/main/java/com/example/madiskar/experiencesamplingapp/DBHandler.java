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
import java.util.Arrays;
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
                    + QuestionEntry.COLUMN_SINGLECHOICE + " INTEGER, "
                    + QuestionEntry.COLUMN_TYPE + " TEXT, "
                    + QuestionEntry.COLUMN_MULTICHOICES + " TEXT, "
                    + QuestionEntry.COLUMN_STUDYID + " INTEGER NOT NULL, "
                    + "FOREIGN KEY (" + QuestionEntry.COLUMN_STUDYID + ") REFERENCES " + ActiveStudyEntry.TABLE_NAME + "(" + ActiveStudyEntry._ID + "))";
    public static final String SQL_CREATE_TABLE_ANSWERS =
            "CREATE TABLE IF NOT EXISTS " + AnswerEntry.TABLE_NAME + " ("
                    + AnswerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + AnswerEntry.COLUMN_ANSWER + " TEXT NOT NULL, "
                    + AnswerEntry.COLUMN_TIMESTAMP + " TEXT NOT NULL, "
                    + AnswerEntry.COLUMN_STUDYID + " INTEGER NOT NULL, "
                    + "FOREIGN KEY (" + AnswerEntry.COLUMN_STUDYID + ") REFERENCES " + ActiveStudyEntry.TABLE_NAME + "(" + ActiveStudyEntry._ID + "))";
    public static final String SQL_CREATE_TABLE_EVENTS =
            "CREATE TABLE IF NOT EXISTS " + EventEntry.TABLE_NAME + " ("
                    + EventEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + EventEntry.COLUMN_STUDYID + " INTEGER NOT NULL, "
                    + EventEntry.COLUMN_NAME + " TEXT NOT NULL, "
                    + EventEntry.COLUMN_CONTROLTIME + " INTEGER NOT NULL, "
                    + EventEntry.COLUMN_UNIT + " TEXT NOT NULL, "
                    + "FOREIGN KEY (" + EventEntry.COLUMN_STUDYID + ") REFERENCES " + ActiveStudyEntry.TABLE_NAME + "(" + ActiveStudyEntry._ID + "))";
    public static final String SQL_CREATE_TABLE_EVENT_RESULTS =
            "CREATE TABLE IF NOT EXISTS " + EventResultsEntry.TABLE_NAME + " ("
                    + EventResultsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + EventResultsEntry.COLUMN_EVENTID + " INTEGER NOT NULL, "
                    + EventResultsEntry.COLUMN_DURATION + " INTEGER NOT NULL, "
                    + "FOREIGN KEY (" + EventResultsEntry.COLUMN_EVENTID + ") REFERENCES " + EventEntry.TABLE_NAME + "(" + EventEntry._ID + "))";



    public static final String SQL_DELETE_TABLE_STUDY = "DROP TABLE IF EXISTS " + ActiveStudyEntry.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_QUESTION = "DROP TABLE IF EXISTS " + QuestionEntry.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_ANSWERS = "DROP TABLE IF EXISTS " + AnswerEntry.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_EVENTS = "DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_EVENT_RESULTS = "DROP TABLE IF EXISTS " + EventResultsEntry.TABLE_NAME;

    public static synchronized DBHandler getInstance(Context context) {
        // Use application context
        if (mInstance == null)
            mInstance = new DBHandler(context.getApplicationContext());
        return mInstance;
    }

    private synchronized SQLiteDatabase getDbInstance() {
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
        db.execSQL(SQL_CREATE_TABLE_EVENTS);
        db.execSQL(SQL_CREATE_TABLE_EVENT_RESULTS);
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE_STUDY);
        db.execSQL(SQL_DELETE_TABLE_QUESTION);
        db.execSQL(SQL_DELETE_TABLE_ANSWERS);
        db.execSQL(SQL_DELETE_TABLE_EVENTS);
        db.execSQL(SQL_DELETE_TABLE_EVENT_RESULTS);
        onCreate(db);
    }


    public void clearTables() {
        SQLiteDatabase db = getDbInstance();
        db.execSQL("DELETE FROM " + ActiveStudyEntry.TABLE_NAME);
        db.execSQL("DELETE FROM " + QuestionEntry.TABLE_NAME);
        db.execSQL("DELETE FROM " + AnswerEntry.TABLE_NAME);
        db.execSQL("DELETE FROM " + EventEntry.TABLE_NAME);
        db.execSQL("DELETE FROM " + EventResultsEntry.TABLE_NAME);
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
            ArrayList<Question> qs = getStudyQuestions(id);
	        Question[] qsArray = new Question[qs.size()];
            for (int i = 0; i < qs.size(); i++) {
                qsArray[i] = qs.get(i);
            }
            Questionnaire qnaire = new Questionnaire(id, qsArray);
            Event[] events = getStudyEvents(id);
	        Calendar beginDate = stringToCalendar(cur.getString(cur.getColumnIndex(ActiveStudyEntry.COLUMN_BEGINDATE)));
            Calendar endDate = stringToCalendar(cur.getString(cur.getColumnIndex(ActiveStudyEntry.COLUMN_ENDDATE)));
            boolean postPonable = ((cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_POSTPONETIME))) == 1);

            Study newStudy = new Study (
                    id, name, qnaire, beginDate, endDate, studyLength,
                    notificationsPerDay, notificationInterval, postponeTime, postPonable, minTimeBetweenNotification, events);
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
            String text = cur.getString(cur.getColumnIndex(QuestionEntry.COLUMN_TEXT));
            String qType = cur.getString(cur.getColumnIndex(QuestionEntry.COLUMN_TYPE));
            int singleChoice = cur.getInt(cur.getColumnIndex(QuestionEntry.COLUMN_SINGLECHOICE));
            String choicesTxt = cur.getString(cur.getColumnIndex(QuestionEntry.COLUMN_MULTICHOICES));
            //System.out.println(choicesTxt);
            //String[] choices = choicesTxt.split(Pattern.quote(";"));
            Question q;

            if(qType.equals("multiple_choice")) {
                String[] choices = choicesTxt.split(Pattern.quote(";"));
                q = new MultipleChoiceQuestion(studyID, singleChoice, text, choices);
                //System.out.println("getting multiple questions");
            } else
                q = new FreeTextQuestion(studyID, text);
            questions.add(q);
            cur.moveToNext();
        }
        cur.close();
        return questions;
    }

    private Event[] getStudyEvents(long studyID) {
        SQLiteDatabase db = getDbInstance();
        Cursor cur = db.rawQuery("SELECT * FROM " + EventEntry.TABLE_NAME + " WHERE " + EventEntry.COLUMN_STUDYID + " = " + studyID + " ORDER BY " + EventEntry._ID, null);
        cur.moveToFirst();

        ArrayList<Event> eventsArrayList = new ArrayList<>();
        while(!cur.isAfterLast()) {
            long id = cur.getLong(cur.getColumnIndex(EventEntry._ID));
            long studyId = cur.getLong(cur.getColumnIndex(EventEntry.COLUMN_STUDYID));
            String name = cur.getString(cur.getColumnIndex(EventEntry.COLUMN_NAME));
            int controlTime = cur.getInt(cur.getColumnIndex(EventEntry.COLUMN_CONTROLTIME));
            String unit = cur.getString(cur.getColumnIndex(EventEntry.COLUMN_UNIT));

            Event event = new Event(id, studyId, name, controlTime, unit);
            eventsArrayList.add(event);
            cur.moveToNext();
        }
        cur.close();
        Event[] events = new Event[eventsArrayList.size()];
        for (int i = 0; i < eventsArrayList.size(); i++) {
            events[i] = eventsArrayList.get(i);
        }
        return events;
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
        for (Event e : study.getEvents())
            insertEvent(e, study.getId());
        return db.insert(ActiveStudyEntry.TABLE_NAME, null, values);
    }


    public long insertStudy(/*arguments here*/) {
        // TODO: Implement inserting a study using only its data, needed when database connection gets implemented
        return -1;
    }


    private long insertQuestion(Question question, long studyID) {
        SQLiteDatabase db = getDbInstance();
        ContentValues values = new ContentValues();
        values.put(QuestionEntry.COLUMN_TEXT, question.getText());
        if(question instanceof MultipleChoiceQuestion) {
            values.put(QuestionEntry.COLUMN_TYPE, "multiple_choice");
            values.put(QuestionEntry.COLUMN_SINGLECHOICE, ((MultipleChoiceQuestion) question).getSingleChoice());
            StringBuilder sb = new StringBuilder();
            String[] c = ((MultipleChoiceQuestion) question).getChoices();
            for(int i = 0; i < c.length; i ++) {
                if(i != c.length-1)
                    sb.append(c[i]).append(";");
                else
                    sb.append(c[i]);
            }
            values.put(QuestionEntry.COLUMN_MULTICHOICES, sb.toString());
        }
        else
            values.put(QuestionEntry.COLUMN_TYPE, "free_text");
        values.put(QuestionEntry.COLUMN_STUDYID, studyID);
        return db.insert(QuestionEntry.TABLE_NAME, null, values);
    }

    private long insertEvent(Event event, long studyID) {
        SQLiteDatabase db = getDbInstance();
        ContentValues values = new ContentValues();
        values.put(EventEntry.COLUMN_NAME, event.getName());
        values.put(EventEntry.COLUMN_STUDYID, studyID);
        values.put(EventEntry.COLUMN_CONTROLTIME, event.getControlTime());
        values.put(EventEntry.COLUMN_UNIT, event.getUnit());
        return db.insert(EventEntry.TABLE_NAME, null, values);
    }

    public long insertEventResult(long eventId, int duration) {
        SQLiteDatabase db = getDbInstance();
        ContentValues values = new ContentValues();
        values.put(EventResultsEntry.COLUMN_EVENTID, eventId);
        values.put(EventResultsEntry.COLUMN_DURATION, duration);
        return db.insert(EventResultsEntry.TABLE_NAME, null, values);
    }

    public long insertAnswer(long studyId, String answer, String timestamp) {
        SQLiteDatabase db = getDbInstance();
        ContentValues values = new ContentValues();
        values.put(AnswerEntry.COLUMN_STUDYID, studyId);
        values.put(AnswerEntry.COLUMN_ANSWER, answer); // TODO: DANGEROUS, must check input and escape characters if necessary //// ANSWER GOES IN CSV FORMAT: answer1,answer2,answer3,...
        values.put(AnswerEntry.COLUMN_TIMESTAMP, timestamp);
        return db.insert(AnswerEntry.TABLE_NAME, null, values);
    }

    public long insertAnswer(long studyId, String[] answers, String timestamp) {
        SQLiteDatabase db = getDbInstance();
        ContentValues values = new ContentValues();
        values.put(AnswerEntry.COLUMN_STUDYID, studyId);
        StringBuilder sb = new StringBuilder();
        for(String s : answers)
            sb.append(s).append(",");
        sb.deleteCharAt(sb.lastIndexOf(","));
        values.put(AnswerEntry.COLUMN_ANSWER, sb.toString());
        values.put(AnswerEntry.COLUMN_TIMESTAMP, timestamp);
        return db.insert(AnswerEntry.TABLE_NAME, null, values);
    }

    public ArrayList<String> getAnswers(long studyId) {
        SQLiteDatabase db = getDbInstance();
        Cursor cur = db.rawQuery("SELECT * FROM " + AnswerEntry.TABLE_NAME + " WHERE " + AnswerEntry.COLUMN_STUDYID + " = " + studyId + " ORDER BY " + AnswerEntry._ID, null);
        cur.moveToFirst();

        ArrayList<String> answers = new ArrayList<>();
        while(!cur.isAfterLast()) {
            StringBuilder sb = new StringBuilder();
            String timestamp = cur.getString(cur.getColumnIndex(AnswerEntry.COLUMN_TIMESTAMP));
            String answerTxt = cur.getString(cur.getColumnIndex(AnswerEntry.COLUMN_ANSWER));
            sb.append(timestamp).append(" ; ").append(answerTxt);
            answers.add(sb.toString());
            cur.moveToNext();
        }
        cur.close();
        return answers;
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
            Log.v("DBHandler class", "Wrong date format");
        }
        return cal;
    }

}
