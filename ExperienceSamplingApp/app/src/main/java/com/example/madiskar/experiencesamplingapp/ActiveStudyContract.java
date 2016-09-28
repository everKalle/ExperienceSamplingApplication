package com.example.madiskar.experiencesamplingapp;

import android.provider.BaseColumns;

/**
 * Created by madiskar on 27/09/2016.
 */
public class ActiveStudyContract {

    private ActiveStudyContract() { }

    public static class ActiveStudyEntry implements BaseColumns {
        public static final String TABLE_NAME = "active_studies_table";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BEGINDATE = "beginDate";
        public static final String COLUMN_ENDDATE = "endDate";
        public static final String COLUMN_STUDYLENGTH = "studyLength";
        public static final String COLUMN_NOTIFICATIONSPERDAY = "notificationsPerDay";
        public static final String COLUMN_NOTIFICATIONINTERVAL = "notificationInterval";
        public static final String COLUMN_MINTIMEBETWEENNOTIFICATIONS = "minTimeBetweenNotifications";
        public static final String COLUMN_POSTPONETIME = "postponeTime";
        public static final String COLUMN_POSTPONABLE = "postponable";
    }

    public static class QuestionEntry implements  BaseColumns {
        public static final String TABLE_NAME = "questions_table";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_ANSWER = "answer";
        public static final String COLUMN_STUDYID = "study_id";
        public static final String COLUMN_TYPE = "question_type";
    }
}
