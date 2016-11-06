package com.example.madiskar.experiencesamplingapp;


import android.support.test.espresso.ViewInteraction;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;


@RunWith(AndroidJUnit4.class)
public class Test3QuestionnaireActivity {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    UiDevice mDevice;

    @Before
    public void setUp() throws Exception {
        //super.setUp();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void questionnaireActivityTest() throws Exception {
        mDevice.openNotification();
        mDevice.wait(Until.hasObject(By.pkg("com.android.systemui")), 10000);

        mDevice.wait(Until.hasObject(By.text("Android UI testing 1")), 130000);   //Wait for notification
        UiObject eventText = mDevice.findObject(new UiSelector().text("Questionnaire"));
        assertTrue(eventText.exists());

        UiObject postponeButton = mDevice.findObject(new UiSelector().textMatches("POSTPONE|Postpone|postpone"));
        assertTrue(postponeButton.exists());

        UiObject refuseButton = mDevice.findObject(new UiSelector().textMatches("REFUSE|Refuse|refuse"));
        assertTrue(refuseButton.exists());

        UiObject okButton = mDevice.findObject(new UiSelector().textMatches("OK|Ok|ok"));
        assertTrue(okButton.exists());
        okButton.click();

        ViewInteraction appCompatEditText5 = onView(
                withId(R.id.inputText));
        appCompatEditText5.perform(scrollTo(), click());

        ViewInteraction appCompatEditText6 = onView(
                withId(R.id.inputText));
        appCompatEditText6.perform(scrollTo(), replaceText("automatic answer"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.nextquestionbutton), withText("Next"), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction radioButton = onView(
                allOf(withText("1 - Valik 2"),
                        withParent(withId(R.id.radioGroupSingle))));
        radioButton.perform(scrollTo(), click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.nextquestionbutton), withText("Next"), isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction checkBox = onView(
                allOf(withText("Mitu - Valik 1"),
                        withParent(withId(R.id.checkBoxGroup))));
        checkBox.perform(scrollTo(), click());

        ViewInteraction checkBox2 = onView(
                allOf(withText("Mitu - Valik 4"),
                        withParent(withId(R.id.checkBoxGroup))));
        checkBox2.perform(scrollTo(), click());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.nextquestionbutton), withText("Submit"), isDisplayed()));
        appCompatButton5.perform(click());
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
