package com.example.madiskar.experiencesamplingapp;


import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.example.madiskar.experiencesamplingapp.activities.LoginActivity;
import com.example.madiskar.experiencesamplingapp.activities.QuestionnaireActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class QuestionnaireActivityTest {

    @Rule
    public ActivityTestRule<QuestionnaireActivity> questionnaireActivityActivityTestRule = new ActivityTestRule<QuestionnaireActivity>(QuestionnaireActivity.class, true, false);

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void questionnaireActivityTest() {
        Intent intent = new Intent();
        intent.putExtra("StudyId", Long.parseLong("374"));
        intent.putExtra("notificationId", 0);

        ViewInteraction appCompatEditText = onView(
                withId(R.id.email_input));
        appCompatEditText.perform(scrollTo(), click());

        ViewInteraction appCompatEditText2 = onView(
                withId(R.id.email_input));
        appCompatEditText2.perform(scrollTo(), replaceText("test@test.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                withId(R.id.password_input));
        appCompatEditText3.perform(scrollTo(), replaceText("testing123"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.button_login), withText("Login")));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        appCompatImageButton.check(matches(isDisplayed()));

        questionnaireActivityActivityTestRule.launchActivity(intent); // launch questions now


        ViewInteraction appCompatEditText5 = onView(
                withId(R.id.inputText));
        appCompatEditText5.perform(scrollTo(), click());

        ViewInteraction appCompatEditText6 = onView(
                withId(R.id.inputText));
        appCompatEditText6.perform(scrollTo(), replaceText("See on androidi test"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.nextquestionbutton), withText("Next"), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction radioButton = onView(
                allOf(withText("1 - Valik 3"),
                        withParent(withId(R.id.radioGroupSingle))));
        radioButton.perform(scrollTo(), click());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.nextquestionbutton), withText("Next"), isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction checkBox3 = onView(
                allOf(withText("Mitu - Valik 2"),
                        withParent(withId(R.id.checkBoxGroup))));
        checkBox3.perform(scrollTo(), click());

        ViewInteraction checkBox4 = onView(
                allOf(withText("Mitu - Valik 3"),
                        withParent(withId(R.id.checkBoxGroup))));
        checkBox4.perform(scrollTo(), click());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.nextquestionbutton), withText("Submit"), isDisplayed()));
        appCompatButton6.perform(click());


        ViewInteraction appCompatImageButton10 = onView(
                allOf(withContentDescription("Open"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        appCompatImageButton10.perform(click());

        ViewInteraction relativeLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.menuList),
                                withParent(withId(R.id.drawerPane))),
                        5),
                        isDisplayed()));
        relativeLayout.perform(click());

        ViewInteraction appCompatButton9 = onView(
                allOf(withId(R.id.button_login), withText("Login")));
        appCompatButton9.check(matches(isDisplayed()));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
