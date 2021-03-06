package at.tugraz.ist.swe.note;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.tugraz.ist.swe.note.database.DatabaseHelper;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TrashActivityTest {

    @Rule
    public ActivityTestRule<TrashActivity> activityActivityTestRule = new ActivityTestRule<>(TrashActivity.class);

    @Before
    public void setUp() {
        Util.resetDatabase(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));
    }

    @Test
    public void checkIfDeletedNotesAreDisplayed() {
        Note note1 = new Note("note1", "blabla1", 1);
        Note note2 = new Note("note2", "blabla2", 2);
        Note note3 = new Note("note3", "blabla3", 3);

        note1.setRemoved(true);
        note2.setRemoved(true);

        Note[] notes = {
                note1,
                note2,
                note3
        };
        TrashActivity activity = activityActivityTestRule.getActivity();
        activity.setNoteStorage(new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext(), null)));
        Util.fillNoteStorage(notes, activity);

        ListView trashListView = activityActivityTestRule.getActivity().findViewById(R.id.trashList);

        assertEquals(notes.length - 1, trashListView.getAdapter().getCount());
        assertTrue(notes[1].equals(trashListView.getAdapter().getItem(0)));
        assertTrue(notes[0].equals(trashListView.getAdapter().getItem(1)));

        boolean foundNote = false;
        for (int i = 0; i < trashListView.getAdapter().getCount(); ++i) {
            Note fetchedNote = (Note) trashListView.getAdapter().getItem(i);
            if (note3.getTitle().compareTo(fetchedNote.getTitle()) == 0 &&
                    note3.getContent().compareTo(fetchedNote.getContent()) == 0) {
                foundNote = true;
                break;
            }
        }
        assertFalse(foundNote);
    }

    @Test
    public void checkIfRestoredNoteIsNotDisplayed() {
        Note note1 = new Note("note1", "blabla1", 1);

        note1.setRemoved(true);

        Note[] notes = {
                note1
        };
        TrashActivity activity = activityActivityTestRule.getActivity();
        activity.setNoteStorage(new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext(), null)));
        Util.fillNoteStorage(notes, activity);

        ListView trashListView = activityActivityTestRule.getActivity().findViewById(R.id.trashList);

        onData(anything()).inAdapterView(withId(R.id.trashList)).atPosition(activityActivityTestRule.getActivity().noteList.size() - 1).perform(click());
        onView(withContentDescription(R.string.action_restore)).perform(click());
        onView(withText(R.string.yes)).perform(click());

        boolean foundNote = false;
        for (int i = 0; i < trashListView.getAdapter().getCount(); ++i) {
            Note fetchedNote = (Note) trashListView.getAdapter().getItem(i);
            if (note1.getTitle().compareTo(fetchedNote.getTitle()) == 0 &&
                    note1.getContent().compareTo(fetchedNote.getContent()) == 0) {
                foundNote = true;
                break;
            }
        }
        assertFalse(foundNote);
    }

    @Test
    public void checkIfRestoredNoteIsNotDisplayedLongClick() {
        Note note1 = new Note("note1", "blabla1", 1);

        note1.setRemoved(true);

        Note[] notes = {
                note1
        };
        TrashActivity activity = activityActivityTestRule.getActivity();
        activity.setNoteStorage(new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext(), null)));
        Util.fillNoteStorage(notes, activity);

        ListView trashListView = activityActivityTestRule.getActivity().findViewById(R.id.trashList);

        onData(anything()).inAdapterView(withId(R.id.trashList)).atPosition(activityActivityTestRule.getActivity().noteList.size() - 1).perform(longClick());
        onView(withText(R.string.yes)).perform(click());

        boolean foundNote = false;
        for (int i = 0; i < trashListView.getAdapter().getCount(); ++i) {
            Note fetchedNote = (Note) trashListView.getAdapter().getItem(i);
            if (note1.getTitle().compareTo(fetchedNote.getTitle()) == 0 &&
                    note1.getContent().compareTo(fetchedNote.getContent()) == 0) {
                foundNote = true;
                break;
            }
        }
        assertFalse(foundNote);
    }
}
