package at.tugraz.ist.swe.note;



import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;

import org.junit.Before;
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
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
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

    @Before
    public void setUp() {
        Util.resetDatabase(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));
    }

    @Test
    public void checkCreateNoteButtonVisibility() {
        onView(withId(R.id.createNoteButton)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfCreateButtonIsClickable() {
        onView(withId(R.id.createNoteButton)).check(matches(isClickable()));
    }

    @Test
    public void checkIfNotesAreDisplayedInOverview() {
        Note[] notes = {
                new Note("note1", "blabla1", 1),
                new Note("note2", "blabla2", 2),
                new Note("note3", "blabla3", 3)
        };

        activityActivityTestRule.getActivity().setNoteList(notes);

        ListView noteListView = activityActivityTestRule.getActivity().findViewById(R.id.notesList);

        assertEquals(notes.length, noteListView.getAdapter().getCount());
        for (int i = 0; i < notes.length; ++i) {
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
        for (int i = 0; i < noteListView.getAdapter().getCount(); ++i) {
            Note fetchedNote = (Note) noteListView.getAdapter().getItem(i);
            if (note.getTitle().compareTo(fetchedNote.getTitle()) == 0 &&
                    note.getContent().compareTo(fetchedNote.getContent()) == 0) {
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

        for (int i = 0; i < noteListView.getAdapter().getCount(); ++i) {
            Note fetchedNote = (Note) noteListView.getAdapter().getItem(i);
            if (fetchedNote.getTitle().isEmpty() && fetchedNote.getContent().isEmpty()) {
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
        MainActivity activity = activityActivityTestRule.getActivity();
        activity.setNoteStorage(new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext(), null)));
        Note checkNote = new Note("note1", "blabla1", 1);
        Note[] notes = {
                checkNote
        };

        Util.fillNoteStorage(notes, activity);

        // click first element
        onData(anything()).inAdapterView(withId(R.id.notesList)).atPosition(0).perform(click());

        onView(withId(R.id.tfTitle)).perform(typeText("update"));

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());

        Note updatedNote = new Note(checkNote.getTitle() + "update", checkNote.getContent(), checkNote.getPinned());

        ListView noteListView = activityActivityTestRule.getActivity().findViewById(R.id.notesList);

        assertEquals(1, noteListView.getAdapter().getCount());

        Note fetchedNote = (Note) noteListView.getAdapter().getItem(0);
        assertEquals(updatedNote, fetchedNote);
    }

    @Test
    public void checkToolbarButtonsVisibility() {
        onView(withId(R.id.search)).check(matches(isDisplayed()));
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        //onView(withText(R.string.main_import)).check(matches(isDisplayed()));
        onView(withText(R.string.main_sort)).check(matches(isDisplayed()));
        //onView(withText(R.string.main_export)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfNoteIsDeletedAfterPressingDeleteOK() {

        MainActivity activity = activityActivityTestRule.getActivity();
        activity.setNoteStorage(new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext(), null)));
        Note checkNote = new Note("note1", "blabla1", 1);
        Note[] notes = {
                checkNote
        };

        Util.fillNoteStorage(notes, activity);
        //click first element
        onData(anything()).inAdapterView(withId(R.id.notesList)).atPosition(activityActivityTestRule.getActivity().noteList.size() - 1).perform(click());

        ListView noteListView = activityActivityTestRule.getActivity().findViewById(R.id.notesList);
        onView(withId(R.id.tfTitle)).check(matches(withText(checkNote.getTitle())));

        onView(withId(R.id.tfContent)).check(matches(withText(checkNote.getContent())));

        onView(withContentDescription(R.string.action_remove)).perform(click());
        onView(withText(R.string.yes)).perform(click());

        boolean foundNote = false;
        for (int i = 0; i < noteListView.getAdapter().getCount(); ++i) {
            Note fetchedNote = (Note) noteListView.getAdapter().getItem(i);
            if (checkNote.getTitle().compareTo(fetchedNote.getTitle()) == 0 &&
                    checkNote.getContent().compareTo(fetchedNote.getContent()) == 0) {
                foundNote = true;
                break;
            }
        }
        assertTrue(!foundNote);
    }
    @Test
    public void checkIfSortButtonIsClickable() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.main_sort)).perform(click());
        // MenuItems are not clickable -> therefore isEnabled()
        onView(withText(R.string.main_sort_by_title_asc)).check(matches(isEnabled()));
        onView(withText(R.string.main_sort_by_created_date_desc)).check(matches(isEnabled()));
    }

    private void checkSort(Note[] notes, Note[] expectedNoteArray, int resourceButtonId) {
        MainActivity activity = activityActivityTestRule.getActivity();
        activity.setNoteStorage(new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext(), null)));
        Util.fillNoteStorage(notes, activity);

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.main_sort)).perform(click());
        onView(withText(resourceButtonId)).perform(click());
        ListView noteListView = activityActivityTestRule.getActivity().findViewById(R.id.notesList);
        assertEquals(expectedNoteArray.length, noteListView.getAdapter().getCount());
        for (int i = 0; i < expectedNoteArray.length; ++i){
            assertEquals(expectedNoteArray[i], noteListView.getAdapter().getItem(i));
        }
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

        checkSort(notes, expectedNoteArray, R.string.main_sort_by_title_asc);
    }

    @Test
    public void checkSortByCreatedDate() {
        Note note1 = new Note("A_Test_title", "blabla1", 1);
        Note note2 = new Note("B_Test_title", "blabla2", 1);
        Note note3 = new Note("C_Test_title", "blabla3", 2);
        Note note4 = new Note("D_Test_title", "blabla4", 1);

        Note[] notes = {
                note1,
                note2,
                note3,
                note4,
        };
        Note[] expectedNoteArray = {
                note3,
                note4,
                note2,
                note1,
        };

        checkSort(notes, expectedNoteArray, R.string.main_sort_by_created_date_desc);
    }

    @Test
    public void checkNotePinning() {
        Note[] notes = {
                new Note("note1", "blabla1", 0),
                new Note("note2", "blabla2", 0),
                new Note("note3", "blabla3", 0)
        };
        activityActivityTestRule.getActivity().setNoteList(notes);

        try{
            runOnUiThread(new Runnable(){
                public void run() {
                    NoteAdapter customNoteAdapter = new NoteAdapter(
                            activityActivityTestRule.getActivity(), activityActivityTestRule.getActivity().noteList);

                    activityActivityTestRule.getActivity().noteListView.setAdapter(customNoteAdapter);
                }
            });
        }
        catch(Throwable e){throw new RuntimeException();}


        ListView noteListView = activityActivityTestRule.getActivity().findViewById(R.id.notesList);
        Note checkNote = (Note) noteListView.getAdapter().getItem(1);

        onData(anything()).inAdapterView(withId(R.id.notesList)).atPosition(1).perform(click());
        onView(withId(R.id.action_pinning)).perform(click());
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());

        Note pinnedNote = (Note) noteListView.getAdapter().getItem(0);

        assertTrue(checkNote.getTitle().equals(pinnedNote.getTitle()) &&
                checkNote.getContent().equals(pinnedNote.getContent()));
    }

    @Test
    public void checkNoteSearch() {
        Note note1 = new Note("Adkdhe", "Ajdnh diekdn ekde eie", 1);
        Note note2 = new Note("Khdhd", "Jdkdh dhgrgrgnd udef rtr", 1);
        Note note3 = new Note("Odjeuzd", "Kduejd efdf ef dferfef", 2);
        Note note4 = new Note("Ldjehd", "Ldf dfe dgrgrg fgtujtge", 1);
        String pattern = "grgrg";

        Note[] notes = {
                note1,
                note2,
                note3,
                note4,
        };
        Note[] expectedNoteArray = {
                note4,
                note2,
        };

        MainActivity activity = activityActivityTestRule.getActivity();
        activity.setNoteStorage(new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext(), null)));
        Util.fillNoteStorage(notes, activity);

        onView(withId(R.id.search)).perform(click());
        onView(withId(android.support.design.R.id.search_src_text)).perform(typeText(pattern));
        ListView noteListView = activityActivityTestRule.getActivity().findViewById(R.id.notesList);
        Util.assertNoteArrayContains(noteListView.getAdapter(), expectedNoteArray);
    }


    @Test
    public void checkTrashButtonVisibility() {
        onView(withId(R.id.action_trash)).check(matches(isDisplayed()));
        onView(withId(R.id.action_trash)).check(matches(isClickable()));
    }
}
