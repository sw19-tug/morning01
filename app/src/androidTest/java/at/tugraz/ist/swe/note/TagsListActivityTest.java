package at.tugraz.ist.swe.note;

import android.graphics.Color;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.widget.ListView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Random;

import at.tugraz.ist.swe.note.database.NoteTag;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertFalse;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;

public class TagsListActivityTest {

    @Rule
    public ActivityTestRule<TagsListActivity> activityActivityTestRule = new ActivityTestRule<>(TagsListActivity.class);

    @Test
    public void checkCreateNoteButtonVisibility() {
        onView(withId(R.id.createTagButton)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfCreateButtonIsClickable() {
        onView(withId(R.id.createTagButton)).check(matches(isClickable()));
    }

    @Test
    public void checkIfTagsAreDisplayedInOverview(){
        NoteTag[] tags = {
                new NoteTag("tag1", Color.BLACK),
                new NoteTag("tag2", Color.BLACK),
                new NoteTag("tag3", Color.BLACK),
        };

        activityActivityTestRule.getActivity().setTagList(tags);

        ListView tagListView = activityActivityTestRule.getActivity().findViewById(R.id.tagsListView);

        assertEquals(tags.length, tagListView.getAdapter().getCount());
        for (int i = 0; i < tags.length; ++i){
            assertTrue(tags[i].equals(tagListView.getAdapter().getItem(i)));
        }
    }
}
