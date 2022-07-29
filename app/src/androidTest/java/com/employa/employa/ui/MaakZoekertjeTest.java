package com.employa.employa.ui;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.employa.employa.R;
import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MaakZoekertjeTest {

    @Rule
    public ActivityTestRule<StartActivity> mActivityTestRule = new ActivityTestRule<>(StartActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    @Test
    public void maakZoekertjeTest() {
        // Er is echt gewoon geen goede manier om eerst uit te loggen indien ingelogd is zonder meer Espresso hacks.
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            ViewInteraction appCompatButton = onView(
                    allOf(withId(R.id.button_signin), withText("Inloggen"),
                            childAtPosition(
                                    allOf(withId(R.id.container_layout),
                                            childAtPosition(
                                                    withClassName(is("android.widget.ScrollView")),
                                                    0)),
                                    3)));
            appCompatButton.perform(scrollTo(), click());

            ViewInteraction appCompatEditText = onView(
                    allOf(withId(R.id.email_field),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.ScrollView")),
                                            0),
                                    1)));
            appCompatEditText.perform(scrollTo(), replaceText("laurens.debackere@ugent.be"), closeSoftKeyboard());

            ViewInteraction appCompatEditText2 = onView(
                    allOf(withId(R.id.password_field),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.ScrollView")),
                                            0),
                                    2)));
            appCompatEditText2.perform(scrollTo(), replaceText("Employa"), closeSoftKeyboard());

            ViewInteraction appCompatButton2 = onView(
                    allOf(withId(R.id.button_signin), withText("Inloggen"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.ScrollView")),
                                            0),
                                    3)));
            appCompatButton2.perform(scrollTo(), click());
        }

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.floatingActionButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.main_content_nav_host_fragment),
                                        0),
                                2),
                        isDisplayed()));
        floatingActionButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction linearLayout = onView(
                allOf(withId(R.id.linearLayout),
                        childAtPosition(
                                allOf(withId(R.id.category_grid_view),
                                        childAtPosition(
                                                withId(R.id.lineairlayout),
                                                2)),
                                5)));
        linearLayout.perform(click());

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.nextButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.main_content_nav_host_fragment),
                                        0),
                                3),
                        isDisplayed()));
        appCompatImageView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.editTextTitle),
                        childAtPosition(
                                allOf(withId(R.id.lineairLayout),
                                        childAtPosition(
                                                withId(R.id.scrollLayout),
                                                0)),
                                3)));
        appCompatEditText3.perform(replaceText("Bijles Duits"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.editTextDescription),
                        childAtPosition(
                                allOf(withId(R.id.lineairLayout),
                                        childAtPosition(
                                                withId(R.id.scrollLayout),
                                                0)),
                                5)));
        appCompatEditText4.perform(replaceText("Ik zoek iemand om mijn grammatical bij te schaven. Niveau 5de-6de middelbaar."), closeSoftKeyboard());

        ViewInteraction appCompatImageView2 = onView(
                allOf(withId(R.id.nextButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.main_content_nav_host_fragment),
                                        0),
                                3),
                        isDisplayed()));
        appCompatImageView2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.editTextTitle),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withClassName(is("android.widget.ScrollView")),
                                                0)),
                                3)));
        appCompatEditText6.perform(replaceText("15€/u"), closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.open_maps_button), withText("Open Maps"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withClassName(is("android.widget.ScrollView")),
                                                0)),
                                5)));
        appCompatButton3.perform(scrollTo(), click());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.search_bar_maps),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout3),
                                        childAtPosition(
                                                withId(R.id.linearLayout4),
                                                2)),
                                1),
                        isDisplayed()));
        appCompatEditText7.perform(replaceText("krijgslaan 281"), closeSoftKeyboard());

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.search_bar_maps), withText("krijgslaan 281"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout3),
                                        childAtPosition(
                                                withId(R.id.linearLayout4),
                                                2)),
                                1),
                        isDisplayed()));
        appCompatEditText8.perform(pressImeActionButton());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.select_location_button), withText("Bevestigen"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout4),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatButton4.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageView3 = onView(
                allOf(withId(R.id.nextButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.main_content_nav_host_fragment),
                                        0),
                                3),
                        isDisplayed()));
        appCompatImageView3.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Werkt niet in Espresso for some reason
        /*ViewInteraction appCompatRadioButton = onView(
                allOf(withId(R.id.radioButtonDateDoesNotMatter), withText("Maakt niet uit"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout6),
                                        2),
                                0),
                        isDisplayed()));
        appCompatRadioButton.perform(click());*/

        ViewInteraction appCompatImageView4 = onView(
                allOf(withId(R.id.nextButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.main_content_nav_host_fragment),
                                        0),
                                3),
                        isDisplayed()));
        appCompatImageView4.perform(click());


        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.buttonConfirm), withText("Plaatsen"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.add_advertisement_view_pager),
                                        1),
                                0),
                        isDisplayed()));
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
