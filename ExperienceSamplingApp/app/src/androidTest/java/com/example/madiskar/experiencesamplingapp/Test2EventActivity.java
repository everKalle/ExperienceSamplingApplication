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
public class Test2EventActivity {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    UiDevice mDevice;

    @Before
    public void setUp() throws Exception {
        //super.setUp();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void eventActivityTest() throws Exception {

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.event_button), withText("Event"),
                        withParent(childAtPosition(
                                withId(android.R.id.list),
                                0)),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(android.R.id.text1), withText("Running"),

                        isDisplayed()));
        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(android.R.id.button1), withText("Start")));
        appCompatButton3.perform(click());


        mDevice.openNotification();
        mDevice.wait(Until.hasObject(By.pkg("com.android.systemui")), 10000);


        UiObject eventText = mDevice.findObject(new UiSelector().text("Active Event"));
        assertTrue("'Active Event' exists", eventText.exists());

        UiObject eventName = mDevice.findObject(new UiSelector().text("Running"));
        assertTrue("Event 'Running' is the active event", eventName.exists());

        UiObject eventFalseName = mDevice.findObject(new UiSelector().text("Short Event"));
        assertFalse("Event 'Short Event' is NOT the active event", eventFalseName.exists());

        UiObject eventStopButton = mDevice.findObject(new UiSelector().textMatches("STOP|Stop|stop"));
        assertTrue("Event stop button exists", eventStopButton.exists());
        eventStopButton.click();

        //Make sure that notifications are closed
        if (mDevice.hasObject(By.pkg("com.android.systemui")))
            mDevice.pressBack();
    }

    /*
     * Removed for now, long waiting does not work in Greenhouse
     *
     */

    /*@Test
    public void eventControlTimeTest() throws Exception{
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.event_button), withText("Event"),
                        withParent(childAtPosition(
                                withId(android.R.id.list),
                                0)),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(android.R.id.text1), withText("Short Event"),

                        isDisplayed()));
        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(android.R.id.button1), withText("Start")));
        appCompatButton3.perform(click());


        mDevice.openNotification();
        mDevice.wait(Until.hasObject(By.pkg("com.android.systemui")), 10000);


        mDevice.wait(Until.hasObject(By.text("Controltime has passed")), 70000);
        UiObject eventText = mDevice.findObject(new UiSelector().text("Controltime for event \"Short Event\" has passed"));
        assertTrue(eventText.exists());

        UiObject eventStopButton = mDevice.findObject(new UiSelector().textMatches("STOP|Stop|stop"));
        if (!eventStopButton.exists()){
            UiObject expandButton = mDevice.findObject(new UiSelector().textMatches(".*01.*"));
            expandButton.click();
            eventStopButton = mDevice.findObject(new UiSelector().textMatches("STOP|Stop|stop"));
            eventStopButton.click();
        } else {
            eventStopButton.click();
        }
    }*/

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