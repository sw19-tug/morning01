package at.tugraz.ist.swe.note;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
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
    public void checkNotesListViewVisibility() {
        onView(withId(R.id.notesList)).check(matches(isDisplayed()));
    }

    
}
