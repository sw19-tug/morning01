package at.tugraz.ist.swe.note;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.rule.ActivityTestRule;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import at.tugraz.ist.swe.note.database.DatabaseHelper;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

public class NoteActivityTest {
    private DatabaseHelper databaseHelper;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Rule
    public ActivityTestRule<NoteActivity> activityActivityTestRule = new ActivityTestRule<>(NoteActivity.class);

    @Test
    public void checkDisplayLogicSaveDeleteButtons() {
        onView(withId(R.id.action_add)).check(matches(isDisplayed()));
        onView(withId(R.id.action_remove)).check(doesNotExist());

        onView(withId(R.id.action_add)).perform(click());

        onView(withId(R.id.action_add)).check(doesNotExist());
        onView(withId(R.id.action_remove)).check(matches(isDisplayed()));

        onView(withId(R.id.action_remove)).perform(click());

        onView(withId(R.id.action_add)).check(matches(isDisplayed()));
        onView(withId(R.id.action_remove)).check(doesNotExist());
    }

    @Test
    public void checkDisplayLogicPinUnpinButtons() {
        onView(withId(R.id.action_pinning)).check(matches(isDisplayed()));
        onView(withId(R.id.action_unpinning)).check(doesNotExist());

        onView(withId(R.id.action_pinning)).perform(click());

        onView(withId(R.id.action_pinning)).check(doesNotExist());
        onView(withId(R.id.action_unpinning)).check(matches(isDisplayed()));

        onView(withId(R.id.action_unpinning)).perform(click());

        onView(withId(R.id.action_pinning)).check(matches(isDisplayed()));
        onView(withId(R.id.action_unpinning)).check(doesNotExist());
    }

    @Test
    public void checkShareButtonVisibility() {
        onView(withId(R.id.action_share)).check(matches(isDisplayed()));
        onView(withId(R.id.action_share)).check(matches(isClickable()));
        onView(withId(R.id.action_share)).perform(click());
    }

    @Test
    public void checkBackButtonVisibility() {
        onView(withContentDescription(R.string.abc_action_bar_up_description)).check(matches(isDisplayed()));
        onView(withContentDescription(R.string.abc_action_bar_up_description)).check(matches(isClickable()));
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
    }

    @Test
    public void testNoteInsert() {
        onView(withId(R.id.tfTitle)).perform(typeText("Test Title"));
        onView(withId(R.id.tfContent)).perform(typeText("some Content"));

        onView(withId(R.id.action_add)).perform(click());

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selection = DatabaseHelper.NOTE_COLUMN_TITLE + " = ? AND " + DatabaseHelper.NOTE_COLUMN_CONTENT + " = ? AND " + DatabaseHelper.NOTE_COLUMN_PINNED + " = ?";

        String[] selectionArgs = {"Test Title", "some Content", "0"};

        Cursor cursor = database.query(
                DatabaseHelper.NOTE_TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        assertTrue(cursor.getCount() > 0);
        cursor.close();
    }


}