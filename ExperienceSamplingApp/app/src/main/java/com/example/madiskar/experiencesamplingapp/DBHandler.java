package com.example.madiskar.experiencesamplingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.madiskar.experiencesamplingapp.ActiveStudyContract.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Pattern;


public class DBHandler extends SQLiteOpenHelper {
    private static DBHandler mInstance = null;
    //private SQLiteDatabase db = null;


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
                    + ActiveStudyEntry.COLUMN_MINTIMEBETWEENNOTIFICATIONS + " INTEGER NOT NULL, "
                    + ActiveStudyEntry.COLUMN_POSTPONETIME + " INTEGER NOT NULL, "
                    + ActiveStudyEntry.COLUMN_POSTPONABLE + " INTEGER NOT NULL, "
                    + ActiveStudyEntry.COLUMN_ISPUBLIC + " INTEGER NOT NULL, "
                    + ActiveStudyEntry.COLUMN_DEFAULTBEEPFREE + " TEXT"
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
                    + EventEntry._ID + " INTEGER PRIMARY KEY, "
                    + EventEntry.COLUMN_STUDYID + " INTEGER NOT NULL, "
                    + EventEntry.COLUMN_NAME + " TEXT NOT NULL, "
                    + EventEntry.COLUMN_CONTROLTIME + " INTEGER NOT NULL, "
                    + EventEntry.COLUMN_UNIT + " TEXT NOT NULL, "
                    + "FOREIGN KEY (" + EventEntry.COLUMN_STUDYID + ") REFERENCES " + ActiveStudyEntry.TABLE_NAME + "(" + ActiveStudyEntry._ID + "))";
    public static final String SQL_CREATE_TABLE_EVENT_RESULTS =
            "CREATE TABLE IF NOT EXISTS " + EventResultsEntry.TABLE_NAME + " ("
                    + EventResultsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + EventResultsEntry.COLUMN_EVENTID + " INTEGER NOT NULL, "
                    + EventResultsEntry.COLUMN_STARTTIME + " TEXT NOT NULL, "
                    + EventResultsEntry.COLUMN_ENDTIME + " TEXT NOT NULL, "
                    + "FOREIGN KEY (" + EventResultsEntry.COLUMN_EVENTID + ") REFERENCES " + EventEntry.TABLE_NAME + "(" + EventEntry._ID + "))";
    public static final String SQL_CREATE_TABLE_BEEPFREE_PERIODS =
            "CREATE TABLE IF NOT EXISTS " + BeepFreePeriodEntry.TABLE_NAME + " ("
                    + BeepFreePeriodEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + BeepFreePeriodEntry.BEEPFREE_TIME + " TEXT NOT NULL )";



    public static final String SQL_DELETE_TABLE_STUDY = "DROP TABLE IF EXISTS " + ActiveStudyEntry.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_QUESTION = "DROP TABLE IF EXISTS " + QuestionEntry.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_ANSWERS = "DROP TABLE IF EXISTS " + AnswerEntry.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_EVENTS = "DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_EVENT_RESULTS = "DROP TABLE IF EXISTS " + EventResultsEntry.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_BEEPFREE_PERIODS = "DROP TABLE IF EXISTS " + BeepFreePeriodEntry.TABLE_NAME;

    public static synchronized DBHandler getInstance(Context context) {
        if (mInstance == null)
            mInstance = new DBHandler(context.getApplicationContext());
        return mInstance;
    }

