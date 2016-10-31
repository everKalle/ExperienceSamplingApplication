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
        public static final String COLUMN_DEFAULTBEEPFREE = "default_beepfree";
    }

    public static class QuestionEntry implements  BaseColumns {
        public static final String TABLE_NAME = "questions_table";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_SINGLECHOICE = "single_choice";
        public static final String COLUMN_STUDYID = "study_id";
        public static final String COLUMN_TYPE = "question_type";
        public static final String COLUMN_MULTICHOICES = "question_multichoices";
    }

    public static class AnswerEntry implements  BaseColumns {
        public static final String TABLE_NAME = "answers_table";
        public static final String COLUMN_STUDYID = "study_id";
        public static final String COLUMN_ANSWER = "answer";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

    public static class EventEntry implements BaseColumns {
        public static final String TABLE_NAME = "event_table";
        public static final String COLUMN_STUDYID = "study_id";
        public static final String COLUMN_NAME = "event_title";
        public static final String COLUMN_CONTROLTIME = "event_control_time";
        public static final String COLUMN_UNIT = "event_unit";
    }

    public static class EventResultsEntry implements BaseColumns {
        public static final String TABLE_NAME = "event_results_table";
        public static final String COLUMN_EVENTID = "event_id";
        public static final String COLUMN_DURATION = "event_duration";
    }
}
