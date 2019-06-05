package at.tugraz.ist.swe.note;

import android.widget.ListAdapter;

import java.util.Date;

import at.tugraz.ist.swe.note.database.DatabaseHelper;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class Util {

    static long timestampInMilliseconds = 0;

    static private Date makeNextDate() {
        Date date = new Date(timestampInMilliseconds);
        timestampInMilliseconds += 1000;
        return date;
    }

    static public void assertNoteArrayEquals(Note[] allStoredNotes, Note[] expectedArray){

        assertEquals(expectedArray.length, allStoredNotes.length);
        for (int i = 0; i < allStoredNotes.length; ++i){
            assertTrue(expectedArray[i].equals(allStoredNotes[i]));
        }

    }

    static public void assertNoteArrayContains(ListAdapter adapter, Note[] expectedNotes) {
        assertNoteArrayContains(convertAdapterToNoteArray(adapter), expectedNotes);
    }

    static public void assertNoteArrayContains(Note[] foundNotes, Note[] expectedNotes) {
        for (Note eNote: expectedNotes){
            boolean found = false;
            for(Note fNote: foundNotes){
                if (eNote.equals(fNote)){
                    found = true;
                }
            }
            assertTrue(found);
        }
    }

    static public void assertNoteTagsContains(NoteTag[] foundNoteTags, NoteTag[] expectedNoteTags) {
        int foundCounter = 0;
        for (NoteTag expectedNoteTag: expectedNoteTags){
            boolean found = false;
            for(NoteTag foundNoteTag: foundNoteTags){
                if (expectedNoteTag.equals(foundNoteTag)){
                    found = true;
                    foundCounter++;
                }
            }
            assertTrue(found);
        }
        assertEquals(foundCounter, foundNoteTags.length);
    }

    static public Note[] convertAdapterToNoteArray(ListAdapter adapter) {
        Note[] result = new Note[adapter.getCount()];
        for (int i = 0; i < adapter.getCount(); ++i){
            result[i] = (Note)adapter.getItem(i);
        }
        return result;
    }

    static public void fillNoteStorage(Note[] notes, NoteStorage noteStorage) {
        for (int i = 0; i < notes.length; ++i) {
            Note note = notes[i];
            Date date = makeNextDate();
            note.setCreatedDate(date);
            note.setChangedDate(date);
            noteStorage.insert(note);
        }
    }

    static public void fillNoteTagStorage(NoteTag[] noteTags, NoteTagStorage noteTagStorage) {
        for (int i = 0; i < noteTags.length; ++i){
            noteTagStorage.insert(noteTags[i]);
        }
    }

    static public void fillNoteStorage(Note[] notes, final MainActivity activity) {
        fillNoteStorage(notes, activity.noteStorage);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activity.refreshNoteList();
            }
        });
    }

    static public void resetDatabase(DatabaseHelper databaseHelper) {
        databaseHelper.getWritableDatabase().execSQL("DELETE FROM " + DatabaseHelper.NOTE_TABLE_NAME);
    }

    static public void fillNoteStorage(Note[] notes, final TrashActivity activity) {
        fillNoteStorage(notes, activity.noteStorage);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activity.refreshNoteList();
            }
        });
    }
}