    private DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_STUDY);
        db.execSQL(SQL_CREATE_TABLE_QUESTION);
        db.execSQL(SQL_CREATE_TABLE_ANSWERS);
        db.execSQL(SQL_CREATE_TABLE_EVENTS);
        db.execSQL(SQL_CREATE_TABLE_EVENT_RESULTS);
        db.execSQL(SQL_CREATE_TABLE_BEEPFREE_PERIODS);
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE_STUDY);
        db.execSQL(SQL_DELETE_TABLE_QUESTION);
        db.execSQL(SQL_DELETE_TABLE_ANSWERS);
        db.execSQL(SQL_DELETE_TABLE_EVENTS);
        db.execSQL(SQL_DELETE_TABLE_EVENT_RESULTS);
        db.execSQL(SQL_DELETE_TABLE_BEEPFREE_PERIODS);
        onCreate(db);
    }


    public void clearTables() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("DELETE FROM " + ActiveStudyEntry.TABLE_NAME);
            db.execSQL("DELETE FROM " + QuestionEntry.TABLE_NAME);
            db.execSQL("DELETE FROM " + AnswerEntry.TABLE_NAME);
            db.execSQL("DELETE FROM " + EventEntry.TABLE_NAME);
            db.execSQL("DELETE FROM " + EventResultsEntry.TABLE_NAME);
            //db.execSQL("DELETE FROM " + BeepFreePeriodEntry.TABLE_NAME);
            db.setTransactionSuccessful();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public long clearStudyAnswers(long studyId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(AnswerEntry.TABLE_NAME, AnswerEntry.COLUMN_STUDYID + " = ? ", new String[]{Long.toString(studyId)});
    }


    public void clearAnswersTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + AnswerEntry.TABLE_NAME);
    }

    public long deleteAnswerEntry(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(AnswerEntry.TABLE_NAME, AnswerEntry._ID + " = ? ", new String[]{Long.toString(id)});
    }

    public long deleteEventResultEntry(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(EventResultsEntry.TABLE_NAME, EventResultsEntry._ID + " = ? ", new String[]{Long.toString(id)});
    }


    public long clearEventResults(long eventId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(EventResultsEntry.TABLE_NAME, EventResultsEntry.COLUMN_EVENTID + " = ? ", new String[]{Long.toString(eventId)});
    }


    public void clearEventResultsTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + EventResultsEntry.TABLE_NAME);
    }


    public boolean isStudyInDb(Study s) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + ActiveStudyEntry.TABLE_NAME + " WHERE " + ActiveStudyEntry._ID + " = " + s.getId() + " ORDER BY " + ActiveStudyEntry._ID, null);
        boolean returnVal = cur.getCount() > 0;
        cur.close();
        return returnVal;
    }


    public ArrayList<Study> getAllStudies() {
        SQLiteDatabase db = getReadableDatabase();

        db.beginTransaction();
        Cursor cur = db.rawQuery("SELECT * FROM " + ActiveStudyEntry.TABLE_NAME, null);
        cur.moveToFirst();

        ArrayList<Study> studies = new ArrayList<>();
        try {
            while (!cur.isAfterLast()) {
                long id = cur.getLong(cur.getColumnIndex(ActiveStudyEntry._ID));
                int studyLength = cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_STUDYLENGTH));
                int notificationsPerDay = cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_NOTIFICATIONSPERDAY));
                int minTimeBetweenNotification = cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_MINTIMEBETWEENNOTIFICATIONS));
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
                boolean postPonable = ((cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_POSTPONABLE))) == 1);
                boolean isPublic = ((cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_ISPUBLIC))) == 1);
                String[] defBeepFree = (cur.getString(cur.getColumnIndex(ActiveStudyEntry.COLUMN_DEFAULTBEEPFREE))).split(Pattern.quote(" "));
                BeepFerePeriod defaultBeepFree = stringToBeepFree(0, defBeepFree[0], defBeepFree[1]);

                Study newStudy = new Study(
                        id, name, qnaire, beginDate, endDate, studyLength,
                        notificationsPerDay, postponeTime, postPonable, minTimeBetweenNotification, events, defaultBeepFree, isPublic);
                studies.add(newStudy);
                cur.moveToNext();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            cur.close();
        }
        return studies;
    }


    public ArrayList<Question> getStudyQuestions(long studyID) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cur = db.rawQuery("SELECT * FROM " + QuestionEntry.TABLE_NAME + " WHERE " + QuestionEntry.COLUMN_STUDYID + " = " + studyID + " ORDER BY " + QuestionEntry._ID, null);

        ArrayList<Question> questions = new ArrayList<>();
        try {
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                long id = cur.getLong(cur.getColumnIndex(QuestionEntry._ID));
                String text = cur.getString(cur.getColumnIndex(QuestionEntry.COLUMN_TEXT));
                String qType = cur.getString(cur.getColumnIndex(QuestionEntry.COLUMN_TYPE));
                int singleChoice = cur.getInt(cur.getColumnIndex(QuestionEntry.COLUMN_SINGLECHOICE));
                String choicesTxt = cur.getString(cur.getColumnIndex(QuestionEntry.COLUMN_MULTICHOICES));
                Question q;

                if (qType.equals("multiple_choice")) {
                    String[] choices = choicesTxt.split(Pattern.quote(";"));
                    q = new MultipleChoiceQuestion(studyID, singleChoice, text, choices);
                } else
                    q = new FreeTextQuestion(studyID, text);
                questions.add(q);
                cur.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cur.close();
        }
        return questions;
    }


    public Study getStudy(long id) {
        SQLiteDatabase db = getReadableDatabase();

        db.beginTransaction();
        Cursor cur = db.rawQuery("SELECT * FROM " + ActiveStudyEntry.TABLE_NAME + " WHERE " + ActiveStudyEntry._ID + " = " + id + " ORDER BY " + ActiveStudyEntry._ID, null);
        cur.moveToFirst();

        Study s = null;
        try{
            int studyLength = cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_STUDYLENGTH));
            int notificationsPerDay = cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_NOTIFICATIONSPERDAY));
            int minTimeBetweenNotification = cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_MINTIMEBETWEENNOTIFICATIONS));
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
            boolean postPonable = ((cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_POSTPONABLE))) == 1);
            boolean isPublic = ((cur.getInt(cur.getColumnIndex(ActiveStudyEntry.COLUMN_ISPUBLIC))) == 1);
            String[] defBeepFree = (cur.getString(cur.getColumnIndex(ActiveStudyEntry.COLUMN_DEFAULTBEEPFREE))).split(Pattern.quote(" "));
            BeepFerePeriod defaultBeepFree = stringToBeepFree(0, defBeepFree[0], defBeepFree[1]);

            Study newStudy = new Study(
                    id, name, qnaire, beginDate, endDate, studyLength,
                    notificationsPerDay, postponeTime, postPonable, minTimeBetweenNotification, events, defaultBeepFree, isPublic);
            s = newStudy;
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            cur.close();
        }
        return s;
    }


    public Event[] getStudyEvents(long studyID) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cur = db.rawQuery("SELECT * FROM " + EventEntry.TABLE_NAME + " WHERE " + EventEntry.COLUMN_STUDYID + " = " + studyID + " ORDER BY " + EventEntry._ID, null);

        ArrayList<Event> eventsArrayList = new ArrayList<>();
        try {
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                long id = cur.getLong(cur.getColumnIndex(EventEntry._ID));
                long studyId = cur.getLong(cur.getColumnIndex(EventEntry.COLUMN_STUDYID));
                String name = cur.getString(cur.getColumnIndex(EventEntry.COLUMN_NAME));
                int controlTime = cur.getInt(cur.getColumnIndex(EventEntry.COLUMN_CONTROLTIME));
                String unit = cur.getString(cur.getColumnIndex(EventEntry.COLUMN_UNIT));

                Event event = new Event(id, studyId, name, controlTime, unit);
                eventsArrayList.add(event);
                cur.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cur.close();
        }
        Event[] events = new Event[eventsArrayList.size()];
        for (int i = 0; i < eventsArrayList.size(); i++) {
            events[i] = eventsArrayList.get(i);
        }
        return events;
    }



    public long insertStudy(Study study) {
        SQLiteDatabase db = getWritableDatabase();
        long returnValue = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(ActiveStudyEntry._ID, study.getId());
            values.put(ActiveStudyEntry.COLUMN_NAME, study.getName());
            values.put(ActiveStudyEntry.COLUMN_BEGINDATE, calendarToString(study.getBeginDate()));
            values.put(ActiveStudyEntry.COLUMN_ENDDATE, calendarToString(study.getEndDate()));
            values.put(ActiveStudyEntry.COLUMN_STUDYLENGTH, study.getStudyLength());
            values.put(ActiveStudyEntry.COLUMN_NOTIFICATIONSPERDAY, study.getNotificationsPerDay());
            values.put(ActiveStudyEntry.COLUMN_MINTIMEBETWEENNOTIFICATIONS, study.getMinTimeBetweenNotifications());
            values.put(ActiveStudyEntry.COLUMN_POSTPONETIME, study.getPostponeTime());
            values.put(ActiveStudyEntry.COLUMN_POSTPONABLE, ((study.getPostponable()) ? 1 : 0));
            values.put(ActiveStudyEntry.COLUMN_ISPUBLIC, ((study.isPublic()) ? 1 : 0));
            values.put(ActiveStudyEntry.COLUMN_DEFAULTBEEPFREE, (study.getDefaultBeepFree().getPeriodAsString()));
            for (Question q : study.getQuesstionnaire().getQuestions())
                insertQuestion(q, study.getId());
            for (Event e : study.getEvents())
                insertEvent(e, study.getId());
            returnValue = db.insert(ActiveStudyEntry.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return returnValue;
    }


    public long updateStudyEntry(Study study) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        long returnValue = -1;

        db.beginTransaction();
        try {
            values.put(ActiveStudyEntry.COLUMN_NAME, study.getName());
            values.put(ActiveStudyEntry.COLUMN_BEGINDATE, calendarToString(study.getBeginDate()));
            values.put(ActiveStudyEntry.COLUMN_ENDDATE, calendarToString(study.getEndDate()));
            values.put(ActiveStudyEntry.COLUMN_STUDYLENGTH, study.getStudyLength());
            values.put(ActiveStudyEntry.COLUMN_NOTIFICATIONSPERDAY, study.getNotificationsPerDay());
            values.put(ActiveStudyEntry.COLUMN_MINTIMEBETWEENNOTIFICATIONS, study.getMinTimeBetweenNotifications());
            values.put(ActiveStudyEntry.COLUMN_POSTPONETIME, study.getPostponeTime());
            values.put(ActiveStudyEntry.COLUMN_POSTPONABLE, ((study.getPostponable()) ? 1 : 0));
            values.put(ActiveStudyEntry.COLUMN_ISPUBLIC, ((study.isPublic()) ? 1 : 0));
            values.put(ActiveStudyEntry.COLUMN_DEFAULTBEEPFREE, (study.getDefaultBeepFree().getPeriodAsString()));
            db.delete(QuestionEntry.TABLE_NAME, QuestionEntry.COLUMN_STUDYID + " = ? ", new String[]{Long.toString(study.getId())});
            for (Question q : study.getQuesstionnaire().getQuestions())
                insertQuestion(q, study.getId());
            db.delete(EventEntry.TABLE_NAME, EventEntry.COLUMN_STUDYID + " = ? ", new String[]{Long.toString(study.getId())});
            for (Event e : study.getEvents())
                insertEvent(e, study.getId());
            returnValue = db.update(ActiveStudyEntry.TABLE_NAME, values, ActiveStudyEntry._ID + "=" + study.getId(), null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return returnValue;
    }


    private long updateEventEntry(Event event) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EventEntry.COLUMN_NAME, event.getName());
        values.put(EventEntry.COLUMN_CONTROLTIME, event.getControlTime());
        values.put(EventEntry.COLUMN_UNIT, event.getUnit());
        return db.update(EventEntry.TABLE_NAME, values, EventEntry._ID + "=" + event.getId(), null);
    }


    public ArrayList<String[]> getAllEventResults() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + EventResultsEntry.TABLE_NAME + " ORDER BY " + EventResultsEntry._ID, null);

        cur.moveToFirst();
        // save to array as "id,eventId,startTime,endTime"
        ArrayList<String[]> results = new ArrayList<>();
        try {
            while (!cur.isAfterLast()) {
                String[] elems = new String[4];
                long id = cur.getLong(cur.getColumnIndex(EventResultsEntry._ID));
                long eventId = cur.getLong(cur.getColumnIndex(EventResultsEntry.COLUMN_EVENTID));
                String startTime = cur.getString(cur.getColumnIndex(EventResultsEntry.COLUMN_STARTTIME));
                String endTime = cur.getString(cur.getColumnIndex(EventResultsEntry.COLUMN_ENDTIME));
                elems[0] = Long.toString(id);
                elems[1] = Long.toString(eventId);
                elems[2] = startTime;
                elems[3] = endTime;
                results.add(elems);
                cur.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cur.close();
        }
        return results;
    }


    public long insertBeepFreePeriod(BeepFerePeriod bfp) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BeepFreePeriodEntry._ID, bfp.getId());
        values.put(BeepFreePeriodEntry.BEEPFREE_TIME, bfp.toString());
        return db.insert(BeepFreePeriodEntry.TABLE_NAME, null, values);
    }



    public ArrayList<BeepFerePeriod> getBeepFreePeriods() {
        SQLiteDatabase db = getReadableDatabase();

        //db.beginTransaction();
        Cursor cur = db.rawQuery("SELECT * FROM " + BeepFreePeriodEntry.TABLE_NAME, null);
        cur.moveToFirst();

        ArrayList<BeepFerePeriod> beepFerePeriods = new ArrayList<>();

        try {
            while (!cur.isAfterLast()) {
                long id = cur.getLong(cur.getColumnIndex(BeepFreePeriodEntry._ID));
                String beepfreeTime = cur.getString(cur.getColumnIndex(BeepFreePeriodEntry.BEEPFREE_TIME));
                //Log.v("WOOOOI", beepfreeTime);
                String[] parts = beepfreeTime.split(":");
                //Log.v("wot is dis", String.valueOf(parts.length));
                BeepFerePeriod beepFerePeriod = stringToBeepFreeWithDots((int) id, parts[0], parts[1]);
                beepFerePeriods.add(beepFerePeriod);
                cur.moveToNext();
            }
            //db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //db.endTransaction();
            cur.close();
        }
        return beepFerePeriods;
    }

    public long editBeepFree(BeepFerePeriod bfp) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(BeepFreePeriodEntry.BEEPFREE_TIME, bfp.toString());
        return db.update(BeepFreePeriodEntry.TABLE_NAME, cv, BeepFreePeriodEntry._ID + " =" +bfp.getId(), null);
    }


    private long insertQuestion(Question question, long studyID) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(QuestionEntry.COLUMN_TEXT, question.getText());
        if (question instanceof MultipleChoiceQuestion) {
            values.put(QuestionEntry.COLUMN_TYPE, "multiple_choice");
            values.put(QuestionEntry.COLUMN_SINGLECHOICE, ((MultipleChoiceQuestion) question).getSingleChoice());
            StringBuilder sb = new StringBuilder();
            String[] c = ((MultipleChoiceQuestion) question).getChoices();
            for (int i = 0; i < c.length; i++) {
                if (i != c.length - 1)
                    sb.append(c[i]).append(";");
                else
                    sb.append(c[i]);
            }
            values.put(QuestionEntry.COLUMN_MULTICHOICES, sb.toString());
        } else
            values.put(QuestionEntry.COLUMN_TYPE, "free_text");
        values.put(QuestionEntry.COLUMN_STUDYID, studyID);
        return db.insert(QuestionEntry.TABLE_NAME, null, values);
    }

    private long insertEvent(Event event, long studyID) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EventEntry._ID, event.getId());
        values.put(EventEntry.COLUMN_NAME, event.getName());
        values.put(EventEntry.COLUMN_STUDYID, studyID);
        values.put(EventEntry.COLUMN_CONTROLTIME, event.getControlTime());
        values.put(EventEntry.COLUMN_UNIT, event.getUnit());
        return db.insert(EventEntry.TABLE_NAME, null, values);
    }

    public long insertEventResult(long eventId, String startTime, String endTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EventResultsEntry.COLUMN_EVENTID, eventId);
        values.put(EventResultsEntry.COLUMN_STARTTIME, startTime);
        values.put(EventResultsEntry.COLUMN_ENDTIME, endTime);
        return db.insert(EventResultsEntry.TABLE_NAME, null, values);
    }

    public long insertAnswer(long studyId, String answer, String timestamp) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AnswerEntry.COLUMN_STUDYID, studyId);
        values.put(AnswerEntry.COLUMN_ANSWER, answer);
        values.put(AnswerEntry.COLUMN_TIMESTAMP, timestamp);
        return db.insert(AnswerEntry.TABLE_NAME, null, values);
    }

    public ArrayList<String> getStudyAnswers(long studyId) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cur = db.rawQuery("SELECT * FROM " + AnswerEntry.TABLE_NAME + " WHERE " + AnswerEntry.COLUMN_STUDYID + " = " + studyId + " ORDER BY " + AnswerEntry._ID, null);

        // studyId,timestamp,answertxt
        ArrayList<String> answers = new ArrayList<>();
        try {
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                StringBuilder sb = new StringBuilder();
                String timestamp = cur.getString(cur.getColumnIndex(AnswerEntry.COLUMN_TIMESTAMP));
                String answerTxt = cur.getString(cur.getColumnIndex(AnswerEntry.COLUMN_ANSWER));
                sb.append(studyId).append(timestamp).append(",").append(answerTxt);
                answers.add(sb.toString());
                cur.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cur.close();
        }
        return answers;
    }


    public ArrayList<String[]> getAllAnswers() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cur = db.rawQuery("SELECT * FROM " + AnswerEntry.TABLE_NAME + " ORDER BY " + AnswerEntry._ID, null);

        // id,studyId,timestamp,answertxt
        ArrayList<String[]> answers = new ArrayList<>();
        try {
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                String[] elems = new String[4];
                long id = cur.getLong(cur.getColumnIndex(AnswerEntry._ID));
                String timestamp = cur.getString(cur.getColumnIndex(AnswerEntry.COLUMN_TIMESTAMP));
                long studyId = cur.getLong(cur.getColumnIndex(AnswerEntry.COLUMN_STUDYID));
                String answerTxt = cur.getString(cur.getColumnIndex(AnswerEntry.COLUMN_ANSWER));
                elems[0] = Long.toString(id);
                elems[1] = Long.toString(studyId);
                elems[2] = timestamp;
                elems[3] = answerTxt;
                answers.add(elems);
                cur.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cur.close();
        }
        return answers;
    }


    public boolean deleteStudyEntry(long studyID) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(QuestionEntry.TABLE_NAME, QuestionEntry.COLUMN_STUDYID + " = ? ", new String[]{Long.toString(studyID)});
            //db.delete(AnswerEntry.TABLE_NAME, AnswerEntry.COLUMN_STUDYID + " = ? ", new String[]{Long.toString(studyID)});
            db.delete(EventEntry.TABLE_NAME, EventEntry.COLUMN_STUDYID + " = ? ", new String[]{Long.toString(studyID)});
            //Event[] studyEvents = getStudyEvents(studyID);
            //for(Event e : studyEvents) {
            //    clearEventResults(e.getId());
            //}
            db.delete(ActiveStudyEntry.TABLE_NAME, ActiveStudyEntry._ID + " = ? ", new String[]{Long.toString(studyID)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
        return true;
    }


    public boolean deleteBeepFreeEntry(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(BeepFreePeriodEntry.TABLE_NAME, BeepFreePeriodEntry._ID + " =?", new String[]{Long.toString(id)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
        return true;
    }



    public static String calendarToString(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }


    public static Calendar stringToCalendar(String str) {
        //only from yyyy-MM-dd HH:mm:ss format
        Calendar cal = Calendar.getInstance();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        try {
            cal.setTime(new SimpleDateFormat(pattern).parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }
    

    public static String hashSha256(String s) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            md.update(s.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] digest = md.digest();
        return String.format("%064x", new java.math.BigInteger(1, digest));
    }


    public static BeepFerePeriod stringToBeepFree(int id, String start, String end) {
        // start and end are defined as HH:mm:ss
        String[] startparts = start.split(Pattern.quote(":"));
        String[] endparts = end.split(Pattern.quote(":"));

        int startTimeHour = Integer.parseInt(startparts[0]);
        int startTimeMinute = Integer.parseInt(startparts[1]);

        int endTimeHour = Integer.parseInt(endparts[0]);
        int endTimeMinute = Integer.parseInt(endparts[1]);

        return new BeepFerePeriod(id, startTimeHour, startTimeMinute, endTimeHour, endTimeMinute);
    }

    public static BeepFerePeriod stringToBeepFreeWithDots(int id, String start, String end) {
        String[] startparts = start.split(Pattern.quote("."));
        String[] endparts = end.split(Pattern.quote("."));

        int startTimeHour = Integer.parseInt(startparts[0]);
        int startTimeMinute = Integer.parseInt(startparts[1]);

        int endTimeHour = Integer.parseInt(endparts[0]);
        int endTimeMinute = Integer.parseInt(endparts[1]);

        return new BeepFerePeriod(id, startTimeHour, startTimeMinute, endTimeHour, endTimeMinute);
    }


    public static JSONArray parseJsonString(String json) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }


    public static ArrayList<Study> jsonArrayToStudyArray(JSONArray jsonArray, boolean blockFinishedStudies) {
        ArrayList<Study> studyList = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonStudy = jsonArray.getJSONObject(i);
                long studyID = jsonStudy.getLong("id");
                String name = jsonStudy.getString("study-title");
                int notificationsPerDay = jsonStudy.getInt("study-beeps-per-day");
                int minTimeBetweenNotifications = jsonStudy.getInt("study-min-time-between-beeps");
                String joinDate = jsonStudy.getString("join_date");
                Calendar realStartDate = stringToCalendar(jsonStudy.getString("study-start-date"));
                BeepFerePeriod defaultBeepFree = stringToBeepFree(0, jsonStudy.getString("study-beep-end-time"), jsonStudy.getString("study-beep-start-time"));
                int postponeTime = jsonStudy.getInt("study-postpone-time");
                boolean allowPostpone = (jsonStudy.getInt("study-allow-postpone") == 1);
                boolean studyDurationForUser = (jsonStudy.getInt("study-duration-for-user") == 1);
                boolean isPublic = (jsonStudy.getInt("study-is-public") == 1);
                Calendar beginDate = stringToCalendar(joinDate);
                Calendar endDate = stringToCalendar(joinDate);
                if(beginDate.before(realStartDate)) {
                    beginDate = realStartDate;
                    endDate = realStartDate;
                }
                Calendar realEndDate = stringToCalendar(jsonStudy.getString("study-end-date"));
                int studyLengthForUser = jsonStudy.getInt("study-duration-time");
                if(studyDurationForUser) {
                    endDate.add(Calendar.MINUTE, studyLengthForUser);
                    if(endDate.after(realEndDate))
                        endDate = realEndDate;
                } else {
                    endDate = realEndDate;
                }
                if(blockFinishedStudies) {
                    if (endDate.before(Calendar.getInstance())) {
                        Log.i("Study over, do not add", name);
                        continue;
                    }
                }

                JSONArray questionArray = jsonStudy.getJSONArray("questions");
                Question[] qs = new Question[questionArray.length()];
                for(int j = 0; j < questionArray.length(); j++) {
                    JSONObject jsonQuestion = questionArray.getJSONObject(j);
                    String title = jsonQuestion.getString("question-title");
                    String type = jsonQuestion.getString("question-type");
                    Question q = null;
                    if(type.equals("freetext")) {
                        q = new FreeTextQuestion(studyID, title);
                    } else if(type.equals("multichoice")) {
                        String choicesAsString = jsonQuestion.getString("question-multichoices");
                        choicesAsString = choicesAsString.substring(1, choicesAsString.length() - 1);
                        String[] choices = choicesAsString.split(Pattern.quote(","));
                        for(int k = 0; k < choices.length; k++) {
                            choices[k] = choices[k].substring(1, choices[k].length() - 1);
                        }
                        int singleChoice = jsonQuestion.getInt("question-multichoice-single-choice");
                        q = new MultipleChoiceQuestion(studyID, singleChoice, title, choices);
                    }
                    qs[j] = q;
                }
                Questionnaire qnaire = new Questionnaire(studyID, qs);

                JSONArray eventArray = jsonStudy.getJSONArray("events");
                Event[] events = new Event[eventArray.length()];
                for(int g = 0; g < eventArray.length(); g++) {
                    JSONObject jsonEvent = eventArray.getJSONObject(g);
                    long eventID = jsonEvent.getLong("id");
                    String title = jsonEvent.getString("event-title");
                    int controltime = jsonEvent.getInt("event-control-time");
                    String controltimeUnit = jsonEvent.getString("event-control-time-unit");
                    events[g] = new Event(eventID, studyID, title, controltime, controltimeUnit);
                }

                studyList.add(new Study(studyID, name, qnaire, beginDate, endDate, studyLengthForUser, notificationsPerDay,
                        postponeTime, allowPostpone, minTimeBetweenNotifications, events, defaultBeepFree, isPublic));
            }

        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        return studyList;
    }

}
