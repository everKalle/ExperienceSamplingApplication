package com.example.madiskar.experiencesamplingapp;

import android.os.Parcel;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


import java.util.Calendar;
import java.util.GregorianCalendar;

import static java.util.Calendar.MONTH;
import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(AndroidJUnit4.class)
public class UnitTestParcelables {

    /*
     * Parcelable testing based on:
     * http://www.kevinrschultz.com/blog/2014/03/01/how-not-to-test-androids-parcelable-interface/
     */

    FreeTextQuestion freeTextQuestion;
    MultipleChoiceQuestion multipleChoiceQuestion;
    Event event;
    Questionnaire questionnaire;
    Study study;

    @Before
    public void setUp(){
        freeTextQuestion = new FreeTextQuestion(21, "testquestion1");
        String[] choices = {"Choice1, choice2"};
        multipleChoiceQuestion = new MultipleChoiceQuestion(21, 1, "Multichoice q1", choices);
        event = new Event(2, 21, "Event 1", 2, "m");
        Question[] questions = {freeTextQuestion, multipleChoiceQuestion};

        questionnaire = new Questionnaire(21, questions);
    }

    @Test
    public void freeTextQuestionTest() throws Exception {
        Parcel parcel = Parcel.obtain();
        freeTextQuestion.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        FreeTextQuestion parceledFreeTextQuestion = FreeTextQuestion.CREATOR.createFromParcel(parcel);
        assertEquals("Associated study ID should remain the same.", freeTextQuestion.getStudyId(), parceledFreeTextQuestion.getStudyId());
        assertEquals("Free text question should remain the same.", freeTextQuestion.getText(), parceledFreeTextQuestion.getText());
    }

    @Test
    public void multipleChoiceQuestionTest() throws Exception {
        Parcel parcel = Parcel.obtain();
        multipleChoiceQuestion.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        MultipleChoiceQuestion parceledMultiChoiceQuestion = MultipleChoiceQuestion.CREATOR.createFromParcel(parcel);
        assertEquals("Associated study ID should remain the same.", multipleChoiceQuestion.getStudyId(), parceledMultiChoiceQuestion.getStudyId());
        assertEquals("Multiple choice question singleChoice value should remain the same.", multipleChoiceQuestion.getSingleChoice(), parceledMultiChoiceQuestion.getSingleChoice());
        assertEquals("Multiple choice question should remain the same.", multipleChoiceQuestion.getText(), parceledMultiChoiceQuestion.getText());
        assertEquals(multipleChoiceQuestion.getChoices(), parceledMultiChoiceQuestion.getChoices());
    }

    @Test
    public void eventTest() throws Exception {
        Parcel parcel = Parcel.obtain();
        event.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Event parceledEvent1 = Event.CREATOR.createFromParcel(parcel);

        assertEquals("Event ID should remain the same.", event.getId(), parceledEvent1.getId());
        assertEquals("Event name should remain the same.", event.getName(), parceledEvent1.getName());
        assertEquals("Event control time should remain the same.", event.getControlTime(), parceledEvent1.getControlTime());
        assertEquals("Event control time unit should remain the same.", event.getUnit(), parceledEvent1.getUnit());
        assertEquals("Event start hour should remain the same.", event.getStartTimeHour(), parceledEvent1.getStartTimeHour());
        assertEquals("Event start minute should remain the same.", event.getStartTimeMinute(), parceledEvent1.getStartTimeMinute());
        assertEquals("Event end hour should be -1.", parceledEvent1.getEndTimeHour(), -1);
        assertEquals("Event end minute should be -1.", parceledEvent1.getEndTimeMinute(), -1);
    }

    @Test
    public void questionnaireTest() throws Exception {
        Parcel parcel = Parcel.obtain();
        questionnaire.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Questionnaire parceledQuestionnaire = Questionnaire.CREATOR.createFromParcel(parcel);

        assertEquals("Questionnaire study ID should remain the same.", questionnaire.getStudyId(), parceledQuestionnaire.getStudyId());
        assertEquals("Questionnaire question 1 should remain the same", questionnaire.getQuestions()[0].getText(), parceledQuestionnaire.getQuestions()[0].getText());
        assertEquals("Questionnaire question 2 should remain the same", questionnaire.getQuestions()[1].getText(), parceledQuestionnaire.getQuestions()[1].getText());
    }
}