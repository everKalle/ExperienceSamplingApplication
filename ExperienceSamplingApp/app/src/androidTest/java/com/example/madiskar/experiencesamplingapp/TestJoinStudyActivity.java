package com.example.madiskar.experiencesamplingapp;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

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
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TestJoinStudyActivity {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void joinStudyActivityTest() {
        /*ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.email_input)));
        appCompatEditText3.perform(scrollTo(), replaceText("test@test.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                withId(R.id.password_input));
        appCompatEditText4.perform(scrollTo(), replaceText("testing123"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.button_login), withText("Login")));
        appCompatButton.perform(scrollTo(), click());*/

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction relativeLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.menuList),
                                withParent(withId(R.id.drawerPane))),
                        1),
                        isDisplayed()));
        relativeLayout.perform(click());

        ViewInteraction appCompatEditText5 = onView(
                withId(R.id.keywords_input));
        appCompatEditText5.perform(scrollTo(), replaceText("android,join"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.button_search), withText("Search"),
                        withParent(withId(R.id.linearLayout))));
        appCompatButton2.perform(scrollTo(), click());

        ViewInteraction studyExists = onView(
                allOf(withText("Android join testing")));
        studyExists.check(matches(isDisplayed()));

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.join_button), withText("Join"),
                        withParent(childAtPosition(
                                withId(android.R.id.list),
                                0)),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(android.R.id.button2), withText("YES")));
        appCompatButton4.perform(scrollTo(), click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Open"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction relativeLayout2 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.menuList),
                                withParent(withId(R.id.drawerPane))),
                        0),
                        isDisplayed()));
        relativeLayout2.perform(click());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.quit_button), withText("Quit"),
                        withParent(childAtPosition(
                                withId(android.R.id.list),
                                1)),
                        isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(android.R.id.button2), withText("OK")));
        appCompatButton6.perform(scrollTo(), click());

        ViewInteraction studyExists2 = onView(
                allOf(withText("Android join testing")));
        studyExists2.check(doesNotExist()); //study should not exist in the list

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withContentDescription("Open"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        appCompatImageButton3.perform(click());

        ViewInteraction relativeLayout3 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.menuList),
                                withParent(withId(R.id.drawerPane))),
                        4),
                        isDisplayed()));
        relativeLayout3.perform(click());

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
