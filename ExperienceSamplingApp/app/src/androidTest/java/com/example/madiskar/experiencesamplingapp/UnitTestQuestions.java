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


import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class UnitTestQuestions{

    //http://www.kevinrschultz.com/blog/2014/03/01/how-not-to-test-androids-parcelable-interface/


    @Test
    public void freeTextQuestionTest() throws Exception {
        FreeTextQuestion freeTextQuestion = new FreeTextQuestion(0, "testquestion1");
        Parcel parcel = Parcel.obtain();
        freeTextQuestion.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        FreeTextQuestion parceledFreeTextQuestion = FreeTextQuestion.CREATOR.createFromParcel(parcel);
        assertEquals(freeTextQuestion.getStudyId(), parceledFreeTextQuestion.getStudyId());
        assertEquals(freeTextQuestion.getText(), parceledFreeTextQuestion.getText());
    }

    @Test
    public void multipleChoiceQuestionTest() throws Exception {
        String[] choices = {"Choice1, choice2"};
        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion(21, 1, "Multichoice q1", choices);
        Parcel parcel = Parcel.obtain();
        multipleChoiceQuestion.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        MultipleChoiceQuestion parceledMultiChoiceQuestion = MultipleChoiceQuestion.CREATOR.createFromParcel(parcel);
        assertEquals(multipleChoiceQuestion.getStudyId(), parceledMultiChoiceQuestion.getStudyId());
        assertEquals(multipleChoiceQuestion.getSingleChoice(), parceledMultiChoiceQuestion.getSingleChoice());
        assertEquals(multipleChoiceQuestion.getText(), parceledMultiChoiceQuestion.getText());
        assertEquals(multipleChoiceQuestion.getChoices(), parceledMultiChoiceQuestion.getChoices());
    }

    /*
    public void testParcelable() {
        Foo foo = new Foo(1L, 3, "name", false);
        Parcel parcel = Parcel.obtain();
        foo.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Foo parceledFoo = Foo.CREATOR.createFromParcel(parcel);
        assertEquals(foo, parceledFoo);
    }


     */
}