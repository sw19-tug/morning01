package at.tugraz.ist.swe.note;

import java.util.Date;

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
    static public void fillNoteStorage(Note[] notes, NoteStorage noteStorage) {
        for (int i = 0; i < notes.length; ++i) {
            Note note = notes[i];
            note.setCreatedDate(makeNextDate());
            noteStorage.insert(note);
        }
    }

}
