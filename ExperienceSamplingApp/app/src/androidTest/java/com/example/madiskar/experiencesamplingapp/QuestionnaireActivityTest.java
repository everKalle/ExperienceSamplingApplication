package com.example.madiskar.experiencesamplingapp;


import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class QuestionnaireActivityTest {

    @Rule
    public ActivityTestRule<QuestionnaireActivity> questionnaireActivityActivityTestRule = new ActivityTestRule<QuestionnaireActivity>(QuestionnaireActivity.class, true, false);

    @Test
    public void questionnaireActivityTest() {
        Question q4 = new FreeTextQuestion(0, "Is it easy?");
        Question q2 = new FreeTextQuestion(0, "Is it still easy?");
        Question q1 = new MultipleChoiceQuestion(0, 1, "How would you rate the difficulty of this question?", new String[]{"easy", "medium", "hard"});
        Question q5 = new MultipleChoiceQuestion(0, 0, "How would you rate the difficulty of this question?", new String[]{"pretty easy", "medium, I think", "hard", "impossible"});

        Question[] batch1 = {q4,q2,q5,q1};
        Questionnaire qnaire1 = new Questionnaire(0, batch1);

        Intent intent = new Intent();
        intent.putExtra("QUESTIONNAIRE", qnaire1);
        intent.putExtra("notificationId", 0);
        questionnaireActivityActivityTestRule.launchActivity(intent);


        ViewInteraction appCompatEditText5 = onView(
                withId(R.id.inputText));
        appCompatEditText5.perform(scrollTo(), click());

        ViewInteraction appCompatEditText6 = onView(
                withId(R.id.inputText));
        appCompatEditText6.perform(scrollTo(), replaceText("question1"), closeSoftKeyboard());

        //pressBack();

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.nextquestionbutton), withText("Next"), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatEditText7 = onView(
                withId(R.id.inputText));
        appCompatEditText7.perform(scrollTo(), replaceText("testing2"), closeSoftKeyboard());

        //pressBack();

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.nextquestionbutton), withText("Next"), isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction checkBox = onView(
                allOf(withText("pretty easy"),
                        withParent(withId(R.id.checkBoxGroup))));
        checkBox.perform(scrollTo(), click());

        ViewInteraction checkBox2 = onView(
                allOf(withText("medium, I think"),
                        withParent(withId(R.id.checkBoxGroup))));
        checkBox2.perform(scrollTo(), click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.nextquestionbutton), withText("Next"), isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction radioButton = onView(
                allOf(withText("hard"),
                        withParent(withId(R.id.radioGroupSingle))));
        radioButton.perform(scrollTo(), click());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.nextquestionbutton), withText("Submit"), isDisplayed()));
        appCompatButton5.perform(click());
    }
}
