package at.tugraz.ist.swe.note;



import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Random;

import at.tugraz.ist.swe.note.database.DatabaseHelper;

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

        activityActivityTestRule.getActivity().setNoteList(notes);

        ListView noteListView = activityActivityTestRule.getActivity().findViewById(R.id.notesList);

        assertEquals(notes.length, noteListView.getAdapter().getCount());
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
    public void checkNoEmptyNote() {

        boolean foundEmptyNote = false;
        onView(withId(R.id.createNoteButton)).perform(click());

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());

        ListView noteListView = activityActivityTestRule.getActivity().findViewById(R.id.notesList);

        for (int i = 0; i < noteListView.getAdapter().getCount(); ++i){
            Note fetchedNote = (Note) noteListView.getAdapter().getItem(i);
            if(fetchedNote.getTitle().isEmpty() && fetchedNote.getContent().isEmpty())
            {
                foundEmptyNote = true;
                break;
            }
        }

        assertFalse(foundEmptyNote);

    }

    @Test
    public void checkNotesListViewVisibility() {
        Note[] notes = {
                new Note("note1", "blabla1", 1)
        };

        activityActivityTestRule.getActivity().setNoteList(notes);
        onView(withId(R.id.notesList)).check(matches(isDisplayed()));
    }

    @Test
    public void checkNoteSaveButtonForEditingNote() {


        //click second element
        onData(anything()).inAdapterView(withId(R.id.notesList)).atPosition(0).perform(click());

        ListView noteListView = activityActivityTestRule.getActivity().findViewById(R.id.notesList);
        Note check_note = (Note) noteListView.getAdapter().getItem(0);
        onView(withId(R.id.tfTitle))
                .check(matches(withText(check_note.getTitle())));

        onView(withId(R.id.tfContent))
                .check(matches(withText(check_note.getContent())));


        onView(withId(R.id.tfTitle)).perform(typeText("update"));

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        noteListView = activityActivityTestRule.getActivity().findViewById(R.id.notesList);

        Note note = new Note(check_note.getTitle()+"update", check_note.getContent(), check_note.getPinned());


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
/*


    @Test
    public void checkIfSearchButtonIsClickable() {
        onView(withId(R.id.searchButton)).check(matches(isClickable()));
    }
    @Test
    public void checkIfImportButtonIsClickable() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.main_import)).check(matches(isClickable()));

    }
    @Test
    public void checkIfExportButtonIsClickable() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.main_export)).check(matches(isClickable()));
    }

*/

    @Test
    public void checkToolbarButtonsVisibility() {
        //onView(withId(R.id.searchButton)).check(matches(isDisplayed()));
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        //onView(withText(R.string.main_import)).check(matches(isDisplayed()));
        onView(withText(R.string.main_sort)).check(matches(isDisplayed()));
        //onView(withText(R.string.main_export)).check(matches(isDisplayed()));
    }
    @Test
    public void checkIfSortButtonIsClickable() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.main_sort)).check(matches(isClickable()));
    }

    @Test
    public void checkSortByTitle() {
        Note note1 = new Note("A_Test_title", "blabla1", 1);
        Note note2 = new Note("B_Test_title", "blabla2", 1);
        Note note3 = new Note("C_Test_title", "blabla3", 1);
        Note note4 = new Note("D_Test_title", "blabla4", 2);

        Note[] notes = {
                note1,
                note3,
                note4,
                note2,
        };
        Note[] expectedNoteArray = {
                note4,
                note1,
                note2,
                note3,
        };

        MainActivity activity = activityActivityTestRule.getActivity();
        Util.fillNoteStorage(notes, activity.noteStorage);
        ListView noteListView = activity.findViewById(R.id.notesList);

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.main_sort)).perform(click());
        onView(withText(R.string.main_sort_by_title_asc)).perform(click());

        assertEquals(expectedNoteArray.length, noteListView.getAdapter().getCount());
        for (int i = 0; i < expectedNoteArray.length; ++i){
            assertTrue(expectedNoteArray[i].equals(noteListView.getAdapter().getItem(i)));
        }
    }

}

