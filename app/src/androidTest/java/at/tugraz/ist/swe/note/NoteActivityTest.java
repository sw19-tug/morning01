package at.tugraz.ist.swe.note;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import at.tugraz.ist.swe.note.database.DatabaseHelper;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class NoteActivityTest {
    private DatabaseHelper databaseHelper;

    @Before
    public void setUp() {
        databaseHelper = new DatabaseHelper(InstrumentationRegistry.getTargetContext());
        Util.resetDatabase(databaseHelper);
    }

    @Rule
    public ActivityTestRule<NoteActivity> activityActivityTestRule = new ActivityTestRule<>(NoteActivity.class);


    @Test
    public void checkDisplayLogicPinUnpinButtons() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText(R.string.action_pinning)).check(matches(isDisplayed()));
        onView(withText(R.string.action_unpinning)).check(doesNotExist());


        onView(withText(R.string.action_pinning)).perform(click());

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText(R.string.action_pinning)).check(doesNotExist());
        onView(withText(R.string.action_unpinning)).check(matches(isDisplayed()));


        onView(withText(R.string.action_unpinning)).perform(click());

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText(R.string.action_pinning)).check(matches(isDisplayed()));
        onView(withText(R.string.action_unpinning)).check(doesNotExist());
    }

    @Test
    public void checkShareButtonVisibility() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText(R.string.action_share)).check(matches(isDisplayed()));
        onView(withText(R.string.action_share)).check(matches(isEnabled()));
    }

    @Test
    public void checkDisabledProtectButtonVisibility() {
        onView(withId(R.id.action_protect_disabled)).check(matches(isDisplayed()));
        onView(withId(R.id.action_protect_disabled)).check(matches(isClickable()));
    }

    @Test
    public void checkBackButtonVisibility() {
        onView(withContentDescription(R.string.abc_action_bar_up_description)).check(matches(isDisplayed()));
        onView(withContentDescription(R.string.abc_action_bar_up_description)).check(matches(isClickable()));
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
    }
}