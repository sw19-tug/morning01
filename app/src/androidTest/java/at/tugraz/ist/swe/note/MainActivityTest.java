package at.tugraz.ist.swe.note;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Random;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void checkCreateNoteButtonVisibility() {
        onView(withId(R.id.createNoteButton)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfCreateButtonIsClickable() {
        onView(withId(R.id.createNoteButton)).check(matches(isClickable()));
    }

    @Test
    public void checkIfNotesAreDisplayedInOverview(){
        Note[] notes = {
                new Note("note1", "blabla1", 1),
                new Note("note2", "blabla2", 2),
                new Note("note3", "blabla3", 3)
        };

        activityActivityTestRule.getActivity().setmNoteList(notes);

        ListView noteListView = activityActivityTestRule.getActivity().findViewById(R.id.notesList);


        assertEquals(3, noteListView.getAdapter().getCount());
        for (int i = 0; i < notes.length; ++i){
            assertTrue(notes[i].equals(noteListView.getAdapter().getItem(i)));
        }
    }

    @Test
    public void checkProperReturnFromNoteActivity() {
        onView(withId(R.id.createNoteButton)).perform(click());

        Random generator = new Random();
        String randomTitle = String.valueOf(generator.nextInt(100000));
        String randomContent = String.valueOf(generator.nextInt(100000));

        onView(withId(R.id.tfTitle)).perform(typeText(randomTitle));
        onView(withId(R.id.tfContent)).perform(typeText(randomContent));
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());

        Note note = new Note(randomTitle, randomContent, 1);
        ListView noteListView = activityActivityTestRule.getActivity().findViewById(R.id.notesList);

        assertNotEquals(0, noteListView.getAdapter().getCount());
        boolean foundNote = false;
        for (int i = 0; i < noteListView.getAdapter().getCount(); ++i){
            Note fetchedNote = (Note) noteListView.getAdapter().getItem(i);
            if(note.getTitle().compareTo(fetchedNote.getTitle()) == 0 &&
                    note.getContent().compareTo(fetchedNote.getContent()) == 0)
            {
                foundNote = true;
                break;
            }

        }
        assertTrue(foundNote);

    }

    @Test
    public void checkNotesListViewVisibility() {
        Note[] notes = {
                new Note("note1", "blabla1", 1)
        };

        activityActivityTestRule.getActivity().setmNoteList(notes);
        onView(withId(R.id.notesList)).check(matches(isDisplayed()));
    }
}
