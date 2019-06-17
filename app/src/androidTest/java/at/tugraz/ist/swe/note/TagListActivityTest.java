package at.tugraz.ist.swe.note;

import android.graphics.Color;
import android.support.test.rule.ActivityTestRule;
import android.widget.ListView;

import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

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

public class TagListActivityTest {

    @Rule
    public ActivityTestRule<TagListActivity> activityActivityTestRule = new ActivityTestRule<>(TagListActivity.class);

    @Test
    public void checkCreateNoteButtonVisibility() {
        onView(withId(R.id.createTagButton)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfCreateButtonIsClickable() {
        onView(withId(R.id.createTagButton)).check(matches(isClickable()));
    }

    @Test
    public void checkIfTagsAreDisplayedInOverview() {
        NoteTag[] tags = {
                new NoteTag("tag1", NoteTag.DEFAULT_COLOR),
                new NoteTag("tag2", NoteTag.DEFAULT_COLOR),
                new NoteTag("tag3", NoteTag.DEFAULT_COLOR),
        };

        activityActivityTestRule.getActivity().setTagList(tags);

        ListView tagListView = activityActivityTestRule.getActivity().findViewById(R.id.tagListView);

        assertEquals(tags.length, tagListView.getAdapter().getCount());
        for (int i = 0; i < tags.length; ++i) {
            assertTrue(tags[i].equals(tagListView.getAdapter().getItem(i)));
        }
    }

    @Test
    public void checkSaveTag() {
        onView(withId(R.id.createTagButton)).perform(click());

        Random generator = new Random();
        String randomTitle = String.valueOf(generator.nextInt(100000));

        onView(withId(R.id.tagNameEditText)).perform(typeText(randomTitle));
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());

        NoteTag tag = new NoteTag(randomTitle, Color.BLUE);
        ListView tagListView = activityActivityTestRule.getActivity().findViewById(R.id.tagListView);

        assertNotEquals(0, tagListView.getAdapter().getCount());
        boolean foundTag = false;
        for (int i = 0; i < tagListView.getAdapter().getCount(); ++i) {
            NoteTag fetchedTag = (NoteTag) tagListView.getAdapter().getItem(i);
            if (tag.getName().compareTo(fetchedTag.getName()) == 0) {
                foundTag = true;
                break;
            }

        }
        assertTrue(foundTag);
    }

    @Test
    public void checkEditTag() {

        onView(withId(R.id.createTagButton)).perform(click());

        Random generator = new Random();
        String randomTitle = String.valueOf(generator.nextInt(100000));

        onView(withId(R.id.tagNameEditText)).perform(typeText(randomTitle));
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        onView(withId(R.id.tagEditToggleSwitch)).perform(click());

        onData(anything()).inAdapterView(withId(R.id.tagListView)).atPosition(activityActivityTestRule.getActivity().tags.size() - 1).perform(click());

        ListView tagListView = activityActivityTestRule.getActivity().findViewById(R.id.tagListView);
        NoteTag check_tag = (NoteTag) tagListView.getAdapter().getItem(activityActivityTestRule.getActivity().currentSelectedTag);
        onView(withId(R.id.tagNameEditText))
                .check(matches(withText(check_tag.getName())));

        String updateString = "update";
        onView(withId(R.id.tagNameEditText)).perform(typeText(updateString));

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());

        NoteTag tag = new NoteTag(check_tag.getName() + updateString, NoteTag.DEFAULT_COLOR);

        assertNotEquals(0, tagListView.getAdapter().getCount());
        boolean foundTag = false;
        for (int i = 0; i < tagListView.getAdapter().getCount(); ++i) {
            NoteTag fetchedTag = (NoteTag) tagListView.getAdapter().getItem(i);
            if (tag.getName().compareTo(fetchedTag.getName()) == 0) {
                foundTag = true;
                break;
            }

        }
        assertTrue(foundTag);
    }


    @Test
    public void checkTagDelete() {

        onView(withId(R.id.createTagButton)).perform(click());

        Random generator = new Random();
        String randomTitle = String.valueOf(generator.nextInt(100000));

        onView(withId(R.id.tagNameEditText)).perform(typeText(randomTitle));
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        onView(withId(R.id.tagEditToggleSwitch)).perform(click());

        onData(anything()).inAdapterView(withId(R.id.tagListView)).atPosition(activityActivityTestRule.getActivity().tags.size() - 1).perform(click());

        ListView tagListView = activityActivityTestRule.getActivity().findViewById(R.id.tagListView);
        NoteTag check_tag = (NoteTag) tagListView.getAdapter().getItem(activityActivityTestRule.getActivity().currentSelectedTag);
        onView(withId(R.id.tagNameEditText))
                .check(matches(withText(check_tag.getName())));


        onView(withId(R.id.action_tag_remove)).perform(click());
        onView(withText("YES")).perform(click());

        boolean foundTag = false;
        for (int i = 0; i < tagListView.getAdapter().getCount(); ++i) {
            NoteTag fetchedTag = (NoteTag) tagListView.getAdapter().getItem(i);
            if (check_tag.getName().compareTo(fetchedTag.getName()) == 0) {
                foundTag = true;
                break;
            }

        }
        assertTrue(!foundTag);
    }
}
